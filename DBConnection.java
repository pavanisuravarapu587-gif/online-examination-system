package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** JDBC steps: load driver -> get Connection -> create Statement -> execute -> process ResultSet -> close. */
public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/examdb";
    private static final String USER = "root";
    private static final String PASS = "password";   // <-- change this

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");     // Type-4 (pure Java) driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found. Add mysql-connector-j.jar to the classpath.", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
