package task;

import app.FileMeta;
import util.DBUtil;
import util.PinyinUtil;
import util.Util;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaomin
 * @date 2020/2/14 11:52
 */
public class FileSave implements ScanCallback {
    @Override
    public void callback(File dir){
        //文件夹下一级子文件和子文件夹保存到数据库
        //获取本地目录下一级子文件和子文件夹
        //集合框架中使用自定义类型，判断是否某个对象在集合存在，比对两个集合中的元素
       File[] children=dir.listFiles();
        List<FileMeta> locals=new ArrayList<>();
        if (children != null) {
            for (File child : children) {
                locals.add(new FileMeta(child));
                System.out.println(child.getPath());
                save(child);
            }
        }
        //获取数据库保存的dir目录的下一级子文件和子文件夹
        //本地有，数据没有，插入数据库
        //TODO List<File>

        //数据库有，本地没有，数据库删除
        //TODO

    }

    private List<FileMeta>  query(File dir) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<FileMeta> metas = new ArrayList<>();
        try {
            //1，创建数据库连接
            connection = DBUtil.getConnection();
            String sql="select name,path,is_directory,size,last_modified"+
                    " from file_meta where path=?";
            //2.创建jdbc操作对statement
            ps = connection.prepareStatement(sql);
            ps.setString(1,dir.getPath());
            //3执行sql语句
            rs = ps.executeQuery();
            //4.处理结果集
            while (rs.next()) {
                String name = rs.getString("name");
                String path = rs.getString("path");
                Boolean isDirectory = rs.getBoolean("is_directory");
                Long size = rs.getLong("size");
                Timestamp lastModified = rs.getTimestamp("last_modified");
                FileMeta meta = new FileMeta(name, path, isDirectory, size,
                        new java.util.Date(lastModified.getTime()));
                System.out.printf("查询文件信息：name=%s,path=%s,is_directory=%s"+
                           "size=%s,last_modified=%s\n",name,path,String.valueOf(isDirectory),
                        String.valueOf(size), Util.parseDate(new java.util.Date(lastModified.getTime())));
                metas.add(meta);
            }
            return metas;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询文件信息失败。检查sql查询语句",e);
        }finally {

            DBUtil.close(connection, ps, rs);
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
                    "(name,path,is_directory,size,last_modified,pinyin,pinyin_first) " +
                    " values(?,?,?,?,?,?,?)";

             //2.获取sql操作命令对象Statement
            statement=connection.prepareStatement(sql);
            System.out.println("执行文件保存操作："+sql);
            statement.setString(1, file.getName());
            statement.setString(2, file.getParent());
            statement.setBoolean(3,file.isDirectory());
            statement.setLong(4, file.length());
            statement.setTimestamp(5, new Timestamp(file.lastModified()));
            String pinyin=null;
            String pinyin_first=null;
            //文件包含汉字，需要获取拼音和首字母，并保存到数据库
            if (PinyinUtil.containsChinese(file.getName())) {
                String[] pinyins = PinyinUtil.get(file.getName());
                pinyin = pinyins[0];
                pinyin_first = pinyins[1];
            }
            statement.setString(6, pinyin);
            statement.setString(7,pinyin_first);

            //3.执行sql
                statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("文件保存失败，检查sql insert语句",e);
        } finally {
            DBUtil.close(connection,statement);
        }
    }

    public static void main(String[] args) {
        DBinit.init();
        File file=new File("C:\\Users\\zhao'min\\Desktop\\精品简历模板");
        FileSave fileSave = new FileSave();
        fileSave.save(file);
        fileSave.query(file.getParentFile());
    }
}
