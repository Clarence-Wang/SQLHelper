import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by apple on 15/8/12.
 */
public class SqlHelper {

    //定义需要的资源变量
    //当访问量不大时可以定义为static，但是当访问量变大时，定义为static会影响并发
    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static CallableStatement cs = null;

    //相关的连接信息，用户名和密码
    private static String driver = "";
    private static String url = "";
    private static String username = "";

    //配置文件
    static Properties pp = null;
    //读取配置文件
    static FileInputStream fis = null;

    //加载驱动，只需要加载一次，一旦创建该类就会被调用
    static{
        try{
            //读取文件
            pp = new Properties();
            fis = new FileInputStream("dbinfo.properties");
            pp.load(fis);
            url = pp.getProperty("url");
            driver = pp.getProperty("driver");
            username = pp.getProperty("username");

            Class.forName(driver);
        }catch (Exception e){
            e.printStackTrace();
        }finally {

            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //捕获之后置空
            fis = null;
        }
    }

    public static Connection getConn(){
        return conn;
    }
    public static PreparedStatement getPs(){
        return ps;
    }
    public static ResultSet getRs() {
        return rs;

    }

    //得到连接
    public static Connection getConnection() throws SQLException {
        conn = DriverManager.getConnection(url, username, "");
        return conn;
    }

    //查询语句
    //返回ResultSet的集合
    public static ResultSet executeQuery(String sql, String[] parameters){

        try{
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            if (parameters != null){
                for (int i = 0; i < parameters.length; i++)
                {
                    ps.setString(i + 1, parameters[i]);
                }
            }
            rs = ps.executeQuery();
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {

        }
        return rs;
    }

    //对于多条sql语句，带上事物操作
    public static void executeUpdate(String[] sql, String[][] parameters) {
        try {
            //获得连接
            conn = getConnection();

            //此时传入的是多条sql语句，考虑到事物，设置为不自动提交
            conn.setAutoCommit(false);

            //在这里处理
            for (int i = 0; i < sql.length; i++)
            {
                if (parameters[i] != null)
                {
                    ps = conn.prepareStatement(sql[i]);
                    for(int j = 0; j < parameters[i].length; j++)
                    {
                        ps.setString(j + 1, parameters[i][j]);
                    }
                    //每条语句处理完之后进行查询
                    ps.executeUpdate();
                }
            }
            //在这里提交
            conn.commit();

        } catch (Exception e)
        {
            e.printStackTrace();//开发语句
            //在事务中若是出现异常，则要进行事务回滚
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            throw new RuntimeException(e.getMessage());
        }finally {
            //关闭资源
            close(rs, ps, conn);
        }
    }

    //对于一个的update／delete／insert
    //一条sql语句
    //参数说明 sql: sql语句   parameters:在sql里面的问号对应的String对象
    public static void executeUpdate(String sql, String[] parameters){
        //创建一个preparedSta..
        try{
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            //ps给 ？赋值
            if (parameters != null)
            {
                for (int i = 0; i < parameters.length; i++)
                {
                    ps.setString(i + 1, parameters[i]);
                }
            }

            //执行update语句
            ps.executeUpdate();

        }catch (Exception e)
        {
            e.printStackTrace();//开发语句
            //抛出运行异常, 可以给调用该函数的函数一个选择
            //可以选择处理，也可以选择放弃处理
            throw new RuntimeException(e.getMessage());
        }finally {
            //关闭资源
            close(rs, ps, conn);
        }
    }

    //单纯的sql语句
    public static void executeUpdate(String sql){
        try{
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

        }catch (Exception e)
        {
            e.printStackTrace();//开发语句
            //抛出运行异常, 可以给调用该函数的函数一个选择
            //可以选择处理，也可以选择放弃处理
            throw new RuntimeException(e.getMessage());
        }finally {
            //关闭资源
            close(rs, ps, conn);
        }
    }


    //存储过程 有返回值 等到数据库再完整的看完一遍再写有返回值的存储过程

    //调用存储过程  无返回值
    public static void callPro1(String sql, String [] parameters)
    {
        try
        {
            conn = getConnection();
            cs = conn.prepareCall(sql);

            if (parameters != null)
            {
                for (int i = 0;i < parameters.length; i++)
                {
                    cs.setObject(i + 1, parameters[i]);
                }
            }
            cs.execute();


        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally{
            //关闭资源
            close(rs, ps, conn);
        }
    }

    //关闭资源单独函数
    public static void close(ResultSet rs, Statement ps, Connection conn){
        if (rs != null)
        {
            try {
                rs.close();
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }

        if (ps != null)
        {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ps = null;
        }

        if (conn != null)
        {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }

    }

}
