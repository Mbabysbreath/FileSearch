import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhaomin
 * @date 2020/2/14 10:26
 */
public class ThreadTest {
    //多线程下线程安全的计数器
    private static AtomicInteger COUNT=new AtomicInteger();
   /* public static void main(String[] args) {
        for(int i=0;i<20;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j=0;j<10000;j++){
                        COUNT.incrementAndGet();
                    }
                }
            }).start();
        }
        while(Thread.activeCount()>1){
            Thread.yield();
        }
        System.out.println(COUNT);
    }*/
   public static void main(String[] args) {
       for (int i = 0; i < 20; i++) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   for (int j = 0; j < 10000; j++) {
                       COUNT.incrementAndGet();//i++操作
                       // COUNT.getAndIncrement();
                   }
               }
           }).start();
       }
       while (Thread.activeCount() > 1) {
           Thread.yield();

       }
       System.out.println(COUNT.get());
}
}
