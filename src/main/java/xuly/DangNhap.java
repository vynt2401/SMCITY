package xuly;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import tienich.KetNoiCSDL;
import tienich.MaHoa;

@WebServlet("/api/dang-nhap")
public class DangNhap extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String p = req.getParameter("password");
        String pHash = MaHoa.bamSHA256(p);

        Connection conn = KetNoiCSDL.layKetNoi();
        String jsonResult = "";

        if (conn == null) {
            jsonResult = "{\"status\": \"error\", \"message\": \"Lỗi kết nối CSDL\"}";
        } else {
            try {
                String sql = "SELECT ho_ten, role FROM NguoiDung WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, u);
                stmt.setString(2, pHash);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String ten = rs.getString("ho_ten");
                    int role = rs.getInt("role");
                    jsonResult = "{\"status\": \"success\", \"ten\": \"" + ten + "\", \"role\": " + role + "}";
                } else {
                    jsonResult = "{\"status\": \"fail\", \"message\": \"Sai tài khoản hoặc mật khẩu\"}";
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                jsonResult = "{\"status\": \"error\", \"message\": \"Lỗi SQL\"}";
            }
        }
        out.print(jsonResult);
        out.flush();
    }
}