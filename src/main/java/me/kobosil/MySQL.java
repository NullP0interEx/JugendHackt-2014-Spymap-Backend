package me.kobosil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * User: Roman@NullP0interEx
 * Date: 13.09.2014
 * Time: 08:10
 * Root: me.kobosil@Spymap
 */
public class MySQL {
    public static final Logger LOGGER = Logger.getLogger("ServerlistConnector");
    private Connection con;

    public void connect(String dbHost, int dbPort, String database, String dbUser, String dbPassword) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + database + "?" + "user=" + dbUser + "&"
                + "password=" + dbPassword);
    }
    public boolean isConnected() throws SQLException {
        return con != null && !con.isClosed();
    }
}
