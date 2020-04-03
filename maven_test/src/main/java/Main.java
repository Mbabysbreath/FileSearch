import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    // 所有JavaFX应用程序的主要入口点。
// 2.在init方法返回之后，以及在系统准备好应用程序开始运行之后，
// 3.将调用start方法。
//4.作用：设置应用程序的场景
    @Override
    public void start(Stage primaryStage) throws Exception {
        //读取fxml文件,使用FXMLLoader类进行加载
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("app.fxml"));
        //窗口的标题
        primaryStage.setTitle("MyFile");
        //主窗口加载的场景，场景里面的描述文件
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }
    public static void main(String[] args) {
        //1.启动应用程序,不能被调用一次以上
        launch(args);
    }
}