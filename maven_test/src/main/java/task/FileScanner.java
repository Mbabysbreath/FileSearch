package task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaomin
 * @date 2020/2/13 12:03
 */
public class FileScanner {
    //1.核心线程数：始终运行的线程数量--正式工
    //2.最大线程数：有新任务，并且当前允许线程数小于最大线程数，会创建新的线程来处理任务--临时工+正式工
    //3-4:超过3这个数量,4这个时间单位，2-1(最大线程数-核心线程数)这些线程就会关闭--时间一到，解雇临时工
    //5.工作的阻塞队列
    //6.如果超出工作队列的长度，任务要处理的方式
    private ThreadPoolExecutor POOL=new ThreadPoolExecutor(3,3,0, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(),new ThreadPoolExecutor.CallerRunsPolicy());

    public void scan(String path) {
    }
}
