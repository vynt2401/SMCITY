package xuly;
import java.util.TimeZone;
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
import java.text.SimpleDateFormat;

@WebServlet("/api/lay-lich-su")
public class LayLichSu extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        Connection conn = KetNoiCSDL.layKetNoi();
        StringBuilder jsonBody = new StringBuilder("[");

        if (conn != null) {
            try {
                // CÂU SQL THẦN THÁNH: Gộp bảng Đánh giá và bảng Sở thích lại
                // Cột 'loai_hd' dùng để phân biệt: 1 là Đánh giá, 2 là Thích
                String sql =
                        "SELECT 'review' AS loai_hd, d.id, d.ten_dia_diem, r.rate_point, r.comment, r.ngay_danh_gia AS thoi_gian " +
                                "FROM Danhgia_diadiem r JOIN DiaDiem d ON r.id_dia_diem = d.id WHERE r.username = ? " +
                                "UNION ALL " +
                                "SELECT 'fav' AS loai_hd, d.id, d.ten_dia_diem, 0 AS rate_point, '' AS comment, s.ngay_them AS thoi_gian " +
                                "FROM SoThich s JOIN DiaDiem d ON s.id_dia_diem = d.id WHERE s.username = ? " +
                                "ORDER BY thoi_gian DESC"; // Sắp xếp cái mới nhất lên đầu

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, u);
                stmt.setString(2, u);

                ResultSet rs = stmt.executeQuery();
                boolean isFirst = true;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // giờ Việt Nam (UTC+7)

                while (rs.next()) {
                    if (!isFirst) jsonBody.append(",");

                    String type = rs.getString("loai_hd");
                    String cmt = rs.getString("comment");
                    if(cmt != null) cmt = cmt.replace("\"", "\\\"").replace("\n", " ");

                    jsonBody.append("{")
                            .append("\"type\":\"").append(type).append("\",")
                            .append("\"id_dia_diem\":").append(rs.getInt("id")).append(",")
                            .append("\"ten\":\"").append(rs.getString("ten_dia_diem")).append("\",")
                            .append("\"rate\":").append(rs.getInt("rate_point")).append(",")
                            .append("\"comment\":\"").append(cmt).append("\",")
                            .append("\"time\":\"").append(sdf.format(rs.getTimestamp("thoi_gian"))).append("\"")
                            .append("}");
                    isFirst = false;
                }
                conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        jsonBody.append("]");
        out.print(jsonBody.toString());
        out.flush();
    }
}