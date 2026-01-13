🏙️ SMCITY - Hệ Thống Đánh Giá Thành Phố Thông Minh
https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white
https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white
https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white
https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white

📋 Giới Thiệu
SMCITY là một hệ thống web đánh giá và khám phá các thành phố thông minh, cho phép người dùng:

🔍 Khám phá thông tin về các thành phố và địa điểm du lịch

⭐ Đánh giá, bình luận về thành phố và địa điểm

❤️ Lưu các địa điểm yêu thích

👥 Phân quyền người dùng (Admin/User)

🚀 Tính Năng Chính
🎯 Cho Người Dùng
Đăng ký/Đăng nhập với xác thực

Tìm kiếm thành phố và địa điểm theo loại hình

Đánh giá và bình luận về địa điểm

Thêm vào danh sách yêu thích

Xem bản đồ tích hợp Google Maps

⚙️ Cho Quản Trị Viên
Quản lý thành phố (thêm, sửa, xóa)

Quản lý địa điểm theo từng thành phố

Quản lý loại hình địa điểm

Quản lý người dùng và đánh giá

🛠️ Công Nghệ Sử Dụng
Backend
Java 17+ - Ngôn ngữ lập trình chính

Servlet/JSP - Xử lý request và hiển thị view

Maven - Quản lý dependencies

JDBC - Kết nối cơ sở dữ liệu

Frontend
HTML5/CSS3 - Cấu trúc và giao diện

JavaScript - Xử lý tương tác

Bootstrap 5 - Framework CSS

JSTL - Hiển thị dữ liệu động

Database
MySQL 8+ - Hệ quản trị cơ sở dữ liệu

Database Schema:

NguoiDung - Quản lý người dùng

ThanhPho - Thông tin thành phố

DiaDiem - Thông tin địa điểm

DanhGia - Đánh giá của người dùng

SoThich - Địa điểm yêu thích

📁 Cấu Trúc Dự Án
text
SMCITY/
├── src/main/
│   ├── java/
│   │   ├── controller/          # Servlet controllers
│   │   ├── model/              # Data models/entities
│   │   ├── dao/                # Data Access Objects
│   │   ├── service/            # Business logic layer
│   │   └── tienich/            # Utilities (Database connection)
│   ├── webapp/
│   │   ├── WEB-INF/
│   │   │   ├── views/          # JSP pages
│   │   │   └── web.xml         # Deployment descriptor
│   │   ├── css/                # Stylesheets
│   │   ├── js/                 # JavaScript files
│   │   └── images/             # Hình ảnh
│   └── resources/
│       └── database.sql        # Database schema
├── pom.xml                     # Maven configuration
└── README.md                   # Tài liệu dự án
⚡ Cài Đặt và Chạy Dự Án
1. Yêu Cầu Hệ Thống
Java JDK 17+

Apache Maven 3.8+

MySQL 8.0+

Apache Tomcat 10+

Git

2. Cài Đặt Cơ Sở Dữ Liệu
sql
-- Tạo database
CREATE DATABASE smart_city_db;
USE smart_city_db;

-- Chạy file database.sql trong thư mục resources
-- hoặc import trực tiếp
3. Cấu Hình Dự Án
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
4. Build và Deploy
bash
# Build với Maven
mvn clean package

# File WAR sẽ được tạo tại: target/SMCITY.war
# Deploy lên Tomcat:
# - Copy SMCITY.war vào thư mục webapps của Tomcat
# - Khởi động Tomcat
5. Chạy Trong IDE (IntelliJ/Eclipse)
Import project như Maven project

Cấu hình Tomcat Server

Add deployment artifact

Run trên Tomcat

🔑 Tài Khoản Mẫu
Vai Trò	Username	Password	Quyền Hạn
👑 Admin	admin	123	Toàn quyền quản trị
👤 User	user1	123	Đánh giá, yêu thích
🌐 Truy Cập Ứng Dụng
Sau khi deploy thành công, truy cập:

URL: http://localhost:8080/SMCITY

Port mặc định: 8080 (có thể thay đổi tùy cấu hình Tomcat)

📊 Database Schema
https://docs/ERD.png

Các bảng chính:

NguoiDung: Lưu thông tin người dùng

ThanhPho: Thông tin thành phố

DiaDiem: Địa điểm trong thành phố

Danhgia_diadiem: Đánh giá địa điểm

Danhgia_city: Đánh giá thành phố

SoThich: Địa điểm yêu thích của người dùng

LoaiHinh: Phân loại địa điểm

🧪 Testing
bash
# Kiểm tra kết nối database
mvn test

# Kiểm tra từng module
# - Test đăng nhập
# - Test thêm địa điểm
# - Test đánh giá
📝 API Endpoints
Method	Endpoint	Mô Tả
GET	/thanhpho	Danh sách thành phố
GET	/thanhpho/{id}	Chi tiết thành phố
POST	/dangnhap	Đăng nhập
POST	/danhgia	Gửi đánh giá
GET	/dia-diem/search	Tìm kiếm địa điểm
🔒 Bảo Mật
Xác thực: Session-based authentication

Phân quyền: Role-based access control

SQL Injection: Sử dụng PreparedStatement

XSS: Escape special characters in output

🐛 Troubleshooting
Lỗi phổ biến và giải pháp:
Lỗi kết nối database:

Kiểm tra MySQL đang chạy

Kiểm tra username/password trong KetNoiCSDL.java

Lỗi 404 - Page not found:

Kiểm tra context path trong Tomcat

Kiểm tra web.xml configuration

Lỗi encoding tiếng Việt:

java
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");
🤝 Đóng Góp
Fork repository

Tạo feature branch (git checkout -b feature/AmazingFeature)

Commit changes (git commit -m 'Add some AmazingFeature')

Push to branch (git push origin feature/AmazingFeature)

Mở Pull Request

📄 Giấy Phép
Dự án được phân phối dưới giấy phép MIT. Xem file LICENSE để biết thêm chi tiết.


Tác giả: 
[vynt2401](https://github.com/vynt2401)
[hadesghost](https://github.com/Hadesghostkiller)


Project Link: https://github.com/vynt2401/SMCITY


Java Servlet/JSP - Web framework

Bootstrap - Frontend framework

Font Awesome - Icons

Google Maps API - Map integration

⭐ Nếu bạn thấy dự án hữu ích, hãy để lại một star trên GitHub!

📊 Thống Kê
https://img.shields.io/github/repo-size/vynt2401/SMCITY
https://img.shields.io/github/last-commit/vynt2401/SMCITY
https://img.shields.io/github/issues/vynt2401/SMCITY

Phiên bản hiện tại: 1.0.0
Cập nhật lần cuối: Tháng 1, 2026

