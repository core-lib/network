package com.qfox.network;

import com.qfox.network.downloader.*;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 *
 * @author yangchangpei 646742615@qq.com
 * @version 1.0.0
 * @date 2015年8月14日 下午4:04:04
 */
public class URLDownloaderTests {

    @Test
    public void testHttps() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        final File file = File.createTempFile("Form", ".tmp");
        URLDownloader
                .download("http://image3.myjuniu.com/513d03b1665344e9a629d96dfaae6f63_dev_91fdfad6a7f1d42cd005a94c312caa9d")
                .asynchronous(executor)
                .callback(new CallbackAdapter() {
                    @Override
                    public void success(AsynchronousDownloader<?> downloader) {
                        System.out.println(file);
                    }
                })
                .to(file);
        Thread.sleep(10000);
    }

    @Test
    public void testConcurrent() throws Exception {
        final Object lock = new Object();
        ExecutorService executor = Executors.newCachedThreadPool();
        URLDownloader.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .concurrent(executor, 3)
                .times(3)
                .listener(new Listener() {
                    public void start(Downloader<?> downloader, long total) {

                    }

                    public void progress(Downloader<?> downloader, long total, long downloaded) {
                        System.out.println(downloaded + " / " + total);
                    }

                    public void finish(Downloader<?> downloader, long total) {

                    }
                })
                .callback(new CallbackAdapter() {
                    @Override
                    public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                })
                .to("C:\\Users\\Administrator\\AppData\\Local\\Temp\\download5.mp4");
        synchronized (lock) {
            lock.wait();
        }
    }

}
