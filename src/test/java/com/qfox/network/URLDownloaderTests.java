package com.qfox.network;

import com.qfox.network.downloader.*;
import org.junit.Test;

import java.io.File;

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
        File file = File.createTempFile("Form", ".tmp");
        URLDownloader
                .download("https://image3.myjuniu.com/513d03b1665344e9a629d96dfaae6f63_dev_91fdfad6a7f1d42cd005a94c312caa9d")
                .block()
                .to(file);
        System.out.println(file);
    }

    @Test
    public void testConcurrent() throws Exception {
        URLDownloader.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .concurrent(3)
                .times(3)
                .listener(new ListenerAdapter() {

                    @Override
                    public void start(Downloader<?> downloader, long total) {
                        System.out.println(System.currentTimeMillis());
                    }

                    @Override
                    public void progress(Downloader<?> downloader, long total, long downloaded) {
                        System.out.println(downloaded + "/" + total);
                    }

                    @Override
                    public void finish(Downloader<?> downloader, long total) {
                        System.out.println(System.currentTimeMillis());
                    }

                })
                .callback(new CallbackAdapter() {
                    @Override
                    public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    }
                })
                .to("/Users/yangchangpei/Documents/download.mp4");
        Thread.sleep(1000 * 1000);
    }

}
