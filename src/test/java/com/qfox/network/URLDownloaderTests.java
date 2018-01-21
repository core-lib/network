//package com.qfox.network;
//
//import com.qfox.network.downloader.IOUtils;
//import com.qfox.network.downloader.URLDownloader;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.Arrays;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * <p>
// * Description:
// * </p>
// * <p>
// * <p>
// * Company: 广州市俏狐信息科技有限公司
// * </p>
// *
// * @author yangchangpei 646742615@qq.com
// * @version 1.0.0
// * @date 2015年8月14日 下午4:04:04
// */
//public class URLDownloaderTests {
//
//    @Test
//    public void testHash() throws Exception {
//        byte[] md5_1 = IOUtils.hash(new FileInputStream("C:\\Users\\Chang\\AppData\\Local\\Temp\\asynchronous10210644136995453048.mp4"), "MD5");
//        byte[] md5_2 = IOUtils.hash(new FileInputStream("C:\\Users\\Chang\\AppData\\Local\\Temp\\concurrent16971484426042738101.mp4"), "MD5");
//        System.out.println(Arrays.equals(md5_1, md5_2));
//    }
//
//    @Test
//    public void testHttps() throws Exception {
//        ExecutorService executor = Executors.newCachedThreadPool();
//        URLDownloader.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
//                .asynchronous(executor)
//                .progress((downloader, total, downloaded) -> System.out.println(downloaded + " / " + total))
//                .complete((downloader, success, exception) -> open())
//                .to(File.createTempFile("asynchronous", ".mp4"));
//        lock();
//    }
//
//    private synchronized void lock() throws InterruptedException {
//        this.wait();
//    }
//
//    private synchronized void open() {
//        this.notify();
//    }
//
//    @Test
//    public void testConcurrent() throws Exception {
//        ExecutorService executor = Executors.newCachedThreadPool();
//        URLDownloader.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
//                .concurrent(executor, 3)
//                .progress((downloader, total, downloaded) -> System.out.println(downloaded + " / " + total))
//                .complete((downloader, success, exception) -> open())
//                .to(File.createTempFile("concurrent", ".mp4"));
//        lock();
//    }
//
//}
