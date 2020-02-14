import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author zhaomin
 * @date 2020/2/14 11:11
 * 等待所有线程执行完毕：
 * 1.CountDownLatch:
 * 初始化一个数值，countDown()可以对数值进行i--操作
 *               await（）一直等待
 * 2.Semaphore：
 *     release():
 *     acquire();
 *
 */
public class WaitTest {

    private static int COUNT=5;
    //参数为线程个数
    private  static CountDownLatch LATCH =new CountDownLatch(COUNT);
    private static Semaphore  SEMAPHORE =new Semaphore(0);
    public static void main(String[] args) throws InterruptedException {
        for(int i=0;i<COUNT;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                 //   LATCH.countDown();//线程数减一
                    SEMAPHORE.release();//颁发一定数量许可证，无参就是颁发一个数量
                }
            }).start();
        }
        //保证在所有子线程执行完毕后再执行以下代码
        //LATCH.await();//await()会阻塞并一直等待，直到LATCH的值==0
        SEMAPHORE.acquire(COUNT);//阻塞并等待一定数量的许可，无参就等待一个资源许可
        System.out.println(Thread.currentThread().getName());
    }
}
