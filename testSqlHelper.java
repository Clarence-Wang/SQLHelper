import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by apple on 15/8/13.
 */
public class testSqlHelper {

    public static void main(String[] args){
        //测试单行案例
//        String sql = "insert into user(username, email, grade, password) values (?, ?, ?, ?)";
//        String[] parameters = {"bbbb", "bbbb@souhu.com", "1", "123"};
//        SqlHelper.executeUpdate(sql, parameters);
//        SqlHelper.executeUpdate("delete from user where username = 'bbbb1'");
        try {
            ResultSet rs = SqlHelper.executeQuery("select * from user", null);
            while (rs.next())
            {
                System.out.println(rs.getInt("id"));
                System.out.println(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            SqlHelper.close(SqlHelper.getRs(), SqlHelper.getPs(), SqlHelper.getConn());
        }
    }

}
