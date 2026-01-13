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

@WebServlet("/api/doi-mat-khau")
public class DoiMatKhau extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String passCu = req.getParameter("pass_cu");
        String passMoi = req.getParameter("pass_moi");

        String hashCu = MaHoa.bamSHA256(passCu);
        String hashMoi = MaHoa.bamSHA256(passMoi);

        Connection conn = KetNoiCSDL.layKetNoi();
        try {
            // 1. Kiểm tra mật khẩu cũ có đúng không
            String sqlCheck = "SELECT username FROM NguoiDung WHERE username = ? AND password = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, u);
            stmtCheck.setString(2, hashCu);
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) {
                // 2. Đúng -> Cập nhật mới
                String sqlUpdate = "UPDATE NguoiDung SET password = ? WHERE username = ?";
                PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
                stmtUpdate.setString(1, hashMoi);
                stmtUpdate.setString(2, u);
                stmtUpdate.executeUpdate();
                out.print("{\"status\": \"success\", \"message\": \"Đổi mật khẩu thành công!\"}");
            } else {
                out.print("{\"status\": \"fail\", \"message\": \"Mật khẩu cũ không đúng!\"}");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Lỗi hệ thống\"}");
        }
    }
}