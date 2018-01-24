Network
=======

Java HTTP / HTTPS Downloader Implementation
> ## Installation
```
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
    ...
<repositories>

<dependencies>
    ...
    <dependency>
        <groupId>com.github.core-lib</groupId>
        <artifactId>network</artifactId>
        <version>v1.2.1</version>
    </dependency>
    ...
</dependencies>
```
> ## Usage
* Block Mode
```
    @Test
    public void testBlock() throws Exception {
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .block()
                .to(File.createTempFile("network", ".mp4"));
    }
```
* Async Mode
```
    @Test
    public void testAsynchronous() throws Exception {
        final Object lock = new Object();
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .asynchronous()
                .callback(new CallbackAdapter() {
                    @Override
                    public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                })
                .to(File.createTempFile("network", ".mp4"));
        synchronized (lock) {
            lock.wait();
        }
    }
```
* Resumable Mode
```
    @Test
    public void testResumable() throws Exception {
        final Object lock = new Object();
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .resumable(3) // max retry 3 times if error occur when downloading
                .callback(new CallbackAdapter() {
                    @Override
                    public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                })
                .to(File.createTempFile("network", ".mp4"));
        synchronized (lock) {
            lock.wait();
        }
    }
```
* Concurrent Mode
```
    @Test
    public void testConcurrent() throws Exception {
        final Object lock = new Object();
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .concurrent(3) // use 3 threads to download the same resource in the same time, but the server must supports it
                .times(3) // every thread max retry 3 times if error occur when downloading
                .callback(new CallbackAdapter() {
                    @Override
                    public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                })
                .to(File.createTempFile("network", ".mp4"));
        synchronized (lock) {
            lock.wait();
        }
    }
```
