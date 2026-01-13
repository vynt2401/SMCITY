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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@WebServlet("/api/lay-dia-diem")
public class LayDiaDiem extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String idCity = req.getParameter("id_city");
        String typeId = req.getParameter("type");
        String username = req.getParameter("user");
        String sort = req.getParameter("sort");

        Connection conn = KetNoiCSDL.layKetNoi();
        StringBuilder jsonBody = new StringBuilder("[");

        if (conn != null) {
            try {
                String sql = "SELECT d.id, d.ten_dia_diem, d.dia_chi, d.anh_dd, d.id_loai_hinh, " +
                        "COALESCE(AVG(dg.rate_point), 0) as diem_tb, " +
                        "COUNT(dg.id) as luot_dg, " +
                        "MAX(CASE WHEN s.username = ? THEN 1 ELSE 0 END) as da_thich " +
                        "FROM DiaDiem d " +
                        "LEFT JOIN Danhgia_diadiem dg ON d.id = dg.id_dia_diem " +
                        "LEFT JOIN SoThich s ON d.id = s.id_dia_diem " +
                        "WHERE d.id_city = ? ";

                if (typeId != null && !typeId.equals("0") && !typeId.isEmpty()) {
                    sql += " AND d.id_loai_hinh = ? ";
                }

                sql += " GROUP BY d.id, d.ten_dia_diem, d.dia_chi, d.anh_dd, d.id_loai_hinh ";

                if ("hot".equals(sort)) {
                    sql += " ORDER BY diem_tb DESC LIMIT 10";
                } else {
                    sql += " ORDER BY d.id DESC LIMIT 50";
                }

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, (username != null && !username.equals("null")) ? username : "");
                stmt.setString(2, idCity);

                if (typeId != null && !typeId.equals("0") && !typeId.isEmpty()) {
                    stmt.setString(3, typeId);
                }

                ResultSet rs = stmt.executeQuery();
                boolean isFirst = true;

                // --- SỬA LỖI TẠI ĐÂY: Đổi "#.0" thành "0.0" ---
                // "0.0" đảm bảo số 0 luôn hiển thị (0.0 thay vì .0)
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat df = new DecimalFormat("0.0", symbols);

                while (rs.next()) {
                    if (!isFirst) jsonBody.append(",");

                    String anh = rs.getString("anh_dd");
                    if(anh == null || anh.isEmpty()) anh = "default_place.jpg";

                    String ten = rs.getString("ten_dia_diem");
                    if(ten != null) ten = ten.replace("\"", "\\\"").replace("\n", " ");

                    String diaChi = rs.getString("dia_chi");
                    if(diaChi != null) diaChi = diaChi.replace("\"", "\\\"").replace("\n", " ");

                    double diemRaw = rs.getDouble("diem_tb");
                    String soSao = df.format(diemRaw);

                    jsonBody.append("{")
                            .append("\"id\":").append(rs.getInt("id")).append(",")
                            .append("\"ten\":\"").append(ten).append("\",")
                            .append("\"diachi\":\"").append(diaChi).append("\",")
                            .append("\"anh\":\"").append(anh).append("\",")
                            .append("\"sao\":").append(soSao).append(",")
                            .append("\"luot_dg\":").append(rs.getInt("luot_dg")).append(",")
                            .append("\"is_fav\":").append(rs.getInt("da_thich") == 1)
                            .append("}");
                    isFirst = false;
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.print("{\"status\":\"error\", \"message\":\"Lỗi SQL: " + e.getMessage().replace("\"", "'") + "\"}");
                return;
            }
        }
        jsonBody.append("]");
        out.print("{\"status\":\"success\", \"data\":" + jsonBody.toString() + "}");
        out.flush();
    }
}