package indi.yolo.sample.jdbc;

import com.mysql.jdbc.ResultSetImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * mysql5:
 * url = jdbc:mysql://localhost:3306/user?useUnicode=true&characterEncoding=utf8
 * driver = com.mysql.jdbc.Driver
 * <p>
 */

public class Mysql5Test {

    public static void main(String[] args) {
        Mysql5Test mysql5Test = new Mysql5Test();
        mysql5Test.simpleQuery();
    }

    /**
     * 一次性全表数据拉取出来
     * 大字段数据byte[]
     */
    public void simpleQuery() {
        String url = "jdbc:mysql://192.168.1.253:3306/test";
        String sql = "select * from person limit 2";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof ResultSetImpl) {
                Method getUpdateCount = resultSet.getClass().getMethod("getUpdateCount");
                long l = (long) getUpdateCount.invoke(resultSet);
                System.out.println(l);
            }
            resultSet.close();
            System.out.println(conn.getAutoCommit());
            conn.close();
            Statement stmt1 = conn.createStatement();
            System.out.println(conn.getAutoCommit());
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * streaming resultSet未携带数据需要每次next取数据
     */
    public void streamingQuery() {
        String url = "jdbc:mysql://192.168.1.116:3306/test";
        String sql = "select * from gongsi";
        try (Connection conn = DriverManager.getConnection(url, "test", "");
             Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(Integer.MIN_VALUE);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof ResultSetImpl) {
                Method getUpdateCount = resultSet.getClass().getMethod("getUpdateCount");
                long l = (long) getUpdateCount.invoke(resultSet);
                System.out.println(l);  //-1
            }
            resultSet.close();
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * cursor resultSet未携带数据需要每次next取数据
     * 添加useCursorFetch=true且statement.setFetchSize(resultSet.setFetchSize无效) 否则仍是static rows
     * fetchSize数据在服务端,驱动仍是一次一条数据
     */
    public void cursorQuery() {
        String url = "jdbc:mysql://192.168.1.116:3306/test?useCursorFetch=true";
        String sql = "select * from gongsi";
        try (Connection conn = DriverManager.getConnection(url, "test", "");
             Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(2);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof ResultSetImpl) {
                Method getUpdateCount = resultSet.getClass().getMethod("getUpdateCount");
                long l = (long) getUpdateCount.invoke(resultSet);
                System.out.println(l);  //-1
            }
            resultSet.close();
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void mysql_procedure() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/test";
        String create = "create procedure add_pro(a int,b int,out sum int) begin set sum = a + b; end";
        String call = "{call add_pro(?,?,?)}";
        String drop = "drop procedure if exists add_pro";
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, "user", "pwd")) {
                CallableStatement cstmt = null;

                cstmt = conn.prepareCall(create);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();

                cstmt = conn.prepareCall(call);
                cstmt.setInt(1, 4);
                cstmt.setInt(2, 5);
                cstmt.registerOutParameter(3, Types.INTEGER);
                /*ResultSet rs = cstmt.executeQuery();
                rs.next(); java.sql.SQLException: ResultSet is from UPDATE. No Data.*/
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                System.out.println(cstmt.getInt(3));
                cstmt.close();

                cstmt = conn.prepareCall(drop);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mysql_procedure_rs() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/fea_flow";
        String create = "CREATE PROCEDURE pro_findById(IN eid INT) " +
                "BEGIN SELECT job_name,service_id FROM lgjob WHERE job_id=eid; END";
        String call = "CALL pro_findById(?)";
        String drop = "drop procedure if exists pro_findById";
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, "user", "pwd")) {
                CallableStatement cstmt = null;

                cstmt = conn.prepareCall(create);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();

                cstmt = conn.prepareCall(call);
                cstmt.setInt(1, 11900);
                // execute,executeUpdate,executeQuery均可以
                ResultSet rs = cstmt.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("job_name");
                    int service_id = rs.getInt("service_id");
                    System.out.println(name + "," + service_id);
                }
                cstmt.close();

                cstmt = conn.prepareCall(drop);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
