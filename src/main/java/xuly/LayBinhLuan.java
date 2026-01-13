package xuly;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
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

@WebServlet("/api/lay-binh-luan")
public class LayBinhLuan extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String idDiaDiem = req.getParameter("id");
        Connection conn = KetNoiCSDL.layKetNoi();
        StringBuilder jsonBody = new StringBuilder();

        if (conn != null) {
            try {
                // SQL: Dùng JOIN để lấy ho_ten từ bảng NguoiDung
                String sql = "SELECT d.*, n.ho_ten " +
                        "FROM Danhgia_diadiem d " +
                        "JOIN NguoiDung n ON d.username = n.username " +
                        "WHERE d.id_dia_diem = ? " +
                        "ORDER BY d.id DESC";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, idDiaDiem);
                ResultSet rs = stmt.executeQuery();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Set giờ VN

                boolean isFirst = true;
                while (rs.next()) {
                    if (!isFirst) jsonBody.append(",");
                    String comment = rs.getString("comment");
                    if(comment != null) comment = comment.replace("\"", "\\\"").replace("\n", " ");

                    // Lấy thời gian và định dạng lại
                    String thoiGianDep = sdf.format(rs.getTimestamp("ngay_danh_gia"));

                    jsonBody.append("{")
                            .append("\"user\":\"").append(rs.getString("ho_ten")).append("\",")
                            .append("\"rate\":").append(rs.getInt("rate_point")).append(",")
                            .append("\"comment\":\"").append(comment).append("\",")
                            .append("\"ngay\":\"").append(thoiGianDep).append("\"") // Dùng biến thời gian mới
                            .append("}");
                    isFirst = false;
                }
                conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        out.print("[" + jsonBody.toString() + "]");
        out.flush();
    }
}