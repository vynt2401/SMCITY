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

@WebServlet("/api/xu-ly-so-thich")
public class XuLySoThich extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String idDiaDiem = req.getParameter("id_dia_diem");
        String action = ""; // Thêm (ADD) hay Xóa (REMOVE)

        Connection conn = KetNoiCSDL.layKetNoi();
        if (conn == null) {
            out.print("{\"status\": \"error\", \"message\": \"Lỗi kết nối CSDL\"}");
            return;
        }

        try {
            // 1. Kiểm tra trạng thái hiện tại
            String sqlCheck = "SELECT COUNT(*) FROM SoThich WHERE username = ? AND id_dia_diem = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, u);
            stmtCheck.setString(2, idDiaDiem);
            ResultSet rs = stmtCheck.executeQuery();
            rs.next();
            boolean isFavorite = rs.getInt(1) > 0;

            if (isFavorite) {
                // 2. Nếu đã thích -> XÓA (REMOVE)
                String sqlDelete = "DELETE FROM SoThich WHERE username = ? AND id_dia_diem = ?";
                PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
                stmtDelete.setString(1, u);
                stmtDelete.setString(2, idDiaDiem);
                stmtDelete.executeUpdate();
                action = "removed";
            } else {
                // 3. Nếu chưa thích -> THÊM (ADD)
                String sqlInsert = "INSERT INTO SoThich (username, id_dia_diem, ngay_them) " + "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 7 HOUR))";
                PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
                stmtInsert.setString(1, u);
                stmtInsert.setString(2, idDiaDiem);
                stmtInsert.executeUpdate();
                action = "added";
            }
            conn.close();
            out.print("{\"status\": \"success\", \"action\": \"" + action + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Lỗi SQL\"}");
        }
        out.flush();
    }
}