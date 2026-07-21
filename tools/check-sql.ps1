param(
    [string]$Server = "localhost,1433",
    [string]$Database = "quan_ly_ban_kinh",
    [string]$User = "minh",
    [string]$Password = "123456"
)

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[Console]::InputEncoding = $utf8NoBom
[Console]::OutputEncoding = $utf8NoBom
$OutputEncoding = $utf8NoBom

$connectionString = "Server=$Server;Database=$Database;User ID=$User;Password=$Password;Encrypt=False;TrustServerCertificate=True;"
$connection = New-Object System.Data.SqlClient.SqlConnection($connectionString)

try {
    $connection.Open()

    $queries = @(
        @{
            Name = "Kiểm tra database và số hóa đơn"
            Sql = "SELECT DB_NAME() AS database_name, COUNT(*) AS so_hoa_don FROM hoa_don"
        },
        @{
            Name = "Kiểm tra join hoa_don với khach_hang, nhan_vien, phieu_giam_gia"
            Sql = "SELECT TOP 5 hd.id, hd.ma_hoa_don, kh.ho_ten AS khach_hang, nv.ho_ten AS nhan_vien, pgg.ma_voucher FROM hoa_don hd LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id LEFT JOIN phieu_giam_gia pgg ON hd.id_phieu_giam_gia = pgg.id ORDER BY hd.id DESC"
        },
        @{
            Name = "Kiểm tra chi tiết hóa đơn và sản phẩm"
            Sql = "SELECT TOP 5 cthd.id_hoa_don, sp.ten_san_pham, spct.ma AS ma_chi_tiet, kqk.kieu_quai AS kieu_quai_kinh, COALESCE(NULLIF(spct.hinh_anh, ''), ha.url_anh) AS hinh_anh, cthd.so_luong, cthd.tong_tien FROM chi_tiet_hoa_don cthd LEFT JOIN san_pham_chi_tiet spct ON cthd.id_san_pham_chi_tiet = spct.id LEFT JOIN san_pham sp ON spct.id_san_pham = sp.id LEFT JOIN gong_kinh gk ON sp.id_gong_kinh = gk.id LEFT JOIN kieu_quai_kinh kqk ON gk.id_kieu_quai_kinh = kqk.id OUTER APPLY (SELECT TOP 1 url_anh FROM hinh_anh_san_pham WHERE id_san_pham = sp.id ORDER BY is_anh_chinh DESC, id ASC) ha ORDER BY cthd.id"
        },
        @{
            Name = "Kiểm tra lịch sử thanh toán"
            Sql = "SELECT TOP 5 id_hoa_don, so_tien, phuong_thuc_thanh_toan, trang_thai_thanh_toan, ngay_thanh_toan FROM lich_su_thanh_toan ORDER BY id DESC"
        }
    )

    foreach ($query in $queries) {
        Write-Output ""
        Write-Output "=== $($query.Name) ==="

        $command = $connection.CreateCommand()
        $command.CommandText = $query.Sql
        $adapter = New-Object System.Data.SqlClient.SqlDataAdapter($command)
        $table = New-Object System.Data.DataTable
        [void]$adapter.Fill($table)
        $table | Format-Table -AutoSize
    }
} finally {
    if ($connection.State -eq "Open") {
        $connection.Close()
    }
}

