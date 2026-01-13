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

@WebServlet("/api/gui-binh-luan")
public class GuiBinhLuan extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String idDiaDiem = req.getParameter("id_dia_diem");
        String rate = req.getParameter("rate");
        String cmt = req.getParameter("comment");

        Connection conn = KetNoiCSDL.layKetNoi();
        String msg = "";
        String status = "fail";

        if (conn != null) {
            try {
                // Cho phép comment nhiều lần, không cần check trùng
                // Thêm cột ngay_danh_gia vào và dùng hàm DATE_ADD để cộng 7 tiếng
                String sql = "INSERT INTO Danhgia_diadiem (username, id_dia_diem, rate_point, comment, ngay_danh_gia) " +
                        "VALUES (?, ?, ?, ?, DATE_ADD(NOW(), INTERVAL 7 HOUR))";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, u);
                stmt.setString(2, idDiaDiem);
                stmt.setString(3, rate);
                stmt.setString(4, cmt);

                int row = stmt.executeUpdate();
                if(row > 0) {
                    status = "success";
                    msg = "Cảm ơn bạn đã đánh giá!";
                } else {
                    msg = "Lỗi khi lưu bình luận.";
                }
                conn.close();
            } catch (Exception e) { e.printStackTrace(); msg = "Lỗi SQL"; }
        } else { msg = "Lỗi kết nối"; }

        out.print("{\"status\":\"" + status + "\", \"message\":\"" + msg + "\"}");
        out.flush();
    }
}