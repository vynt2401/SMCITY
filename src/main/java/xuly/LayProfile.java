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

@WebServlet("/api/lay-profile")
public class LayProfile extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        Connection conn = KetNoiCSDL.layKetNoi();

        StringBuilder jsonFavs = new StringBuilder("["); // Danh sách sở thích
        String hoTen = "";

        if (conn != null) {
            try {
                // 1. Lấy thông tin cá nhân
                String sqlInfo = "SELECT ho_ten FROM NguoiDung WHERE username = ?";
                PreparedStatement stmtInfo = conn.prepareStatement(sqlInfo);
                stmtInfo.setString(1, u);
                ResultSet rsInfo = stmtInfo.executeQuery();
                if (rsInfo.next()) hoTen = rsInfo.getString("ho_ten");

                // 2. Lấy danh sách sở thích (JOIN bảng DiaDiem và SoThich)
                String sqlFav = "SELECT d.id, d.ten_dia_diem FROM DiaDiem d " +
                        "JOIN SoThich s ON d.id = s.id_dia_diem " +
                        "WHERE s.username = ?";
                PreparedStatement stmtFav = conn.prepareStatement(sqlFav);
                stmtFav.setString(1, u);
                ResultSet rsFav = stmtFav.executeQuery();

                boolean isFirst = true;
                while (rsFav.next()) {
                    if (!isFirst) jsonFavs.append(",");
                    jsonFavs.append("{\"id\":").append(rsFav.getInt("id"))
                            .append(", \"ten\":\"").append(rsFav.getString("ten_dia_diem")).append("\"}");
                    isFirst = false;
                }
                conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        jsonFavs.append("]");

        // Trả về JSON tổng hợp
        out.print("{\"status\":\"success\", \"hoten\":\"" + hoTen + "\", \"favs\":" + jsonFavs.toString() + "}");
        out.flush();
    }
}