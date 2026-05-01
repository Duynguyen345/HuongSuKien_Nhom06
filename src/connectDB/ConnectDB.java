package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectDB – Quản lý kết nối đến SQL Server.
 *
 * Cấu hình:
 *   SERVER   : Tên máy chủ SQL Server (mặc định: localhost\SQLEXPRESS)
 *   DATABASE : Tên cơ sở dữ liệu (mặc định: HSK_DB)
 *   USERNAME : Tài khoản SQL Server (mặc định: sa)
 *   PASSWORD : Mật khẩu SQL Server (mặc định: 123456)
 *
 * Nếu bạn dùng Windows Authentication, đặt USE_WINDOWS_AUTH = true
 * và không cần điền USERNAME/PASSWORD.
 */
public class ConnectDB {

    // ============================================================
    //  ⚙️  CẤU HÌNH KẾT NỐI – chỉnh sửa cho phù hợp với hệ thống
    // ============================================================
    private static final String SERVER           = "localhost:1433";
    private static final String DATABASE         = "quanlycuahangtienloi";
    private static final String USERNAME         = "sa";
    private static final String PASSWORD         = "sapassword";
    private static final boolean USE_WINDOWS_AUTH = false;    // SQL Auth, không cần sqljdbc_auth.dll
    // ============================================================

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /** Singleton connection – dùng chung toàn ứng dụng */
    private static Connection connection = null;
    
    
    
 // 1. Bổ sung biến instance để dùng Singleton
    private static ConnectDB instance = new ConnectDB();

    // 2. Bổ sung hàm getInstance() để file UI hết lỗi
    public static ConnectDB getInstance() {
        return instance;
    }

    // 3. Bổ sung hàm connect() để file UI gọi được
    public void connect() throws SQLException {
        getConnection(); // Hàm này sẽ tự động khởi tạo kết nối
    }
    
    
    
    
    
    

    /** Trả về Connection đang mở; nếu chưa có hoặc đã đóng thì tạo mới. */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = createNewConnection();
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy JDBC Driver: " + e.getMessage(), e);
        }
        return connection;
    }

    /** Tạo một Connection mới tới SQL Server. */
    private static Connection createNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);

        String url;
        if (USE_WINDOWS_AUTH) {
            url = String.format(
            		"jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=quanlycuahangtienloi;encrypt=false;"
            		+ "trustServerCertificate=true;",
                SERVER, DATABASE
            );
            return DriverManager.getConnection(url);
        } else {
            url = String.format(
            		"jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=quanlycuahangtienloi;"
            		+ "encrypt=false;trustServerCertificate=true;",
                SERVER, DATABASE
            );
            return DriverManager.getConnection(url, USERNAME, PASSWORD);
        }
    }

    /** Đóng kết nối nếu đang mở. */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("[ConnectDB] Đã đóng kết nối.");
                }
            } catch (SQLException e) {
                System.err.println("[ConnectDB] Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }

    /** Kiểm tra nhanh trạng thái kết nối (dùng khi debug). */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Ngăn tạo instance
    private ConnectDB() {}
}
