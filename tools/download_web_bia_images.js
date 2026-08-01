const fs = require("fs");
const path = require("path");

const root = "D:/Java3/DA1_03/web-bia-images";
const sourcePath = path.join(root, "_source", "app.js");
const baseUrl = "https://web-bia.onrender.com";

const app = fs.readFileSync(sourcePath, "utf8");

const boundaries = {
  header: app.indexOf("function N4"),
  hero: app.indexOf("function R4"),
  about: app.indexOf("function M4"),
  services: app.indexOf("function D4"),
  projects: app.indexOf("function H4"),
  products: app.indexOf("function U4"),
  contact: app.indexOf("function B4"),
};

function slug(input) {
  return input
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/Đ/g, "D")
    .replace(/đ/g, "d")
    .replace(/[^a-zA-Z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .toLowerCase();
}

function urlsFrom(text) {
  return [...text.matchAll(/"([^"]+\.(?:jpg|jpeg|png|webp|gif|svg))"/gi)].map((m) => m[1]);
}

function numberedName(index, url) {
  const clean = decodeURIComponent(url.split("?")[0]);
  const ext = path.extname(clean) || ".img";
  const original = path.basename(clean, ext).replace(/[^\w().-]+/g, "_");
  return `${String(index).padStart(2, "0")}_${original}${ext}`;
}

const items = [];
const seen = new Set();

function add(url, folder, label, index) {
  const key = `${folder}|${url}`;
  if (seen.has(key)) return;
  seen.add(key);
  items.push({
    url,
    absoluteUrl: new URL(url, baseUrl).toString(),
    folder,
    label,
    filename: numberedName(index, url),
  });
}

function addSingle(section, url, label, index = 1) {
  add(url, section, label, index);
}

const headerSeg = app.slice(boundaries.header, boundaries.hero);
const heroSeg = app.slice(boundaries.hero, boundaries.about);
const servicesSeg = app.slice(boundaries.services, boundaries.projects);
const projectsSeg = app.slice(boundaries.projects, boundaries.products);
const productsSeg = app.slice(boundaries.products, boundaries.contact);

urlsFrom(headerSeg).forEach((url, i) => addSingle("01-header-logo", url, "Header / Logo", i + 1));
urlsFrom(heroSeg).forEach((url, i) => addSingle("02-hero", url, "Hero", i + 1));

const serviceCardRe = /\{title:"([^"]+)"[\s\S]*?image:"([^"]+\.(?:jpg|jpeg|png|webp|gif|svg))"/gi;
let match;
while ((match = serviceCardRe.exec(servicesSeg))) {
  add(match[2], `03-services/${slug(match[1])}`, `Dịch vụ - ${match[1]}`, 1);
}

const projectRe = /\{id:\d+,name:"([^"]+)",location:"([^"]+)"[\s\S]*?images:\[([^\]]*)\]/g;
while ((match = projectRe.exec(projectsSeg))) {
  const name = match[1];
  const location = match[2];
  urlsFrom(match[3]).forEach((url, i) => {
    add(url, `04-projects/${slug(name)}`, `${name} - ${location}`, i + 1);
  });
}

const productRe = /\{id:\d+,brand:"([^"]+)",name:"([^"]+)"[\s\S]*?images:\[([^\]]*)\]/g;
while ((match = productRe.exec(productsSeg))) {
  const brand = match[1];
  const name = match[2];
  urlsFrom(match[3]).forEach((url, i) => {
    add(url, `05-products/${slug(`${brand}-${name}`)}`, `${brand} - ${name}`, i + 1);
  });
}

async function download(item) {
  const dir = path.join(root, item.folder);
  fs.mkdirSync(dir, { recursive: true });
  const out = path.join(dir, item.filename);
  if (fs.existsSync(out) && fs.statSync(out).size > 0) {
    return { ...item, savedTo: out, skipped: true };
  }
  const response = await fetch(item.absoluteUrl);
  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}: ${item.absoluteUrl}`);
  }
  const buffer = Buffer.from(await response.arrayBuffer());
  fs.writeFileSync(out, buffer);
  return { ...item, savedTo: out, bytes: buffer.length, skipped: false };
}

(async () => {
  const results = [];
  const failures = [];
  const concurrency = 8;
  let cursor = 0;

  async function worker() {
    for (;;) {
      const index = cursor++;
      if (index >= items.length) return;
      try {
        const result = await download(items[index]);
        results.push(result);
        process.stdout.write(`OK ${results.length}/${items.length} ${result.folder}/${result.filename}\n`);
      } catch (error) {
        failures.push({ ...items[index], error: error.message });
        process.stdout.write(`FAIL ${items[index].absoluteUrl} ${error.message}\n`);
      }
    }
  }

  await Promise.all(Array.from({ length: concurrency }, worker));

  const summary = {
    source: baseUrl,
    total: items.length,
    downloaded: results.filter((r) => !r.skipped).length,
    skipped: results.filter((r) => r.skipped).length,
    failed: failures.length,
    sections: {},
    files: results.sort((a, b) => a.folder.localeCompare(b.folder) || a.filename.localeCompare(b.filename)),
    failures,
  };

  for (const item of results) {
    const section = item.folder.split("/")[0];
    summary.sections[section] = (summary.sections[section] || 0) + 1;
  }

  fs.writeFileSync(path.join(root, "manifest.json"), JSON.stringify(summary, null, 2), "utf8");
  console.log(JSON.stringify({ total: summary.total, downloaded: summary.downloaded, skipped: summary.skipped, failed: summary.failed, sections: summary.sections }, null, 2));

  if (failures.length) process.exitCode = 1;
})();
