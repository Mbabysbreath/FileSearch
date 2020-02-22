package task;

import app.FileMeta;
import util.DBUtil;
import util.Util;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


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
//                System.out.println(child.getPath());
//                save(child);
            }
        }
        //获取数据库保存的dir目录的下一级子文件和子文件夹
        //TODO List<File>
        List<FileMeta> metas = query(dir);

        //数据库有，本地没有，数据库删除
        //TODO delete
        for (FileMeta meta : metas) {
            if (!locals.contains(meta)) {
                //1.meta的删除：1.删除meta信息本身
                //2.如果meta是目录，删除meta的子目录和下边的所以文件
                delete(meta);
            }
        }

        //本地有，数据没有，插入数据库
        for (FileMeta meta : locals) {
            if (!metas.contains(meta)) {
                save(meta);
            }
        }

    }

    public void delete(FileMeta meta) {
        Connection connection=null;
        PreparedStatement ps=null;
        try{
            connection=DBUtil.getConnection();

            String sql=" DELETE FROM file_meta WHERE"+
                    "(name=? AND path=? AND is_directory=?)";//删除文件本身
            if(meta.getDirectory()){//如果是文件夹，还要删除文件夹的子文件和子文件夹
                sql += " or path=?" +//匹配数据库文件夹的儿子
                        " or path like ?";//匹配数据库文件夹的孙后辈
            }
            ps=connection.prepareStatement(sql);
            ps.setString(1, meta.getName());
            ps.setString(2, meta.getPath());
            ps.setBoolean(3, meta.getDirectory());
            if (meta.getDirectory()) {
                ps.setString(4, meta.getPath()+File.separator+meta.getName());
                ps.setString(5, meta.getPath()+File.separator+meta.getName()+File.separator);
            }
            System.out.printf("删除文件信息dir=%s\n",
                    meta.getPath()+File.separator+meta.getName());
            ps.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException("删除文件信息出错，检查delete语句",e);
        }finally {
            DBUtil.close(connection,ps);
        }
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
     * @param meta 传进的目录
     */
    private void save(FileMeta meta) {

        Connection connection=null;
        PreparedStatement statement=null;

            try {

                //1.获取数据库连接
                connection = DBUtil.getConnection();
                String sql=" insert into file_meta" +
                        " (name,path,is_directory,size,last_modified,pinyin,pinyin_first) " +
                        " values(?,?,?,?,?,?,?)";

                 //2.获取sql操作命令对象Statement
                statement=connection.prepareStatement(sql);
                statement.setString(1, meta.getName());
                statement.setString(2, meta.getPath());
                statement.setBoolean(3,meta.getDirectory());
                statement.setLong(4, meta.getSize());
                //数据库保存日期类型，可以按数据库设置的日期格式，以字符串传入
                statement.setString(5, meta.getLastModifiedText());
                statement.setString(6, meta.getPinyin());
                statement.setString(7,meta.getPinyinFirst());

                //3.执行sql
                    statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("文件保存失败，检查sql insert语句",e);
            } finally {
                DBUtil.close(connection,statement);
            }
    }

    public static void main(String[] args) {

        List<FileMeta> locals=new ArrayList<>();
        locals.add(new FileMeta("你好", "C:\\Users\\zhao'min\\Desktop\\大家好", true,0, new Date()));
        locals.add(new FileMeta("大家哈珀","C:\\Users\\zhao'min\\Desktop\\大家好",true,0,new Date()));
        locals.add(new FileMeta("沟通.txt","C:\\Users\\zhao'min\\Desktop\\大家好\\你好",true,0,new Date()));

        List<FileMeta> metas = new ArrayList<>();
        metas.add(new FileMeta("你好1", "C:\\Users\\zhao'min\\Desktop\\大家好",true, 0, new Date()));
        metas.add(new FileMeta("大家哈珀","C:\\Users\\zhao'min\\Desktop\\大家好",true,0,new Date()));
        metas.add(new FileMeta("沟通.txt","C:\\Users\\zhao'min\\Desktop\\大家好\\你好1",true,0,new Date()));

        //集合中是否包含某个元素，不一定代表传入这个对象在Java内存中是同一个对象的引用
        for (FileMeta meta : locals) {
            if (!metas.contains(meta)) {
                System.out.println(meta);
            }
        }


    }

}
