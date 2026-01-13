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

@WebServlet("/api/them-danh-gia")
public class themDanhgia_city extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String idCity = req.getParameter("id_city");
        String rate = req.getParameter("rate"); // Số sao (1-5)

        Connection conn = KetNoiCSDL.layKetNoi();
        if (conn == null) {
            out.print("{\"status\": \"error\", \"message\": \"Lỗi kết nối CSDL\"}");
            return;
        }

        try {
            // 1. KIỂM TRA: User này đã đánh giá thành phố này chưa?
            String sqlCheck = "SELECT id FROM Danhgia_city WHERE username = ? AND id_city = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, u);
            stmtCheck.setString(2, idCity);
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) {
                // Đã tồn tại -> Báo lỗi
                out.print("{\"status\": \"fail\", \"message\": \"Bạn đã đánh giá thành phố này rồi!\"}");
            } else {
                // 2. Chưa tồn tại -> Thêm mới
                String sqlInsert = "INSERT INTO Danhgia_city (username, id_city, rate_city) VALUES (?, ?, ?)";
                PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
                stmtInsert.setString(1, u);
                stmtInsert.setString(2, idCity);
                stmtInsert.setString(3, rate);

                int row = stmtInsert.executeUpdate();
                if (row > 0) {
                    out.print("{\"status\": \"success\", \"message\": \"Đánh giá thành công!\"}");
                } else {
                    out.print("{\"status\": \"fail\", \"message\": \"Lỗi khi lưu đánh giá\"}");
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Lỗi SQL\"}");
        }
        out.flush();
    }
}