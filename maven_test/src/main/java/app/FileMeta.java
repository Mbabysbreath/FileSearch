package app;

import util.PinyinUtil;
import util.Util;

import java.io.File;
import java.util.Date;

public class FileMeta {

    private String name;//文件名称
    private String path;//文件父目录路径
    private Long size;//文件大小
    private Date lastModified;//文件上次修改时间
    private Boolean isDirectory;//是否是文件夹
    private String sizeText;//给客户端控件使用，和app.fxml中定义的名称一致
    private String lastModifiedText;//和app.fxml定义的一致
    private String pinyin;//文件名拼音
    private String pinyinFirst;//文件名拼音首字母

    //通过文件设置属性
    public FileMeta(File file){
        this(file.getName(),file.getParent(),file.isDirectory(),
                file.length(),new Date(file.lastModified()));
    }

    //通过数据库获取的数据设置FileMeta
    public FileMeta(String name,String path,Boolean isDirectory,
                    long size,Date lastModified){
        this.name=name;
        this.path=path;
        this.isDirectory=isDirectory;
        this.size=size;
        this.lastModified=lastModified;
        if(PinyinUtil.containsChinese(name)){
            String[] pinyins=PinyinUtil.get(name);
            pinyin = pinyins[0];
            pinyinFirst = pinyins[1];
        }
        //客户端表格控件文件大小，文件上次修改时间的设置
        sizeText= Util.parseSize(size);
        lastModifiedText=Util.parseDate(lastModified);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getSizeText() {
        return sizeText;
    }

    public void setSizeText(String sizeTest) {
        this.sizeText = sizeTest;
    }

    public String getLastModifiedText() {
        return lastModifiedText;
    }

    public void setLastModifiedText(String lastModifiedText) {
        this.lastModifiedText = lastModifiedText;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst;
    }

    public Boolean getDirectory() {
        return isDirectory;
    }

    public void setDirectory(Boolean directory) {
        isDirectory = directory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileMeta fileMeta = (FileMeta) o;

        if (name != null ? !name.equals(fileMeta.name) : fileMeta.name != null) return false;
        if (path != null ? !path.equals(fileMeta.path) : fileMeta.path != null) return false;
        return isDirectory != null ? isDirectory.equals(fileMeta.isDirectory) : fileMeta.isDirectory == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (isDirectory != null ? isDirectory.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
