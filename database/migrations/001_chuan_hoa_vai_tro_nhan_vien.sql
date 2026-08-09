-- SQL Server: chạy một lần trước khi triển khai phiên bản có phân quyền mới.
-- Nếu chưa có tài khoản Quản lý, điền mã nhân viên vào biến dưới đây, ví dụ N'NV0001'.
DECLARE @ma_quan_ly_khoi_tao NVARCHAR(50) = NULL;

BEGIN TRANSACTION;

UPDATE nhan_vien
SET vai_tro = 0
WHERE vai_tro IS NULL;

IF NOT EXISTS (SELECT 1 FROM nhan_vien WHERE vai_tro = 1)
   AND @ma_quan_ly_khoi_tao IS NOT NULL
BEGIN
    UPDATE nhan_vien
    SET vai_tro = 1
    WHERE ma_nhan_vien = @ma_quan_ly_khoi_tao AND (trang_thai IS NULL OR trang_thai = 1);
END;

-- Phải có ít nhất một quản lý để tránh khóa toàn bộ khu vực quản trị.
IF NOT EXISTS (SELECT 1 FROM nhan_vien WHERE vai_tro = 1 AND (trang_thai IS NULL OR trang_thai = 1))
BEGIN
    ROLLBACK TRANSACTION;
    THROW 50001, N'Cần gán ít nhất một nhân viên đang hoạt động làm Quản lý trước khi triển khai.', 1;
END;

ALTER TABLE nhan_vien
ALTER COLUMN vai_tro INT NOT NULL;

IF NOT EXISTS (
    SELECT 1
    FROM sys.default_constraints dc
    JOIN sys.columns c
      ON c.default_object_id = dc.object_id
    WHERE dc.parent_object_id = OBJECT_ID(N'dbo.nhan_vien')
      AND c.name = N'vai_tro'
)
BEGIN
    ALTER TABLE nhan_vien
    ADD CONSTRAINT DF_nhan_vien_vai_tro DEFAULT 0 FOR vai_tro;
END;

COMMIT TRANSACTION;
