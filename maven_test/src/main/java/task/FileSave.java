package task;

import util.DBUtil;
import util.PinyinUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author zhaomin
 * @date 2020/2/14 11:52
 */
public class FileSave implements ScanCallback {
    @Override
    public void callback(File dir){
        //文件夹下一级子文件和子文件夹保存到数据库
       File[] children=dir.listFiles();
        if (children != null) {
            for (File child : children) {
                System.out.println(child.getPath());
                save(child);
            }
        }
    }

    /**
     * 文件信息保存到数据库
     * @param file
     */
    private void save(File file) {

        Connection connection=null;
        PreparedStatement statement=null;

        try {

            //1.获取数据库连接
             connection = DBUtil.getConnection();
             String sql="insert into file_meta" +
                     "(name,path,size,last_modified,pinyin,pinyin_first) "+
                     " VALUES (?,?,?,?,?,?)";

             //2.获取sql操作命令对象Statement
            statement=connection.prepareStatement(sql);
            System.out.println("执行文件保存操作："+sql);
            statement.setString(1, file.getName());
            statement.setString(2, file.getParent());
            statement.setLong(3, file.length());
            statement.setTimestamp(4, new Timestamp(file.lastModified()));
            String pinyin=null;
            String pinyin_first=null;
            //文件包含汉字，需要获取拼音和首字母，并保存到数据库
            if (PinyinUtil.containsChinese(file.getName())) {
                String[] pinyins = PinyinUtil.get(file.getName());
                pinyin = pinyins[0];
                pinyin_first = pinyins[1];
            }
            statement.setString(5, pinyin);
            statement.setString(6,pinyin_first);

                //3.执行sql
                statement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("文件保存失败，检查sql insert语句",e);
        } finally {
            DBUtil.close(connection,statement);
        }
    }
}
