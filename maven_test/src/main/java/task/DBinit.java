package task;

import util.DBUtil;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * 1.初始化数据库：
 *    数据库文件约定好放在target/everything-like.db
 *    调用DBUtil.getConnection()--可以完成数据初始化
 * 2.并且读取sql文件，
 * 3.执行sql语句来初始化表
 * @author zhaomin
 * @date 2020/2/12 23:28
 */
public class DBinit {

    public static void init(){
        /*获取target编译文件夹的路径
          通过getClassLoader().getResource()
          /getClassLoader().getResourceAsStream()这样的方法

          默认的根路径为编译文件夹的路径（target/classes）
         */
        //获取DBinit编译文件后的路径，classe用./  父类用../但这里到不了父类
        URL classesURL= DBinit.class.getClassLoader().getResource("./");
        //获取target/classes文件夹的父目录路径target
        String dir = new File(classesURL.getPath()).getParent();
        String url="jdbc:sqlite://"+dir+File.separator+"everything-like.db";
        System.out.println(url);
        /*数据库jdbc操作，sql语句的执行*/

        Connection connection=null;
        Statement statement = null;
        try {
            //1.建立数据库连接Connection
            connection = DBUtil.getConnection();
            //2.创建sql语句执行对象Statement
            statement = connection.createStatement();
            String[] sqls = readSQL();
            for (String sql : sqls) {
               // System.out.println("执行sql操作"+sql);
                //3.执行sql语句
                statement.executeUpdate(sql);
            }
            //4.如果是查询操作，获取结果集ResultSet,处理结果集
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("初始化数据库表操作失败",e);
        }finally{
            //5.释放资源
            DBUtil.close(connection,statement );
        }
    }

    public static String[] readSQL(){
        try {
            //通过ClassLoader获取流,或者通过FileInputStream获取
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("init.sql");
            //字节流转换为字符流；需要通过字节字符转换六来操作
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb=new StringBuilder();
            String  line;
            while((line=br.readLine())!=null){
                if(line.contains("--")){//去除--注释的代码
                    line=line.substring(0,line.indexOf("--"));
                }
                sb.append(line);
            }
            String[] sqls = sb.toString().split(";");
            return sqls;
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("读取sql文件错误",e);
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(readSQL()));
        init();
    }
}
