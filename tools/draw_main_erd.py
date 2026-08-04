from pathlib import Path
from PIL import Image, ImageDraw, ImageFont


OUT = Path(__file__).resolve().parents[1] / "so_do_quan_he_thuc_the_chinh_RIOR.png"
W, H = 2600, 1550


def font(size, bold=False, italic=False):
    base = Path(r"C:\Windows\Fonts")
    if bold and italic:
        name = "timesbi.ttf"
    elif bold:
        name = "timesbd.ttf"
    elif italic:
        name = "timesi.ttf"
    else:
        name = "times.ttf"
    return ImageFont.truetype(str(base / name), size)


img = Image.new("RGB", (W, H), "white")
d = ImageDraw.Draw(img)

TITLE = font(49, bold=True)
ENTITY_FONT = font(30)
REL_FONT = font(25)
CARD_FONT = font(24)
NOTE_FONT = font(24, italic=True)

entity_fill = "#f4cfcc"
entity_border = "#b76560"
line_color = "#353535"
section_color = "#88615e"


def center_text(box, text, fnt, fill="#181818"):
    x1, y1, x2, y2 = box
    bb = d.textbbox((0, 0), text, font=fnt)
    tw, th = bb[2] - bb[0], bb[3] - bb[1]
    d.text(((x1 + x2 - tw) / 2, (y1 + y2 - th) / 2 - 3), text, font=fnt, fill=fill)


nodes = {
    "Khách hàng": (120, 250, 440, 340),
    "Nhân viên": (120, 735, 440, 825),
    "Hóa đơn": (1110, 530, 1430, 620),
    "Phiếu giảm giá": (2130, 250, 2480, 340),
    "Thanh toán hóa đơn": (2070, 700, 2530, 790),
    "Chi tiết hóa đơn": (1080, 1000, 1460, 1090),
    "Sản phẩm": (250, 1240, 570, 1330),
    "Sản phẩm chi tiết": (1900, 1240, 2320, 1330),
}


def entity(name):
    box = nodes[name]
    d.rectangle(box, fill=entity_fill, outline=entity_border, width=2)
    center_text(box, name, ENTITY_FONT)


def anchor(name, side):
    x1, y1, x2, y2 = nodes[name]
    return {
        "left": (x1, (y1 + y2) / 2),
        "right": (x2, (y1 + y2) / 2),
        "top": ((x1 + x2) / 2, y1),
        "bottom": ((x1 + x2) / 2, y2),
    }[side]


def diamond(cx, cy, label, width=190, height=105):
    pts = [(cx, cy - height / 2), (cx + width / 2, cy),
           (cx, cy + height / 2), (cx - width / 2, cy)]
    d.polygon(pts, fill="white", outline=line_color)
    center_text((cx - width / 2, cy - height / 2,
                 cx + width / 2, cy + height / 2), label, REL_FONT)
    return pts


def line(points):
    d.line(points, fill=line_color, width=3, joint="curve")


def card(x, y, value):
    bb = d.textbbox((0, 0), value, font=CARD_FONT)
    pad = 4
    d.rectangle((x - pad, y - pad, x + (bb[2] - bb[0]) + pad,
                 y + (bb[3] - bb[1]) + pad), fill="white")
    d.text((x, y), value, font=CARD_FONT, fill=line_color)


# Title and project subtitle
d.text((95, 62), "2.2.2. Sơ đồ quan hệ thực thể", font=TITLE, fill="#111111")
d.text((1720, 75), "Website bán kính thời trang RIOR", font=NOTE_FONT, fill=section_color)
d.line((95, 133, 2505, 133), fill="#d8b3b0", width=2)

# Draw relationship lines first so entity boxes remain visually clean.
# Khách hàng -- Mua -- Hóa đơn
line([anchor("Khách hàng", "right"), (690, 295)])
line([(810, 332), anchor("Hóa đơn", "top")])
diamond(750, 315, "Mua", 165, 95)
card(470, 267, "1")
card(1048, 468, "N")

# Nhân viên -- Lập -- Hóa đơn
line([anchor("Nhân viên", "right"), (700, 780)])
line([(820, 750), anchor("Hóa đơn", "bottom")])
diamond(760, 765, "Lập", 165, 95)
card(470, 748, "1")
card(1045, 635, "N")

# Phiếu giảm giá -- Áp dụng -- Hóa đơn
line([anchor("Phiếu giảm giá", "left"), (1860, 295)])
line([(1740, 335), anchor("Hóa đơn", "top")])
diamond(1800, 315, "Áp dụng", 210, 105)
card(2070, 266, "1")
card(1455, 468, "N")

# Hóa đơn -- Thanh toán -- Thanh toán hóa đơn
line([anchor("Hóa đơn", "right"), (1710, 575)])
line([(1830, 630), anchor("Thanh toán hóa đơn", "left")])
diamond(1770, 605, "Thanh toán", 245, 110)
card(1460, 545, "1")
card(2005, 690, "N")

# Hóa đơn -- Gồm -- Chi tiết hóa đơn
line([anchor("Hóa đơn", "bottom"), (1270, 760)])
line([(1270, 865), anchor("Chi tiết hóa đơn", "top")])
diamond(1270, 812, "Gồm", 170, 100)
card(1295, 645, "1")
card(1295, 945, "N")

# Chi tiết hóa đơn -- Chứa -- Sản phẩm chi tiết
line([anchor("Chi tiết hóa đơn", "right"), (1660, 1045)])
line([(1775, 1100), anchor("Sản phẩm chi tiết", "top")])
diamond(1715, 1072, "Chứa", 180, 100)
card(1490, 1015, "N")
card(1855, 1177, "1")

# Sản phẩm -- Có biến thể -- Sản phẩm chi tiết
line([anchor("Sản phẩm", "right"), (1120, 1285)])
line([(1330, 1285), anchor("Sản phẩm chi tiết", "left")])
diamond(1225, 1285, "Có biến thể", 280, 115)
card(600, 1255, "1")
card(1840, 1255, "N")

# Connect the product variant to invoice detail in a clearer vertical path.
# (The relationship above already represents this through "Chứa".)

for name in nodes:
    entity(name)

# Small legend
d.text((95, 1462), "Ký hiệu: 1 = một; N = nhiều. Sơ đồ chỉ thể hiện các thực thể và quan hệ nghiệp vụ chính.",
       font=NOTE_FONT, fill="#444444")

# Chỉ xuất riêng phần sơ đồ, loại bỏ tiêu đề và chú thích theo yêu cầu.
img = img.crop((60, 165, 2550, 1390))
img.save(OUT, dpi=(220, 220), optimize=True)
print(OUT)
