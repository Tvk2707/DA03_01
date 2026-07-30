# Template execution contract — RIOR SD-26, Chương 1–2

## Reference

- Retained reference: `C:\Users\Admin\Downloads\Bản sao của SD-21.docx`
- SHA-256: `5907c99afbf17865fb89db25d603ed57c7ab83e827f2a6adc7a518bc288bd7dd`
- Size: 1,980,468 bytes; observed in Microsoft Word as 36 pages.
- Package inventory: `D:\Java3\DA1_03\.codex-doc-work\rior-report\package-inventory.json` (40 parts, SHA-256 per part).
- Style evidence: `D:\Java3\DA1_03\.codex-doc-work\rior-report\template-style-evidence.json`.
- Render evidence: unavailable because the bundled environment has no LibreOffice/`soffice`; Microsoft Word observation confirmed the cover/page count. Structural audits are authoritative for this run.
- Sections: 5, all A4 portrait (8.27 × 11.69 in), all start on a new page.

## Page system

- Margins in every section: left 1.38 in, right 0.79 in, top 0.98 in, bottom 0.79 in.
- Section 1 has a different first page. Sections 2–5 use separate headers and link their footers to the preceding section.
- Preserve all five section breaks, page sizes, margins, header/footer distances, and link-to-previous behavior.
- PAGE fields exist only in `word/header2.xml` and `word/header6.xml`; preserve both fields and set `w:updateFields=true` after editing.

## Typography and paragraph roles

- The reference uses primarily `Normal`, `Heading 1`, `Heading 2`, `Heading 3`, `Heading 5`, and `Heading 6`, plus extensive direct formatting. Preserve each source paragraph's style and paragraph properties while replacing only its text runs.
- Body paragraphs: justified where already set, 1.3 line spacing, 3 pt before/after in the report body; inherited source font/size, visually 13 pt for most content.
- Cover report title: centered, bold, 18 pt. Cover topic and location/year: centered and bold using the source run properties.
- Main front-matter headings: `Heading 1`, centered, bold, 1.3 line spacing, 3 pt before/after.
- Chapter title and section headings: preserve the source's current `Heading 1`/`Heading 2` role, alignment, boldness, spacing, and keep behavior.
- Figure captions: reuse `Heading 5`, centered/italic source treatment. Table captions: reuse `Heading 6`, centered/bold source treatment.
- Do not normalize the source's intentionally direct-formatted title system or apply a generic design preset.

## Lists and tables

- Preserve all existing table styles, borders, fills, cell margins, alignment, and explicit column grids. Resize by cloning or removing source rows only.
- Table 0: cover metadata; expand student area from four to five students by cloning the last student row.
- Table 1: document-format conventions; preserve unchanged.
- Table 2: glossary header; preserve and leave ready for later glossary entries.
- Table 3: member directory; expand to one header plus five students; phone/email cells remain `—` because the user did not provide them.
- Table 4: version record; rewrite project name/date/version.
- Table 5: resource assignment; expand to one header plus five members.
- Table 6: plan; resize to one header plus 20 supplied tasks, preserving the six-column grid and header row.
- Table 7: survey summary; retain five functional rows but explicitly mark the source as pending field-survey confirmation. Do not present invented stores or findings.
- Table 8: use cases; resize to one header plus 12 source-supported use cases for the Manager/Employee actors.
- Table 9: entity list; expand to one header plus 25 JPA entities found in the project source.
- No fixed row heights may be introduced. Existing widths and wrapping rules remain the layout authority.

## Components and package preservation

- Preserve the blue ornamental cover border, FPT Polytechnic logo, recurring header logos, header/footer drawings, page-number fields, bookmarks, numbering, relationships, and all media parts byte-for-byte.
- Preserve the 20 drawing occurrences (9 inline, 11 anchored) and their relationship IDs. Images are temporary placeholders by explicit user request.
- Replace the footer text naming FourSpiceHotpot with the official RIOR topic, without touching its page-number fields or drawing objects.
- Replace the eight stale restaurant-related footnote bodies with explicit temporary-source notes; preserve footnote IDs and references so anchors do not break.
- No comments or content controls exist. Eight footnotes exist. Endnotes are empty.

## Content flow and slot map

- `word/document.xml`, cover paragraphs 3–4 and table 0: official title, SD-26, advisor, five students.
- Front matter paragraphs 47–53, 80–111 and tables 3–5: acknowledgements, introduction, summary, member and project metadata rewritten for RIOR.
- Chapter 1 paragraphs 112–226 and tables 6–7: context, objectives/scope, two temporary figure captions, resources, supplied plan, and survey placeholders.
- Chapter 2 paragraphs 227–263 and tables 8–9: source-supported use cases, actors, entity descriptions, and temporary total-use-case/ERD image captions.
- Paragraphs before Chapter 1 not listed above retain their structural purpose. Material after paragraph 263 does not exist in this reference and is out of scope.
- Unsupported facts (student phones/emails and actual survey stores/addresses/findings) must remain visibly pending, never fabricated.

## Fidelity gates

- The retained reference must still hash to the recorded SHA-256 before and after the build.
- Preserve-only package parts must match the inventory except for intended edits to `word/document.xml`, relevant footer/header text parts, `word/footnotes.xml`, `word/settings.xml`, and package metadata if Word updates it later.
- Section count, page geometry, relationships, media hashes, drawing counts, field counts, footnote IDs, and bookmark count must remain unchanged.
- Structural QA must confirm no FourSpiceHotpot/restaurant/booking/menu/chatbot terminology remains in editable report content except explicit placeholder-image notes.
- Visual render QA is deferred because `soffice` is unavailable; disclose this at delivery and avoid claiming a rendered pass.
