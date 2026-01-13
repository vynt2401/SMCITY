# Dự án SMcity - Thành phố Thông minh

## Yêu cầu cài đặt
1. Java JDK 17+
2. Maven
3. MySQL (Docker hoặc XAMPP)
4. Tomcat 10+

## Cách chạy dự án
1. Mở MySQL, tạo database tên `smart_city_db`.
2. Chạy file `database.sql` ở trong `/resources` để nạp dữ liệu.
3. Mở file `src/main/java/tienich/KetNoiCSDL.java` sửa lại user/pass database của bạn.
4. Cấu hình Tomcat trong IntelliJ và chạy.
5. Truy cập: http://localhost:8080/SMcity/

## Tài khoản mẫu
- Admin: admin / 123
- User: user1 / 123
