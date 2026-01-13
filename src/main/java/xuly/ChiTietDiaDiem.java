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

@WebServlet("/api/chi-tiet-dia-diem")
public class ChiTietDiaDiem extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String id = req.getParameter("id");
        String username = req.getParameter("user");
        Connection conn = KetNoiCSDL.layKetNoi();
        String jsonResult = "{}";

        if (conn != null && id != null) {
            try {
                // JOIN 3 BẢNG: DiaDiem, LoaiHinh, ThanhPho
                String sql = "SELECT d.*, l.ten_loai_hinh, t.ten_thanh_pho, d.map_link " +
                        "FROM DiaDiem d " +
                        "LEFT JOIN LoaiHinh l ON d.id_loai_hinh = l.id " +
                        "LEFT JOIN ThanhPho t ON d.id_city = t.id " +
                        "WHERE d.id = ?";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String ten = rs.getString("ten_dia_diem");
                    String diaChi = rs.getString("dia_chi");
                    String moTa = rs.getString("mo_ta");

                    // --- SỬA LỖI Ở ĐÂY ---
                    String tenLoai = rs.getString("ten_loai_hinh"); // Biến tên là tenLoai
                    if(tenLoai == null) tenLoai = "Khác";           // Phải check tenLoai

                    String tenTP = rs.getString("ten_thanh_pho");
                    String mapLink = rs.getString("map_link");
                    String anhList = rs.getString("anh_dd");

                    if(moTa != null) {
                        moTa = moTa.replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n") // Giữ lại dấu xuống dòng
                                .replace("\r", "");
                    } else {
                        moTa = "";
                    }
                    if(anhList == null || anhList.isEmpty()) anhList = "default_place.jpg";
                    if(mapLink == null) mapLink = "";

                    int idCity = rs.getInt("id_city");

                    // Tính điểm TB
                    String sqlRate = "SELECT AVG(rate_point) as diem_tb FROM Danhgia_diadiem WHERE id_dia_diem = ?";
                    PreparedStatement stmtRate = conn.prepareStatement(sqlRate);
                    stmtRate.setString(1, id);
                    ResultSet rsRate = stmtRate.executeQuery();

                    double diemTB = 0;
                    if (rsRate.next()) diemTB = rsRate.getDouble("diem_tb");

                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    DecimalFormat df = new DecimalFormat("0.0", symbols);
                    String sao = df.format(diemTB);

                    // Check yêu thích
                    boolean isFav = false;
                    if (username != null && !username.isEmpty() && !username.equals("null")) {
                        String sqlFav = "SELECT COUNT(*) FROM SoThich WHERE username = ? AND id_dia_diem = ?";
                        PreparedStatement stmtFav = conn.prepareStatement(sqlFav);
                        stmtFav.setString(1, username);
                        stmtFav.setString(2, id);
                        ResultSet rsFav = stmtFav.executeQuery();
                        if (rsFav.next() && rsFav.getInt(1) > 0) isFav = true;
                    }

                    jsonResult = "{" +
                            "\"status\": \"success\"," +
                            "\"ten\": \"" + ten + "\"," +
                            "\"diachi\": \"" + diaChi + "\"," +
                            "\"mota\": \"" + moTa + "\"," +
                            "\"loai\": \"" + tenLoai + "\"," + // Dùng biến tenLoai
                            "\"city_name\": \"" + tenTP + "\"," +
                            "\"anh_list\": \"" + anhList + "\"," +
                            "\"map_link\": \"" + mapLink + "\"," +
                            "\"id_city\": " + idCity + "," +
                            "\"sao\": \"" + sao + "\"," +
                            "\"is_fav\": " + isFav +
                            "}";
                } else {
                    jsonResult = "{\"status\": \"fail\", \"message\": \"Không tìm thấy địa điểm\"}";
                }
                conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        out.print(jsonResult);
        out.flush();
    }
}