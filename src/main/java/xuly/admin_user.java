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

@WebServlet("/api/admin-user")
public class admin_user extends HttpServlet {

    // GET: Lấy danh sách user (có tìm kiếm & phân trang)
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String keyword = req.getParameter("q"); // Từ khóa tìm kiếm
        int page = 1;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception e) {}
        int limit = 10;
        int offset = (page - 1) * limit;

        Connection conn = KetNoiCSDL.layKetNoi();
        StringBuilder jsonBody = new StringBuilder("[");
        int totalPages = 0;

        if (conn != null) {
            try {
                // 1. Đếm tổng số để chia trang
                String sqlCount = "SELECT COUNT(*) FROM NguoiDung WHERE role = 0"; // Chỉ quản lý user thường
                if (keyword != null && !keyword.isEmpty()) {
                    sqlCount += " AND (username LIKE ? OR ho_ten LIKE ?)";
                }
                PreparedStatement stmtCount = conn.prepareStatement(sqlCount);
                if (keyword != null && !keyword.isEmpty()) {
                    stmtCount.setString(1, "%" + keyword + "%");
                    stmtCount.setString(2, "%" + keyword + "%");
                }
                ResultSet rsCount = stmtCount.executeQuery();
                if (rsCount.next()) {
                    int total = rsCount.getInt(1);
                    totalPages = (int) Math.ceil((double) total / limit);
                }

                // 2. Lấy dữ liệu
                String sqlData = "SELECT * FROM NguoiDung WHERE role = 0";
                if (keyword != null && !keyword.isEmpty()) {
                    sqlData += " AND (username LIKE ? OR ho_ten LIKE ?)";
                }
                sqlData += " LIMIT ? OFFSET ?";

                PreparedStatement stmtData = conn.prepareStatement(sqlData);
                int idx = 1;
                if (keyword != null && !keyword.isEmpty()) {
                    stmtData.setString(idx++, "%" + keyword + "%");
                    stmtData.setString(idx++, "%" + keyword + "%");
                }
                stmtData.setInt(idx++, limit);
                stmtData.setInt(idx, offset);

                ResultSet rs = stmtData.executeQuery();
                boolean isFirst = true;
                while (rs.next()) {
                    if (!isFirst) jsonBody.append(",");
                    jsonBody.append("{")
                            .append("\"user\":\"").append(rs.getString("username")).append("\",")
                            .append("\"ten\":\"").append(rs.getString("ho_ten")).append("\"")
                            .append("}");
                    isFirst = false;
                }
                conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        jsonBody.append("]");
        out.print("{\"status\":\"success\", \"total_pages\":" + totalPages + ", \"data\":" + jsonBody.toString() + "}");
        out.flush();
    }

    // POST: Thêm hoặc Xóa user
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String action = req.getParameter("action");
        Connection conn = KetNoiCSDL.layKetNoi();

        try {
            if ("delete".equals(action)) {
                String u = req.getParameter("username");
                String sql = "DELETE FROM NguoiDung WHERE username = ? AND role = 0"; // Chỉ xóa user thường
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, u);
                stmt.executeUpdate();
                out.print("{\"status\":\"success\", \"message\":\"Đã xóa người dùng!\"}");
            }
            else if ("add".equals(action)) {
                String u = req.getParameter("username");
                String p = req.getParameter("password");
                String n = req.getParameter("hoten");

                // Check trùng
                String sqlCheck = "SELECT username FROM NguoiDung WHERE username = ?";
                PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
                stmtCheck.setString(1, u);
                if (stmtCheck.executeQuery().next()) {
                    out.print("{\"status\":\"fail\", \"message\":\"Tên đăng nhập đã tồn tại!\"}");
                } else {
                    String sql = "INSERT INTO NguoiDung(username, password, ho_ten, role) VALUES(?, ?, ?, 0)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, u);
                    stmt.setString(2, MaHoa.bamSHA256(p));
                    stmt.setString(3, MaHoa.chuanHoaTen(n));
                    stmt.executeUpdate();
                    out.print("{\"status\":\"success\", \"message\":\"Thêm người dùng thành công!\"}");
                }
            }
            conn.close();
        } catch (Exception e) {
            out.print("{\"status\":\"error\", \"message\":\"Lỗi SQL: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}