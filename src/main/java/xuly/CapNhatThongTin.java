package xuly;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tienich.KetNoiCSDL;
import tienich.MaHoa; // Import để dùng hàm chuẩn hóa tên

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Pattern;

@WebServlet("/api/cap-nhat-thong-tin")
public class CapNhatThongTin extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String hoTenMoi = req.getParameter("hoten");

        // Validate tên (như lúc đăng ký)
        String regexTen = "^[a-zA-ZÀ-ỹ\\s]+$";
        if (!Pattern.matches(regexTen, hoTenMoi)) {
            out.print("{\"status\": \"fail\", \"message\": \"Tên không được chứa số/ký tự đặc biệt\"}");
            return;
        }

        String tenChuan = MaHoa.chuanHoaTen(hoTenMoi); // Chuẩn hóa lại cho đẹp

        Connection conn = KetNoiCSDL.layKetNoi();
        try {
            String sql = "UPDATE NguoiDung SET ho_ten = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tenChuan);
            stmt.setString(2, u);
            stmt.executeUpdate();

            conn.close();
            out.print("{\"status\": \"success\", \"message\": \"Cập nhật tên thành công!\", \"ten_moi\": \"" + tenChuan + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Lỗi SQL\"}");
        }
    }
}