package com.qfox.network;

import com.qfox.network.downloader.IoKit;
import com.qfox.network.downloader.Network;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

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
    public void testHash() throws Exception {
        byte[] md5_1 = IoKit.hash(new FileInputStream("C:\\Users\\Chang\\AppData\\Local\\Temp\\concurrent7113386470598141573.mp4"), "MD5");
        byte[] md5_2 = IoKit.hash(new FileInputStream("C:\\Users\\Chang\\AppData\\Local\\Temp\\asynchronous6498744456875679451.mp4"), "MD5");
        System.out.println(Arrays.equals(md5_1, md5_2));
    }

    @Test
    public void testHttps() throws Exception {
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .asynchronous()
                .start((downloader, total) -> System.out.println("started"))
                .progress((downloader, total, downloaded) -> System.out.println(downloaded + " / " + total))
                .finish((downloader, total) -> System.out.println("finished"))
                .complete((downloader, success, exception) -> open())
                .to(File.createTempFile("asynchronous", ".mp4"));
        lock();
    }

    private synchronized void lock() throws InterruptedException {
        this.wait();
    }

    private synchronized void open() {
        this.notify();
    }

    @Test
    public void testConcurrent() throws Exception {
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .concurrent(10)
                .start((downloader, total) -> System.out.println("started"))
                .progress((downloader, total, downloaded) -> System.out.println(downloaded + " / " + total))
                .finish((downloader, total) -> System.out.println("finished"))
                .complete((downloader, success, exception) -> open())
                .to(File.createTempFile("concurrency", ".mp4"));
        lock();
    }

}
