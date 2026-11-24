# Hệ thống quản lý thư viện

Đồ án môn Phân tích thiết kế hệ thống. Hệ thống quản lý thư viện cho một trường đại học (thư viện giả định), chạy trên nền web, gồm ba phân hệ:

* **Bạn đọc:** tra cứu sách, xem sách đang mượn và lịch sử, tự gia hạn, đặt trước, xem tiền phạt.
* **Thủ thư:** quản lý đầu sách, bản sách, bạn đọc; mượn, trả bằng mã vạch; xử lý vi phạm, thu tiền phạt; xem danh sách quá hạn.
* **Quản trị viên:** quản lý tài khoản, sửa tham số quy định (số sách tối đa, số ngày mượn, mức phạt...), xem nhật ký.

Ngoài ra hệ thống có các tác vụ chạy theo lịch: đánh dấu sách quá hạn, tự khóa và mở thẻ, hủy lượt giữ sách đặt trước đã hết hạn, gửi email nhắc hạn trả.

## Công nghệ

|Phần|Công nghệ|
|-|-|
|Backend|Java 17, Spring Boot 3.3, Spring Data JPA, Spring Security (JWT)|
|Frontend|React 18, Vite 5, React Router, Axios|
|Cơ sở dữ liệu|PostgreSQL 16|
|Email khi chạy thử|MailHog|
|Kiểm thử|JUnit 5, Mockito, Postman, JMeter|

## Cấu trúc thư mục

```
├── backend/     Mã nguồn Spring Boot, chia gói theo tầng (controller, service, repository, entity...)
├── frontend/    Mã nguồn React
├── database/    thuvien.sql (tạo bảng + dữ liệu cấu hình)
│                du\_lieu\_demo.sql (dữ liệu mẫu)
│                du\_lieu\_lon.sql (20.000 đầu sách, 50.000 bản sách để đo hiệu năng)
└── kiem-thu/    Bộ kiểm thử API (Postman) và kịch bản kiểm thử tải (JMeter)
```

## Cài đặt và chạy

Cần cài sẵn: JDK 17, Node.js bản LTS, PostgreSQL 16. MailHog không bắt buộc, chỉ cần nếu muốn xem email (xem thư ở http://localhost:8025).

**1. Cơ sở dữ liệu**

Tạo database tên `thuvien`, rồi chạy lần lượt hai file:

```
database/thuvien.sql
database/du\_lieu\_demo.sql
```

Muốn nạp lại từ đầu thì chạy `DROP SCHEMA public CASCADE; CREATE SCHEMA public;` trước. Ngày mượn và hạn trả trong dữ liệu mẫu tính theo `CURRENT\_DATE`, nên chạy vào ngày nào cũng có đủ các tình huống trễ hạn, sắp đến hạn.

**2. Backend**

Sửa mật khẩu PostgreSQL trong `backend/src/main/resources/application.yml` cho đúng với máy, rồi chạy:

```
cd backend
mvn spring-boot:run
```

Hoặc mở thư mục `backend` bằng IntelliJ và chạy lớp `ThuVienApplication`. Backend chạy ở cổng 8080.

**3. Frontend**

```
cd frontend
npm install
npm run dev
```

Mở http://localhost:5173.

## Tài khoản mẫu

Mật khẩu chung: `Thuvien@123`

|Tài khoản|Vai trò|
|-|-|
|admin|Quản trị viên|
|thuthu01, thuthu02|Thủ thư|
|BD000001 → BD000025|Bạn đọc|

Một vài bạn đọc có sẵn tình huống để thử:

|Mã thẻ|Tình huống|
|-|-|
|BD000001|Đang mượn 3/5 cuốn, mượn thêm được|
|BD000002|Có 1 cuốn quá hạn, không được mượn thêm|
|BD000003|Quá hạn hơn 30 ngày, thẻ tự bị khóa|
|BD000004|Đang chờ đặt trước cuốn "Kinh tế vận tải"|

## Kiểm thử

* **Kiểm thử đơn vị:** `cd backend \&\& mvn test` (9 lớp, 43 test).
* **Kiểm thử API:** import `kiem-thu/ThuVien.postman\_collection.json` vào Postman rồi chạy bằng Collection Runner. Nên nạp lại dữ liệu mẫu trước mỗi lần chạy vì bộ test có mượn, trả, thu tiền thật.
* **Kiểm thử tải:** mở `kiem-thu/ThuVien\_50\_nguoi.jmx` bằng JMeter (50 người dùng đồng thời trong 5 phút).

Tác vụ định kỳ mặc định chạy lúc 0 giờ và mỗi giờ. Khi thử có thể đăng nhập admin, vào Nhật ký, bấm "Chạy tác vụ định kỳ ngay".

## Phạm vi hiện tại

Đã làm: 22 yêu cầu mức Must, cùng với đổi mật khẩu, kiểm tra ISBN, gia hạn, đặt trước, email nhắc hạn và nhật ký thao tác.

Chưa làm: ba báo cáo thống kê (lượt mượn, sách mượn nhiều, tiền phạt), xuất báo cáo ra Excel, nhập danh sách bạn đọc từ Excel.

