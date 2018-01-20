package com.qfox.network;

import com.qfox.network.downloader.AsynchronousDownloader;
import com.qfox.network.downloader.CallbackAdapter;
import com.qfox.network.downloader.URLDownloader;
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

    private synchronized void lock() throws InterruptedException {
        this.wait();
    }

    private synchronized void open() {
        this.notify();
    }

    @Test
    public void testConcurrent() throws Exception {
//        ExecutorService executor = Executors.newCachedThreadPool();
//        URLDownloader.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
//                .asynchronous(executor)
//                .progress(((downloader, total, downloaded) -> System.out.println(downloaded + " / " + total)))
//                .complete((downloader, success, exception) -> open())
//                .to("C:\\Users\\Administrator\\AppData\\Local\\Temp\\download12.mp4");
//        lock();
    }

}
