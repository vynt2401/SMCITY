# SMCITY - Hệ Thống Đánh Giá Thành Phố Thông Minh

## Giới thiệu
SMCITY là một hệ thống web đánh giá và khám phá các thành phố thông minh, cho phép người dùng:

### Khám phá thông tin về các thành phố và địa điểm du lịch

### Đánh giá, bình luận về thành phố và địa điểm

### Lưu các địa điểm yêu thích

### Phân quyền người dùng (Admin/User)

## Tính Năng Chính

### Cho Người Dùng
Đăng ký/Đăng nhập với xác thực

Tìm kiếm thành phố và địa điểm theo loại hình

Đánh giá và bình luận về địa điểm

Thêm vào danh sách yêu thích

Xem bản đồ tích hợp Google Maps

## Cho Quản Trị Viên
Quản lý thành phố (thêm, sửa, xóa)

Quản lý địa điểm theo từng thành phố

Quản lý loại hình địa điểm

Quản lý người dùng và đánh giá

## Công Nghệ Sử Dụng
### Backend
```
Java 17+ - Ngôn ngữ lập trình chính

Servlet/JSP - Xử lý request và hiển thị view

Maven - Quản lý dependencies

JDBC - Kết nối cơ sở dữ liệu
```

### Frontend
```
HTML5/CSS3 - Cấu trúc và giao diện

JavaScript - Xử lý tương tác

Bootstrap 5 - Framework CSS

JSTL - Hiển thị dữ liệu động
```

### Database
```
MySQL 8+ - Hệ quản trị cơ sở dữ liệu

Database Schema:

NguoiDung - Quản lý người dùng

ThanhPho - Thông tin thành phố

DiaDiem - Thông tin địa điểm

DanhGia - Đánh giá của người dùng

SoThich - Địa điểm yêu thích
```

## Cài Đặt và Chạy Dự Án
1. Yêu Cầu Hệ Thống
```
Java JDK 17+

Apache Maven 3.8+

MySQL 8.0+

Apache Tomcat 10+

Git
```
2. Cài Đặt Cơ Sở Dữ Liệu
```
sql
-- Tạo database
CREATE DATABASE smart_city_db;
USE smart_city_db;

-- Chạy file database.sql trong thư mục resources
-- hoặc import trực tiếp
```

3. Cấu Hình Dự Án
```
bash
# Clone repository
git clone https://github.com/vynt2401/SMCITY.git
cd SMCITY

# Cấu hình database (src/main/java/tienich/KetNoiCSDL.java)
public static Connection getConnection() {
    String url = "jdbc:mysql://localhost:3306/smart_city_db";
    String user = "root";  # Thay đổi theo cấu hình của bạn
    String password = "your_password";  # Thay đổi theo cấu hình của bạn
}
```
4. Build và Deploy

```
bash
# Build với Maven
mvn clean package

# File WAR sẽ được tạo tại: target/SMCITY.war
# Deploy lên Tomcat:
# - Copy SMCITY.war vào thư mục webapps của Tomcat
# - Khởi động Tomcat
5. Chạy Trong IDE (IntelliJ/Eclipse)
Import project như Maven project
```
Cấu hình Tomcat Server
```
Add deployment artifact

Run trên Tomcat
```

### Tài Khoản Mẫu

Username admin
Password 123

Sau khi deploy thành công, truy cập:
```
URL: http://localhost:8080/SMCITY
```
Port mặc định: 8080 (có thể thay đổi tùy cấu hình Tomcat)

```
Database Schema
https://docs/ERD.png
```

Các bảng chính:

NguoiDung: Lưu thông tin người dùng

ThanhPho: Thông tin thành phố

DiaDiem: Địa điểm trong thành phố

Danhgia_diadiem: Đánh giá địa điểm

Danhgia_city: Đánh giá thành phố

SoThich: Địa điểm yêu thích của người dùng

LoaiHinh: Phân loại địa điểm

## Testing
```
bash
# Kiểm tra kết nối database
mvn test
```

## Kiểm tra từng module trước khi chạy chính
## - Test đăng nhập
## - Test thêm địa điểm
## - Test đánh giá

## API Endpoints
Method	Endpoint	Mô Tả
GET	/thanhpho	Danh sách thành phố
GET	/thanhpho/{id}	Chi tiết thành phố
POST	/dangnhap	Đăng nhập
POST	/danhgia	Gửi đánh giá
GET	/dia-diem/search	Tìm kiếm địa điểm

Tác giả: 
[vynt2401](https://github.com/vynt2401)
[hadesghost](https://github.com/Hadesghostkiller)


Project Link: https://github.com/vynt2401/SMCITY


Java Servlet/JSP - Web framework

Bootstrap - Frontend framework

Font Awesome - Icons

Google Maps API - Map integration



Phiên bản hiện tại: 1.0.0
Cập nhật lần cuối: Tháng 1, 2026

