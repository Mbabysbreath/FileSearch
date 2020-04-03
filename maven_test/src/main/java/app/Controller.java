package app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import task.*;
import util.DBUtil;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 控制器类，用来控制下面我们即将用来构建界面的FXML文件中的一系列动作，
 * 即所谓的控制代码
 * 控制app.fxml中的作用）。
 */
//JavaFX程序中我们可以使用FXML文件编写前台界面，使用FXMLLoader类将FXML文件绑定到主程序。
//使用一个Controller类和@FXML注解将操作的逻辑绑定到FXML文件中的界面元素。
public class Controller implements Initializable {
//对应刚才编辑的fx:id里面的id--app.fxml

   // @FXML注解用于说明该变量或者方法可以在FXML文件中进行访问。
    @FXML
    private GridPane rootPane;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<FileMeta> fileTable;

    @FXML
    private Label srcDirectory;//显示选择目录的标签

    private Thread task;
//初始化控制器

    /**
     *在控制器的根元素被完全处理后调用，以初始化控制器。
     * @param location 解析根对象的相对路径的位置
     * @param resources 本地化根对象的资源
     */
    public void initialize(URL location, ResourceBundle resources) {
        //界面初始化时，需要初始化数据库及表

        DBinit.init();
        // 添加搜索框监听器，内容改变时执行监听事件,重新刷新表格
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                freshTable();
                System.out.println("输入的文字为："+newValue);
            }
        });
    }
/*
点击目录发生的事件
 */
    public void choose(Event event) {
        // 选择文件目录
        //提供对标准目录选择器对话框的支持。这些对话框具有独立于JavaFX的平台UI组件的外观和感觉。
        //DirectoryChooser用来选择一个文件夹，FileChooser用来选择文件
        DirectoryChooser directoryChooser=new DirectoryChooser();
        Window window = rootPane.getScene().getWindow();
        //返回用户选择的目录下的文件
        File file = directoryChooser.showDialog(window);
        if(file == null)
            return;
        // 获取选择的目录路径的字符串形式，并显示
        String path = file.getPath();
        //将路径显示在标签中
        srcDirectory.setText(path);

        //选择了目录，就需要执行目录的扫描任务：将该目录下的所有的子文件和子文件夹都扫描出来
        if (task != null) {
            task.interrupt();
        }
        task=new Thread(new Runnable(){

            @Override
            public void run() {
                //文件扫描回调接口，做文件夹下一级子文件和文件夹保存数据库的操作
                ScanCallback callback=new FileSave();
                FileScanner scanner=new FileScanner(callback);
                try {
                    System.out.println("执行文件扫描任务");
                    //为了提高效率，多线程执行扫描任务
                    scanner.scan(path);
                    //TODO
                    //等待文件扫描任务执行完毕,即需要阻塞等待
                   // System.out.println("等待扫描任务结束："+path);
                    scanner.waitFinish();
                    System.out.println("任务执行完毕，刷新表格文件");
                    //刷新表格，将扫描出来的子文件及子文件夹都展示在表格里
                    freshTable();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        task.start();
    }

    // 刷新表格数据--选择目录或搜索框
    private void freshTable(){
        ObservableList<FileMeta> metas = fileTable.getItems();
        metas.clear();
        //如果选择了某个目录，代表需要再根据搜索框的内容，来进行数据库文件信息的查询
        String dir = srcDirectory.getText();
        if (dir != null && dir.trim().length() != 0) {
            String content = searchField.getText();//搜索框中的内容
            //提供数据库的查询方法
            List<FileMeta> fileMetas= FileSearch.search(dir,content);
            // Collection-->List/Set-->ArrayList+LinkedList/HashSet+TreeSet
            //Map-->HashMap/Hashtable/TreeMap
            metas.addAll(fileMetas);//进行表格的刷新
        }

        //-->方法返回后javaFx表单做什么？
        //通过反射获取fileMetas类型中的属性（app.fxml文件中定义的属性
    }
}