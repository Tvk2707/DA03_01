from __future__ import annotations

import hashlib
import sys
from copy import deepcopy
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile

from lxml import etree


W_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
DC_NS = "http://purl.org/dc/elements/1.1/"
NS = {"w": W_NS, "dc": DC_NS}
W = f"{{{W_NS}}}"

REFERENCE_SHA256 = "5907c99afbf17865fb89db25d603ed57c7ab83e827f2a6adc7a518bc288bd7dd"


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def set_text_nodes(container: etree._Element, text: str) -> None:
    nodes = container.xpath(".//w:t", namespaces=NS)
    if not nodes:
        paragraphs = container.xpath(".//w:p", namespaces=NS)
        paragraph = paragraphs[0] if paragraphs else etree.SubElement(container, W + "p")
        run = etree.SubElement(paragraph, W + "r")
        node = etree.SubElement(run, W + "t")
        nodes = [node]
    nodes[0].text = text
    if text.startswith(" ") or text.endswith(" "):
        nodes[0].set("{http://www.w3.org/XML/1998/namespace}space", "preserve")
    for node in nodes[1:]:
        node.text = ""


def set_paragraph_text(paragraphs: list[etree._Element], index: int, text: str) -> None:
    set_text_nodes(paragraphs[index], text)


def remove_paragraphs(paragraphs: list[etree._Element], indices: list[int]) -> None:
    for index in sorted(indices, reverse=True):
        paragraph = paragraphs[index]
        if paragraph.xpath(".//w:drawing|.//w:pict|.//w:sectPr", namespaces=NS):
            raise RuntimeError(f"Refusing to remove protected paragraph P{index}")
        paragraph.getparent().remove(paragraph)


def table_rows(table: etree._Element) -> list[etree._Element]:
    return table.xpath("./w:tr", namespaces=NS)


def resize_table(table: etree._Element, count: int) -> list[etree._Element]:
    rows = table_rows(table)
    if not rows:
        raise RuntimeError("Cannot resize an empty table")
    while len(rows) < count:
        clone = deepcopy(rows[-1])
        table.append(clone)
        rows.append(clone)
    while len(rows) > count:
        table.remove(rows.pop())
    return rows


def set_row(row: etree._Element, values: list[str]) -> None:
    cells = row.xpath("./w:tc", namespaces=NS)
    if len(cells) != len(values):
        raise RuntimeError(f"Expected {len(cells)} values, received {len(values)}")
    for cell, value in zip(cells, values):
        set_text_nodes(cell, value)


def fill_table(table: etree._Element, rows_data: list[list[str]]) -> None:
    rows = resize_table(table, len(rows_data))
    for row, values in zip(rows, rows_data):
        set_row(row, values)


def build(reference: Path, output: Path) -> None:
    actual_hash = sha256_file(reference)
    if actual_hash != REFERENCE_SHA256:
        raise RuntimeError(
            f"Reference hash mismatch: expected {REFERENCE_SHA256}, got {actual_hash}"
        )

    with ZipFile(reference, "r") as source:
        parts = {name: source.read(name) for name in source.namelist()}
        infos = {info.filename: info for info in source.infolist()}

    document = etree.fromstring(parts["word/document.xml"])
    body = document.xpath("/w:document/w:body", namespaces=NS)[0]
    paragraphs = body.xpath("./w:p", namespaces=NS)
    tables = body.xpath("./w:tbl", namespaces=NS)
    if len(paragraphs) != 264 or len(tables) != 10:
        raise RuntimeError("Reference structure no longer matches the distilled slot map")

    paragraph_updates = {
        4: "ĐỀ TÀI: XÂY DỰNG WEBSITE BÁN KÍNH THỜI TRANG RIOR",
        48: "Đề tài “Xây dựng website bán kính thời trang RIOR” là kết quả của quá trình học tập, nghiên cứu và vận dụng kiến thức chuyên ngành Phát triển phần mềm vào một bài toán quản lý bán lẻ thực tế. Trong quá trình thực hiện, nhóm đã khảo sát nghiệp vụ, phân tích yêu cầu, thiết kế dữ liệu, xây dựng các chức năng quản trị và kiểm thử hệ thống nhằm hoàn thiện sản phẩm.",
        49: "Trước hết, nhóm xin gửi lời cảm ơn chân thành đến cô Nguyễn Thúy Hằng - giảng viên hướng dẫn - đã tận tình theo sát tiến độ, định hướng phương pháp thực hiện và đóng góp nhiều ý kiến chuyên môn trong suốt quá trình phát triển dự án. Những góp ý của cô giúp nhóm nhận diện vấn đề, điều chỉnh giải pháp và hoàn thiện hệ thống đúng mục tiêu đề ra.",
        50: "Nhóm xin chân thành cảm ơn quý thầy cô Bộ môn Phát triển phần mềm, Trường Cao đẳng FPT Polytechnic đã truyền đạt các kiến thức về lập trình Java Web, phân tích thiết kế hệ thống, cơ sở dữ liệu, kiểm thử phần mềm và quản lý dự án. Đây là nền tảng quan trọng để nhóm xây dựng hệ thống RIOR theo mô hình ứng dụng Web nhiều tầng.",
        51: "Nhóm cũng cảm ơn các thành viên đã phối hợp trong việc phân tích nghiệp vụ, thiết kế cơ sở dữ liệu, lập trình các phân hệ, tích hợp và kiểm thử. Tinh thần làm việc nhóm và việc thường xuyên trao đổi mã nguồn đã giúp dự án được hoàn thành theo kế hoạch.",
        52: "Mặc dù đã cố gắng hoàn thiện sản phẩm, báo cáo vẫn có thể còn thiếu sót do giới hạn về thời gian và kinh nghiệm thực tế. Nhóm mong nhận được ý kiến đóng góp của giảng viên để tiếp tục cải thiện nội dung và chất lượng hệ thống.",
        53: "Nhóm xin trân trọng cảm ơn!",
        81: "Trong bối cảnh chuyển đổi số diễn ra mạnh mẽ, các cửa hàng bán lẻ ngày càng cần một hệ thống tập trung để quản lý sản phẩm, tồn kho, khách hàng, nhân viên và giao dịch. Đối với ngành kính mắt thời trang, mỗi mẫu kính có thể có nhiều biến thể về màu sắc, kích cỡ, chất liệu, kiểu dáng, gọng kính và tròng kính, khiến việc quản lý thủ công dễ phát sinh sai lệch dữ liệu.",
        82: "Tại nhiều cửa hàng, thông tin sản phẩm và hóa đơn vẫn được ghi nhận trên sổ sách hoặc các tệp rời rạc. Việc tra cứu chậm, tồn kho thiếu đồng bộ, khó kiểm soát giá bán và áp dụng khuyến mãi có thể ảnh hưởng trực tiếp đến tốc độ phục vụ cũng như độ chính xác khi thanh toán tại quầy.",
        83: "Xuất phát từ nhu cầu đó, nhóm thực hiện đề tài “Xây dựng website bán kính thời trang RIOR”. Hệ thống hướng đến việc số hóa quy trình quản lý và bán hàng tại cửa hàng kính mắt, giúp dữ liệu được lưu trữ tập trung, cập nhật nhất quán và hỗ trợ người dùng thao tác thuận tiện.",
        84: "Đối với nhân viên, hệ thống hỗ trợ đăng nhập, tìm kiếm hoặc quét mã sản phẩm, tạo hóa đơn chờ, chọn khách hàng, áp dụng phiếu giảm giá, thanh toán và in hóa đơn tại quầy.",
        85: "Đối với quản lý, hệ thống cung cấp các chức năng quản trị sản phẩm và biến thể, thuộc tính kính, hóa đơn, khách hàng, nhân viên, chương trình giảm giá và báo cáo thống kê doanh thu.",
        86: "Thông qua dự án, nhóm có cơ hội vận dụng kiến thức Java Web, Jakarta Servlet, JSP/JSTL, Hibernate/JPA, JDBC và SQL Server; đồng thời rèn luyện kỹ năng phân tích nghiệp vụ, phân công công việc, tích hợp mã nguồn và kiểm thử.",
        87: "Trong phạm vi tài liệu hiện tại, báo cáo trình bày hai chương:",
        88: "Chương 1: Giới thiệu bối cảnh, mục tiêu, phạm vi, nguồn lực, kế hoạch và nội dung khảo sát của dự án.",
        89: "Chương 2: Phân tích tác nhân, danh sách chức năng, use case và các thực thể dữ liệu của hệ thống RIOR.",
        96: "Thông qua báo cáo này, nhóm trình bày nền tảng phân tích và thiết kế cho hệ thống RIOR, làm cơ sở để đối chiếu giữa yêu cầu nghiệp vụ với các chức năng đã triển khai trong mã nguồn.",
        98: "Dự án “Xây dựng website bán kính thời trang RIOR” được thực hiện nhằm hỗ trợ hoạt động quản lý và bán hàng tại cửa hàng kính mắt. Hệ thống giải quyết nhu cầu quản lý tập trung dữ liệu sản phẩm, biến thể, tồn kho, khách hàng, nhân viên, hóa đơn và chương trình ưu đãi.",
        99: "Mục tiêu của RIOR là chuẩn hóa quy trình bán hàng tại quầy, giảm sai sót khi lập và thanh toán hóa đơn, đồng thời cung cấp dữ liệu thống kê phục vụ công tác quản lý.",
        100: "Hệ thống được xây dựng cho hai nhóm người dùng nội bộ là Quản lý và Nhân viên. Quyền truy cập được xác định theo vai trò sau khi đăng nhập.",
        101: "Các nhóm chức năng chính của hệ thống gồm:",
        102: "Bán hàng tại quầy: tạo hóa đơn chờ, tìm kiếm hoặc quét mã sản phẩm, cập nhật giỏ hàng và lựa chọn khách hàng.",
        103: "Thanh toán: áp dụng phiếu giảm giá, chọn phương thức thanh toán, xác nhận giao dịch, cập nhật tồn kho và trạng thái hóa đơn.",
        104: "Quản lý sản phẩm và biến thể theo màu sắc, kích cỡ, giá bán, số lượng tồn kho, hình ảnh và trạng thái kinh doanh.",
        105: "Quản lý các thuộc tính kính gồm danh mục, thương hiệu, chất liệu, kiểu dáng, màu sắc, kích cỡ, tròng kính và gọng kính.",
        106: "Quản lý hóa đơn, chi tiết hóa đơn, lịch sử xử lý và lịch sử thanh toán.",
        107: "Quản lý thông tin khách hàng, địa chỉ khách hàng, nhân viên và tài khoản đăng nhập.",
        108: "Quản lý phiếu giảm giá, điều kiện áp dụng, thời gian hiệu lực, số lượng và đối tượng nhận ưu đãi.",
        109: "Thống kê doanh thu, hóa đơn, sản phẩm bán chạy, khách hàng nổi bật và các mặt hàng tồn chậm.",
        110: "Về công nghệ, hệ thống sử dụng Java 17, Jakarta Servlet 6, JSP/JSTL, Hibernate 6.4/JPA kết hợp JDBC, SQL Server và Maven để đóng gói ứng dụng dạng WAR.",
        111: "Kết quả của dự án là một hệ thống quản lý bán kính có khả năng hỗ trợ xuyên suốt từ quản trị dữ liệu sản phẩm đến hoàn tất giao dịch tại quầy, góp phần nâng cao hiệu quả vận hành và khả năng kiểm soát dữ liệu của cửa hàng.",
        114: "Trong lĩnh vực bán lẻ kính mắt, số lượng mẫu mã và biến thể sản phẩm thường lớn. Một sản phẩm có thể được phân loại theo danh mục, thương hiệu, chất liệu, kiểu dáng, gọng kính, tròng kính, màu sắc và kích cỡ. Nếu dữ liệu không được quản lý thống nhất, nhân viên dễ nhầm lẫn khi tra cứu, báo giá và kiểm tra tồn kho.",
        115: "Quy trình bán hàng thủ công còn làm phát sinh nhiều hạn chế trong việc tạo hóa đơn, lựa chọn khách hàng, áp dụng khuyến mãi, ghi nhận phương thức thanh toán và đối soát doanh thu.",
        116: "Thông tin sản phẩm và biến thể có thể bị trùng mã, sai giá hoặc không phản ánh đúng số lượng tồn thực tế.",
        117: "Nhân viên mất nhiều thời gian khi tìm sản phẩm, lập hóa đơn và tính toán số tiền khách cần thanh toán.",
        118: "Dữ liệu khách hàng, nhân viên, hóa đơn và ưu đãi phân tán khiến việc tra cứu và kiểm soát lịch sử gặp khó khăn.",
        119: "Báo cáo doanh thu và sản phẩm bán chạy khó được tổng hợp kịp thời nếu phải xử lý thủ công.",
        120: "Hệ thống RIOR được xây dựng để tập trung hóa dữ liệu, tự động hóa các bước bán hàng tại quầy và cung cấp công cụ quản trị phù hợp với cửa hàng kính mắt.",
        121: "Quản lý có quyền theo dõi thống kê và quản trị các dữ liệu cốt lõi như sản phẩm, hóa đơn, khách hàng, nhân viên và chương trình giảm giá.",
        122: "Nhân viên sử dụng hệ thống để đăng nhập, tra cứu sản phẩm, hỗ trợ khách hàng, lập hóa đơn và thực hiện thanh toán tại quầy theo quyền được cấp.",
        123: "Việc triển khai hệ thống giúp giảm thao tác lặp, hạn chế sai sót và tạo nền tảng dữ liệu nhất quán cho hoạt động kinh doanh.",
        126: "Mục tiêu của dự án RIOR là xây dựng một website quản lý và bán kính thời trang, hỗ trợ cửa hàng kiểm soát dữ liệu sản phẩm, tồn kho và giao dịch tại quầy trên cùng một hệ thống.",
        127: "Phạm vi tập trung vào các nghiệp vụ nội bộ dành cho Quản lý và Nhân viên; không bao gồm chức năng đặt hàng trực tuyến hay tài khoản mua hàng dành cho khách hàng cuối.",
        128: "Phía quản trị và vận hành: Hệ thống cung cấp các nhóm chức năng chính sau:",
        129: "Lập hóa đơn bán hàng tại quầy: Nhân viên có thể tạo hóa đơn chờ, tìm kiếm hoặc quét mã sản phẩm, lựa chọn biến thể, thêm sản phẩm, cập nhật số lượng, chọn khách hàng và áp dụng phiếu giảm giá hợp lệ.",
        130: "Thanh toán hóa đơn tại quầy: Hệ thống hỗ trợ tiền mặt hoặc chuyển khoản, kiểm tra số tiền, xác nhận giao dịch, cập nhật trạng thái hóa đơn, ghi nhận lịch sử thanh toán và trừ tồn kho.",
        131: "Quản lý sản phẩm: Cho phép thêm, sửa, tìm kiếm và cập nhật mã, tên, danh mục, thương hiệu, chất liệu, kiểu dáng, gọng kính, tròng kính, hình ảnh và trạng thái kinh doanh.",
        132: "Quản lý biến thể và thuộc tính: Mỗi sản phẩm có thể có nhiều biến thể màu sắc và kích cỡ với mã, giá nhập, giá bán, số lượng tồn, trọng lượng, hình ảnh và trạng thái riêng; hệ thống kiểm tra để tránh tổ hợp trùng lặp.",
        133: "Quản lý hóa đơn: Cho phép tra cứu danh sách, xem chi tiết, theo dõi trạng thái, lịch sử hóa đơn, lịch sử thanh toán và thông tin nhân viên xử lý.",
        134: "Quản lý khách hàng: Lưu trữ, tìm kiếm và cập nhật họ tên, số điện thoại, email, ngày sinh, giới tính, trạng thái và các địa chỉ liên quan.",
        135: "Quản lý nhân viên: Quản lý hồ sơ, thông tin liên hệ, tài khoản đăng nhập, vai trò Quản lý/Nhân viên và trạng thái làm việc.",
        136: "Quản lý phiếu và đợt giảm giá: Thiết lập mã, tên, loại giảm, giá trị giảm, đơn hàng tối thiểu, số lượng, thời gian hiệu lực, phạm vi áp dụng và trạng thái.",
        137: "Thống kê - báo cáo: Tổng hợp doanh thu theo khoảng thời gian, tỷ lệ hoàn thành hóa đơn, sản phẩm bán chạy, khách hàng nổi bật và sản phẩm tồn chậm.",
        138: "Đăng nhập và phân quyền: Xác thực bằng email hoặc mã nhân viên, kiểm tra trạng thái tài khoản và điều hướng giao diện theo vai trò.",
        139: "Phạm vi kỹ thuật: Ứng dụng được phát triển theo mô hình MVC, triển khai trên máy chủ hỗ trợ Jakarta Servlet và kết nối cơ sở dữ liệu SQL Server.",
        154: "Hình 1.1: Activity Diagram bán hàng tại quầy (tạm giữ sơ đồ mẫu)",
        156: "Hình 1.2: Activity Diagram thanh toán hóa đơn tại quầy (tạm giữ sơ đồ mẫu)",
        170: "1.4.1. Khảo sát thực tế tại cửa hàng kính số 1 (chưa bổ sung dữ liệu)",
        171: "Địa điểm: [CẦN BỔ SUNG TÊN VÀ ĐỊA CHỈ CỬA HÀNG]",
        172: "Hình ảnh khảo sát: Tạm giữ ảnh mẫu; nhóm sẽ thay bằng ảnh khảo sát thực tế.",
        174: "Hình 1.3: Hình ảnh khảo sát cửa hàng kính số 1 (tạm giữ ảnh mẫu)",
        175: "Kết luận khảo sát: [CẦN BỔ SUNG SAU KHI NHÓM XÁC NHẬN DỮ LIỆU]",
        176: "Bán hàng tại quầy: [CẦN BỔ SUNG QUY TRÌNH THỰC TẾ]",
        177: "Quản lý sản phẩm và tồn kho: [CẦN BỔ SUNG]",
        178: "Quản lý hóa đơn và thanh toán: [CẦN BỔ SUNG]",
        179: "Chương trình giảm giá: [CẦN BỔ SUNG]",
        180: "Thống kê và báo cáo: [CẦN BỔ SUNG]",
        181: "1.4.2. Khảo sát thực tế tại cửa hàng kính số 2 (chưa bổ sung dữ liệu)",
        182: "Địa điểm: [CẦN BỔ SUNG TÊN VÀ ĐỊA CHỈ CỬA HÀNG]",
        183: "Hình ảnh khảo sát: Tạm giữ ảnh mẫu; nhóm sẽ thay bằng ảnh khảo sát thực tế.",
        185: "Hình 1.4: Hình ảnh khảo sát cửa hàng kính số 2 (tạm giữ ảnh mẫu)",
        186: "Kết luận khảo sát: [CẦN BỔ SUNG SAU KHI NHÓM XÁC NHẬN DỮ LIỆU]",
        187: "Bán hàng tại quầy: [CẦN BỔ SUNG QUY TRÌNH THỰC TẾ]",
        188: "Quản lý sản phẩm và tồn kho: [CẦN BỔ SUNG]",
        189: "Quản lý hóa đơn và thanh toán: [CẦN BỔ SUNG]",
        190: "Chương trình giảm giá: [CẦN BỔ SUNG]",
        191: "Thống kê và báo cáo: [CẦN BỔ SUNG]",
        192: "1.4.3. Khảo sát thực tế tại cửa hàng kính số 3 (chưa bổ sung dữ liệu)",
        193: "Địa điểm: [CẦN BỔ SUNG TÊN VÀ ĐỊA CHỈ CỬA HÀNG]",
        194: "Hình ảnh khảo sát: Tạm giữ ảnh mẫu; nhóm sẽ thay bằng ảnh khảo sát thực tế.",
        196: "Hình 1.5: Hình ảnh khảo sát cửa hàng kính số 3 (tạm giữ ảnh mẫu)",
        197: "Kết luận khảo sát: [CẦN BỔ SUNG SAU KHI NHÓM XÁC NHẬN DỮ LIỆU]",
        198: "Bán hàng tại quầy: [CẦN BỔ SUNG QUY TRÌNH THỰC TẾ]",
        199: "Quản lý sản phẩm và tồn kho: [CẦN BỔ SUNG]",
        200: "Quản lý hóa đơn và thanh toán: [CẦN BỔ SUNG]",
        201: "Chương trình giảm giá: [CẦN BỔ SUNG]",
        202: "Thống kê và báo cáo: [CẦN BỔ SUNG]",
        203: "1.4.4. Khảo sát online các hệ thống quản lý bán lẻ (chưa bổ sung nguồn)",
        204: "Địa điểm: Online",
        205: "Kết luận khảo sát: [CẦN BỔ SUNG TÊN HỆ THỐNG, ĐƯỜNG DẪN VÀ NGÀY TRUY CẬP]",
        206: "Các tiêu chí dự kiến đối chiếu gồm:",
        207: "Quy trình bán hàng tại quầy và thanh toán.",
        208: "Quản lý sản phẩm, biến thể và tồn kho.",
        209: "Quản lý hóa đơn, khách hàng và nhân viên.",
        210: "Quản lý phiếu giảm giá và chương trình ưu đãi.",
        211: "Thống kê doanh thu và báo cáo hoạt động kinh doanh.",
        224: "Tổng hợp kết quả khảo sát theo nhóm chức năng (chờ nhóm bổ sung dữ liệu xác thực)",
        225: "Bảng 1.4: Bảng tổng hợp kết quả khảo sát",
        230: "Hình 2.1: Use case tổng hệ thống RIOR (tạm giữ sơ đồ mẫu)",
        238: "Bảng 2.1: Danh sách các Use Case",
        255: "Bảng 2.2: Danh sách thực thể",
        262: "Hình 2.2: Sơ đồ quan hệ thực thể hệ thống RIOR (tạm giữ sơ đồ mẫu)",
    }
    for index, text in paragraph_updates.items():
        set_paragraph_text(paragraphs, index, text)

    remove_paragraphs(
        paragraphs,
        [90, 91, 92, 93, 94, 95, 124, 140, 141, 142, 143, 144, 145, 146, 147,
         148, 149, 150, 151, 152, 166, 167, 168, 212, 213, 214, 215, 216,
         217, 218, 219, 220, 221, 222, 223],
    )

    fill_table(tables[0], [
        ["Giảng viên hướng dẫn:", "Nguyễn Thúy Hằng", ""],
        ["Chuyên ngành:", "Phát triển phần mềm", ""],
        ["Nhóm thực hiện:", "SD-26", ""],
        ["Sinh viên thực hiện:", "Trần Văn Khánh", "PH64736"],
        ["", "Thẩm Anh Minh", "PH64693"],
        ["", "Vũ Tiến Nam", "PH64895"],
        ["", "Nguyễn Tiến Bách", "PH65600"],
        ["", "Đỗ Vạn Thành", "PH65510"],
    ])

    fill_table(tables[3], [
        ["STT", "Họ tên", "MSSV", "SĐT", "Email"],
        ["1", "Trần Văn Khánh", "PH64736", "—", "—"],
        ["2", "Thẩm Anh Minh", "PH64693", "—", "—"],
        ["3", "Vũ Tiến Nam", "PH64895", "—", "—"],
        ["4", "Nguyễn Tiến Bách", "PH65600", "—", "—"],
        ["5", "Đỗ Vạn Thành", "PH65510", "—", "—"],
    ])

    fill_table(tables[4], [
        ["Tên", "Ngày", "Lý do thay đổi", "Phiên bản"],
        ["Xây dựng website bán kính thời trang RIOR", "29/07/2026", "Cập nhật nội dung Chương 1–2", "1.0"],
    ])

    fill_table(tables[5], [
        ["STT", "Họ và tên", "Nhiệm vụ"],
        ["1", "Trần Văn Khánh", "Thiết kế Use Case, Database; quản lý sản phẩm; bán hàng tại quầy; tích hợp hệ thống"],
        ["2", "Thẩm Anh Minh", "Thiết kế ERD; quản lý hóa đơn"],
        ["3", "Vũ Tiến Nam", "Quản lý phiếu giảm giá và đợt giảm giá"],
        ["4", "Nguyễn Tiến Bách", "Thiết kế giao diện Admin; đăng nhập/đăng ký; quản lý nhân viên"],
        ["5", "Đỗ Vạn Thành", "Quản lý khách hàng; thống kê"],
    ])

    plan = [
        ["1", "Chọn đề tài", "09/07/2026", "29/07/2026", "Cả nhóm", "100%"],
        ["2", "Lập kế hoạch thực hiện dự án", "09/07/2026", "29/07/2026", "Cả nhóm", "100%"],
        ["3", "Khảo sát dự án", "09/07/2026", "29/07/2026", "Cả nhóm", "100%"],
        ["4", "Xác định yêu cầu nghiệp vụ dự án", "09/07/2026", "29/07/2026", "Cả nhóm", "100%"],
        ["5", "Phân tích luồng chức năng", "09/07/2026", "29/07/2026", "Cả nhóm", "100%"],
        ["6", "Thiết kế sơ đồ ERD", "09/07/2026", "29/07/2026", "Thẩm Anh Minh", "100%"],
        ["7", "Thiết kế Use Case", "09/07/2026", "29/07/2026", "Trần Văn Khánh", "100%"],
        ["8", "Thiết kế Database", "09/07/2026", "29/07/2026", "Trần Văn Khánh", "100%"],
        ["9", "Thiết kế giao diện Admin", "09/07/2026", "29/07/2026", "Nguyễn Tiến Bách", "100%"],
        ["10", "Code chức năng đăng nhập, đăng ký", "09/07/2026", "29/07/2026", "Nguyễn Tiến Bách", "100%"],
        ["11", "Code chức năng quản lý sản phẩm", "09/07/2026", "29/07/2026", "Trần Văn Khánh", "100%"],
        ["12", "Code chức năng quản lý hóa đơn", "09/07/2026", "29/07/2026", "Thẩm Anh Minh", "100%"],
        ["13", "Code chức năng quản lý phiếu giảm giá", "09/07/2026", "29/07/2026", "Vũ Tiến Nam", "100%"],
        ["14", "Code chức năng đợt giảm giá", "09/07/2026", "29/07/2026", "Vũ Tiến Nam", "100%"],
        ["15", "Code chức năng quản lý nhân viên", "09/07/2026", "29/07/2026", "Nguyễn Tiến Bách", "100%"],
        ["16", "Code chức năng quản lý khách hàng", "09/07/2026", "29/07/2026", "Đỗ Vạn Thành", "100%"],
        ["17", "Code chức năng bán hàng tại quầy", "09/07/2026", "29/07/2026", "Trần Văn Khánh", "100%"],
        ["18", "Code chức năng thống kê", "09/07/2026", "29/07/2026", "Đỗ Vạn Thành", "100%"],
        ["19", "Kiểm tra tích hợp", "09/07/2026", "29/07/2026", "Cả nhóm", "100%"],
        ["20", "Kiểm tra và tích hợp toàn bộ hệ thống", "09/07/2026", "29/07/2026", "Trần Văn Khánh", "100%"],
    ]
    fill_table(tables[6], [["STT", "Tên công việc", "Ngày bắt đầu", "Ngày kết thúc", "Người thực hiện", "Hoàn thành"]] + plan)

    survey_pending = "Chưa có dữ liệu khảo sát được nhóm xác nhận; cần bổ sung nguồn, địa điểm và kết quả thực tế."
    fill_table(tables[7], [
        ["STT", "Luồng", "Mô tả luồng"],
        ["1", "Bán hàng tại quầy", survey_pending],
        ["2", "Quản lý sản phẩm và tồn kho", survey_pending],
        ["3", "Quản lý hóa đơn và thanh toán", survey_pending],
        ["4", "Quản lý phiếu/đợt giảm giá", survey_pending],
        ["5", "Thống kê - báo cáo", survey_pending],
    ])

    use_cases = [
        ["1", "UC-01", "Đăng nhập", "Quản lý, Nhân viên", "Xác thực bằng email hoặc mã nhân viên và điều hướng theo vai trò."],
        ["2", "UC-02", "Bán hàng tại quầy", "Quản lý, Nhân viên", "Tạo hóa đơn chờ, tìm/quét sản phẩm và quản lý giỏ hàng tại quầy."],
        ["3", "UC-03", "Thanh toán hóa đơn", "Quản lý, Nhân viên", "Áp dụng ưu đãi, chọn phương thức thanh toán và hoàn tất giao dịch."],
        ["4", "UC-04", "Quản lý hóa đơn", "Quản lý, Nhân viên", "Tra cứu, xem chi tiết, trạng thái và lịch sử hóa đơn."],
        ["5", "UC-05", "Quản lý sản phẩm", "Quản lý, Nhân viên", "Thêm, sửa, tìm kiếm và cập nhật thông tin sản phẩm kính."],
        ["6", "UC-06", "Quản lý biến thể sản phẩm", "Quản lý, Nhân viên", "Quản lý màu, kích cỡ, giá, tồn kho, hình ảnh và trạng thái biến thể."],
        ["7", "UC-07", "Quản lý thuộc tính", "Quản lý, Nhân viên", "Quản lý danh mục, thương hiệu, chất liệu, kiểu dáng, gọng và tròng kính."],
        ["8", "UC-08", "Quản lý khách hàng", "Quản lý, Nhân viên", "Thêm, sửa, tìm kiếm khách hàng và quản lý địa chỉ."],
        ["9", "UC-09", "Quản lý nhân viên", "Quản lý", "Quản lý hồ sơ, tài khoản, vai trò và trạng thái nhân viên."],
        ["10", "UC-10", "Quản lý giảm giá", "Quản lý", "Quản lý phiếu/đợt giảm giá, điều kiện và thời gian áp dụng."],
        ["11", "UC-11", "Thống kê - báo cáo", "Quản lý", "Theo dõi doanh thu, hóa đơn, sản phẩm, khách hàng và tồn kho."],
        ["12", "UC-12", "Đăng xuất", "Quản lý, Nhân viên", "Kết thúc phiên đăng nhập và quay về màn hình đăng nhập."],
    ]
    fill_table(tables[8], [["STT", "Mã UC", "Tên UC", "Tác nhân", "Mô tả"]] + use_cases)

    entities = [
        ("Ca làm việc", "Ghi nhận ca của nhân viên và liên kết các hóa đơn phát sinh trong ca."),
        ("Chất liệu", "Danh mục chất liệu sử dụng cho sản phẩm kính."),
        ("Chi tiết hóa đơn", "Lưu sản phẩm biến thể, số lượng và đơn giá của từng dòng hóa đơn."),
        ("Danh mục", "Phân nhóm sản phẩm kính phục vụ quản lý và tra cứu."),
        ("Địa chỉ khách hàng", "Lưu các địa chỉ liên hệ/giao dịch gắn với khách hàng."),
        ("Gọng kính", "Mô tả gọng theo hình dáng và kiểu quai kính."),
        ("Hình ảnh sản phẩm", "Lưu đường dẫn và thông tin hình ảnh của sản phẩm."),
        ("Hình dáng gọng", "Danh mục hình dáng dùng để phân loại gọng kính."),
        ("Hình thức thanh toán", "Danh mục các phương thức thanh toán được hệ thống hỗ trợ."),
        ("Hóa đơn", "Lưu thông tin giao dịch, khách hàng, nhân viên, ca, ưu đãi và tổng tiền."),
        ("Khách hàng", "Lưu hồ sơ khách mua hàng và lịch sử giao dịch liên quan."),
        ("Khách hàng - Phiếu giảm giá", "Bảng liên kết phiếu giảm giá cá nhân với khách hàng."),
        ("Kích cỡ", "Danh mục kích cỡ áp dụng cho biến thể sản phẩm."),
        ("Kiểu dáng", "Danh mục kiểu dáng của sản phẩm kính."),
        ("Kiểu quai kính", "Danh mục kiểu quai dùng để cấu thành gọng kính."),
        ("Lịch sử hóa đơn", "Theo dõi các lần thay đổi trạng thái và xử lý hóa đơn."),
        ("Lịch sử thanh toán", "Theo dõi các sự kiện và thông tin thanh toán của hóa đơn."),
        ("Màu sắc", "Danh mục màu áp dụng cho biến thể sản phẩm."),
        ("Nhân viên", "Lưu hồ sơ, thông tin đăng nhập, vai trò và trạng thái làm việc."),
        ("Phiếu giảm giá", "Lưu mã, loại giảm, giá trị, điều kiện, thời hạn và số lượng ưu đãi."),
        ("Sản phẩm", "Lưu thông tin chung của mẫu kính và các thuộc tính phân loại."),
        ("Sản phẩm chi tiết", "Biến thể sản phẩm theo màu, kích cỡ, giá, tồn kho và trạng thái."),
        ("Thanh toán hóa đơn", "Liên kết hóa đơn với phương thức, số tiền và mã giao dịch."),
        ("Thương hiệu", "Danh mục thương hiệu của sản phẩm kính."),
        ("Tròng kính", "Danh mục loại tròng kính áp dụng cho sản phẩm."),
    ]
    entity_rows = [[str(index), name, description] for index, (name, description) in enumerate(entities, 1)]
    fill_table(tables[9], [["STT", "Tên thực thể", "Mô tả"]] + entity_rows)

    parts["word/document.xml"] = etree.tostring(
        document, xml_declaration=True, encoding="UTF-8", standalone=True
    )

    footer = etree.fromstring(parts["word/footer1.xml"])
    footer_text = "Xây dựng website bán kính thời trang RIOR"
    set_text_nodes(footer, footer_text)
    parts["word/footer1.xml"] = etree.tostring(
        footer, xml_declaration=True, encoding="UTF-8", standalone=True
    )

    footnotes = etree.fromstring(parts["word/footnotes.xml"])
    footnote_text = {
        "1": "Sơ đồ tạm giữ từ tài liệu mẫu; nhóm sẽ thay bằng Activity Diagram bán hàng tại quầy.",
        "2": "Sơ đồ tạm giữ từ tài liệu mẫu; nhóm sẽ thay bằng Activity Diagram thanh toán hóa đơn.",
        "3": "Ảnh khảo sát tạm giữ; cần thay bằng ảnh khảo sát cửa hàng kính thực tế số 1.",
        "4": "Ảnh khảo sát tạm giữ; cần thay bằng ảnh khảo sát cửa hàng kính thực tế số 2.",
        "5": "Ảnh khảo sát tạm giữ; cần thay bằng ảnh khảo sát cửa hàng kính thực tế số 3.",
        "6": "Nguồn khảo sát online chưa được nhóm cung cấp; cần bổ sung đường dẫn và ngày truy cập.",
        "7": "Sơ đồ use case tạm giữ từ tài liệu mẫu; nhóm sẽ thay bằng sơ đồ hệ thống RIOR.",
        "8": "Sơ đồ ERD tạm giữ từ tài liệu mẫu; nhóm sẽ thay bằng sơ đồ cơ sở dữ liệu RIOR.",
    }
    for footnote in footnotes.xpath("//w:footnote", namespaces=NS):
        note_id = footnote.get(W + "id")
        if note_id in footnote_text:
            set_text_nodes(footnote, footnote_text[note_id])
    parts["word/footnotes.xml"] = etree.tostring(
        footnotes, xml_declaration=True, encoding="UTF-8", standalone=True
    )

    settings = etree.fromstring(parts["word/settings.xml"])
    update_fields = settings.find(W + "updateFields")
    if update_fields is None:
        update_fields = etree.SubElement(settings, W + "updateFields")
    update_fields.set(W + "val", "true")
    parts["word/settings.xml"] = etree.tostring(
        settings, xml_declaration=True, encoding="UTF-8", standalone=True
    )

    core = etree.fromstring(parts["docProps/core.xml"])
    title_nodes = core.xpath("//dc:title", namespaces=NS)
    if title_nodes:
        title_nodes[0].text = "Báo cáo dự án tốt nghiệp - Website bán kính thời trang RIOR"
    subject_nodes = core.xpath("//dc:subject", namespaces=NS)
    if subject_nodes:
        subject_nodes[0].text = "Nội dung Chương 1–2 của dự án RIOR, nhóm SD-26"
    parts["docProps/core.xml"] = etree.tostring(
        core, xml_declaration=True, encoding="UTF-8", standalone=True
    )

    output.parent.mkdir(parents=True, exist_ok=True)
    with ZipFile(output, "w", compression=ZIP_DEFLATED) as target:
        for name in infos:
            target.writestr(infos[name], parts[name])

    if sha256_file(reference) != REFERENCE_SHA256:
        raise RuntimeError("Reference changed during authoring")
    print(output)


def main() -> None:
    if len(sys.argv) != 3:
        raise SystemExit("Usage: build_rior_report.py REFERENCE.docx OUTPUT.docx")
    build(Path(sys.argv[1]), Path(sys.argv[2]))


if __name__ == "__main__":
    main()
