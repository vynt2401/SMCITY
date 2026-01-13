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

@WebServlet("/api/quen-mat-khau")
public class QuenMatKhau extends HttpServlet { // Đã đổi tên class
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String newPass = req.getParameter("newpass");
        String newPassHash = MaHoa.bamSHA256(newPass);

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
                String sqlUpdate = "UPDATE NguoiDung SET password = ? WHERE username = ?";
                PreparedStatement ptUpdate = conn.prepareStatement(sqlUpdate);
                ptUpdate.setString(1, newPassHash);
                ptUpdate.setString(2, u);

                int row = ptUpdate.executeUpdate();
                if(row > 0) {
                    out.print("{\"status\": \"success\", \"message\": \"Đổi mật khẩu thành công!\"}");
                } else {
                    out.print("{\"status\": \"fail\", \"message\": \"Lỗi khi cập nhật mật khẩu\"}");
                }
            } else {
                out.print("{\"status\": \"fail\", \"message\": \"Tên đăng nhập không tồn tại!\"}");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Lỗi SQL\"}");
        }
        out.flush();
    }
}