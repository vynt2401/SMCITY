package xuly;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tienich.KetNoiCSDL;
import tienich.MaHoa;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

@WebServlet("/api/dang-ky")
public class DangKy extends HttpServlet { // Đã đổi tên class
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String p = req.getParameter("password");
        String hoTen = req.getParameter("hoten");

        String regexTen = "^[a-zA-ZÀ-ỹ\\s]+$";
        if (!Pattern.matches(regexTen, hoTen)) {
            out.print("{\"status\": \"fail\", \"message\": \"Họ tên không được chứa số hoặc ký tự đặc biệt!\"}");
            out.flush();
            return;
        }

        String hoTenChuan = MaHoa.chuanHoaTen(hoTen);
        String pHash = MaHoa.bamSHA256(p);

        Connection conn = KetNoiCSDL.layKetNoi();
        if (conn == null) {
            out.print("{\"status\": \"error\", \"message\": \"Lỗi kết nối CSDL\"}");
            return;
        }

        try {
            String sqlCheck = "SELECT username FROM NguoiDung WHERE username = ?";
            PreparedStatement ptCheck = conn.prepareStatement(sqlCheck);
            ptCheck.setString(1, u);
            ResultSet rs = ptCheck.executeQuery();

            if (rs.next()) {
                out.print("{\"status\": \"fail\", \"message\": \"Tên đăng nhập đã tồn tại!\"}");
            } else {
                String sqlInsert = "INSERT INTO NguoiDung (username, password, ho_ten, role) VALUES (?, ?, ?, 0)";
                PreparedStatement ptInsert = conn.prepareStatement(sqlInsert);
                ptInsert.setString(1, u);
                ptInsert.setString(2, pHash);
                ptInsert.setString(3, hoTenChuan);

                int row = ptInsert.executeUpdate();
                if (row > 0) {
                    out.print("{\"status\": \"success\", \"message\": \"Đăng ký thành công!\"}");
                } else {
                    out.print("{\"status\": \"fail\", \"message\": \"Không thể tạo tài khoản\"}");
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Lỗi SQL: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}