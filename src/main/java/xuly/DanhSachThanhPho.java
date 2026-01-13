package xuly;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tienich.KetNoiCSDL;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/danh-sach-thanh-pho")
public class DanhSachThanhPho extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Cấu hình trả về JSON tiếng Việt
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        Connection conn = KetNoiCSDL.layKetNoi();
        StringBuilder jsonResult = new StringBuilder("["); // Bắt đầu chuỗi JSON mảng

        if (conn != null) {
            try {
                String sql = "SELECT * FROM ThanhPho";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                boolean isFirst = true;
                while (rs.next()) {
                    if (!isFirst) {
                        jsonResult.append(","); // Thêm dấu phẩy giữa các phần tử
                    }
                    int id = rs.getInt("id");
                    String ten = rs.getString("ten_thanh_pho");

                    // --- MỚI: Lấy ảnh bìa ---
                    String anh = rs.getString("anh_bia");
                    // Xử lý nếu trong database chưa có ảnh (null) thì dùng ảnh mặc định
                    if(anh == null || anh.isEmpty()) {
                        anh = "default.jpg";
                    }

                    // Tạo JSON object thủ công: {"id": 1, "ten": "Hà Nội", "anh": "hanoi.jpg"}
                    jsonResult.append("{\"id\":").append(id)
                            .append(", \"ten\":\"").append(ten).append("\"")
                            .append(", \"anh\":\"").append(anh).append("\"}");

                    isFirst = false;
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jsonResult.append("]"); // Đóng chuỗi JSON

        out.print(jsonResult.toString());
        out.flush();
    }
}