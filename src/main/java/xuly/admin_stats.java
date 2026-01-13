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

@WebServlet("/api/admin-stats")
public class admin_stats extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String idCity = req.getParameter("id_city"); // Lọc theo thành phố
        String days = req.getParameter("days"); // Lọc theo số ngày (7, 30...)

        if (days == null) days = "30"; // Mặc định xem 30 ngày gần nhất

        Connection conn = KetNoiCSDL.layKetNoi();
        StringBuilder labels = new StringBuilder("[");
        StringBuilder values = new StringBuilder("[");

        if (conn != null) {
            try {
                // Query: Đếm số đánh giá theo từng ngày
                // JOIN với DiaDiem để lọc theo id_city
                String sql = "SELECT DATE(dg.ngay_danh_gia) as ngay, COUNT(*) as so_luong " +
                        "FROM Danhgia_diadiem dg " +
                        "JOIN DiaDiem dd ON dg.id_dia_diem = dd.id " +
                        "WHERE dg.ngay_danh_gia >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) ";

                // Nếu có chọn thành phố (id_city khác 0 và khác null)
                if (idCity != null && !idCity.equals("0")) {
                    sql += "AND dd.id_city = ? ";
                }

                sql += "GROUP BY DATE(dg.ngay_danh_gia) ORDER BY ngay ASC";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(days));

                if (idCity != null && !idCity.equals("0")) {
                    stmt.setString(2, idCity);
                }

                ResultSet rs = stmt.executeQuery();
                boolean isFirst = true;
                while (rs.next()) {
                    if (!isFirst) { labels.append(","); values.append(","); }
                    // labels: ["2025-11-20", "2025-11-21"]
                    labels.append("\"").append(rs.getString("ngay")).append("\"");
                    // values: [5, 10]
                    values.append(rs.getInt("so_luong"));
                    isFirst = false;
                }
                conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        labels.append("]");
        values.append("]");

        out.print("{\"status\":\"success\", \"labels\":" + labels + ", \"data\":" + values + "}");
        out.flush();
    }
}