package task;

import java.io.File;

/**
 * @author zhaomin
 * @date 2020/2/14 11:50
 */
public interface ScanCallback {
    /**
     * 对于文件夹进行扫描回调，处理文件夹，将文件夹下一级的子文件夹和子文件存到数据库
     */
    void callback(File dir);
}
