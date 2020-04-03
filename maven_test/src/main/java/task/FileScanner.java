package task;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhaomin
 * @date 2020/2/13 12:03
 */
public class FileScanner {
    //1.核心线程数：始终运行的线程数量--正式工
    //2.最大线程数：有新任务，并且当前允许线程数小于最大线程数，会创建新的线程来处理任务--临时工+正式工
    //3-4:超过3这个数量,4这个时间单位，2-1(最大线程数-核心线程数)这些线程就会关闭--时间一到，解雇临时工
    //5.工作的阻塞队列
    //6.如果超出工作队列的长度，任务要处理的方式(4种策略)
//    private ThreadPoolExecutor POOL=new ThreadPoolExecutor(
//            3,3,0, TimeUnit.MICROSECONDS,
//            new LinkedBlockingQueue<>(),new ThreadPoolExecutor.AbortPolicy());
//多线程执行大量任务，使用线程池来提高执行效率
    private ExecutorService POOL = Executors.newFixedThreadPool(4);
    //之前多线程讲解的方法是一种快捷方式
    // private ExecutorService exe=Executors.newFixedThreadPool(4);

    //在线程执行时，待执行任务数+1， 执行完后，待执行任务数-1，
    // 开启子任务时，每个子任务都执行任务数+1操作，
    // 这样在最后可以判断出是否所有线程执行完毕。
    //计数器，不传入参数，从0开始计数
    private volatile AtomicInteger count = new AtomicInteger();

    //线程等待的锁对象--实现所有线程完毕后，再执行最后一个线程
    //private Object lock=new Object();//第一种，synchronized(lock)进行wait()等待
    // private CountDownLatch latch=new CountDownLatch(1);//第二种，await()阻塞等待知道latch=0;
    private Semaphore semaphore = new Semaphore(0);//第三种，acquire(),请求一定资源的许可证

    private ScanCallback callback;

    //为便于在线程中执行的任务有较好的扩展性，可以考虑使用接口回调的方式实现。传入时设定为文件信息保存的任务
    public FileScanner(ScanCallback callback) {
        this.callback = callback;
    }


    /**
     * 扫描文件目录
     * 最开始：不知道有多少子文件夹，不知道应启动多少线程
     *
     * @param path
     */
    public void scan(String path) {
        //启动根目录扫描任务,计数器++i
        count.incrementAndGet();//++i
        doScan(new File(path));
    }

    /**
     * @param dir 递归遍历文件夹
     */

    private void doScan(File dir) {

        POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.callback(dir);//真正保存到数据库
                    File[] children = dir.listFiles();
                    if (children != null) {
                        for (File child : children) {
                            //如果是文件夹，递归处理
                            if (child.isDirectory()) {
                                //System.out.println("文件夹"+child.getPath());
                                count.incrementAndGet();//线程加1
                                System.out.println("当前任务数：" + count.get());
                                doScan(child);
                            }
                        }
                    }
                } finally {
                    int r = count.decrementAndGet();//线程减一
                    if (r == 0) {
                        /*第一种实现*/
//                        synchronized (lock){
//                            lock.notify();
//                        }
                        /*第二种*/
//                        latch.countDown();
                        /*第三种*/
                        semaphore.release();
                    }
                }
            }
        });
    }

    /**
     * 等待扫描任务结束（scan)
     * 多线程的任务等待：thread.start()
     */

    public void waitFinish() throws InterruptedException {
//        synchronized (lock) {
//            lock.wait();
//        }
//        latch.await();

        try {
            semaphore.acquire();//无参默认是请求1个，只有等待到一个，才会向下执行，否则阻塞
        } finally {
            System.out.println("关闭线程池");
            //阻塞等待，直到任务完成后需要关闭线程池
            POOL.shutdownNow();//正常执行完毕，关闭线程
        }

    }

    /**
     * 线程池关闭
     */
    public void shutdown() {
        System.out.println("关闭线程池");
        // POOL.shutdown();//内部实现原理是通过内部Thread.interrpt()来中断
        //新传任务不再接收，但是目前所有的任务（所有线程中执行的任务+工作队列中的任务）还要执行完毕
        POOL.shutdownNow();//内部实现原理通过内部thread.interrpt()，线程不安全】
        //新传任务不再接收
        //目前的任务（所有线程中执行的任务）判断是否能停止，
        // 如果能够停止，就结束任务；
        //如果不能，就执行完再停止
        //工作队列中的任务是直接丢弃
    }
}
