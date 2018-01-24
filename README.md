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
> ## Usages
* ### Block Mode
```
    @Test
    public void testBlock() throws Exception {
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .block()
                .to(File.createTempFile("network", ".mp4"));
    }
```
* ### Async Mode
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
* ### Resumable Mode
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
* ### Concurrent Mode
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
> ## Download progress listening
```
    @Test
    public void testListen() throws Exception {
        final Object lock = new Object();
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .asynchronous()
                .listener(new ListenerAdapter() {
                    @Override
                    public void start(Downloader<?> downloader, long total) {
                        System.out.println("download started and resource size is " + total + " bytes");
                    }

                    @Override
                    public void progress(Downloader<?> downloader, long total, long downloaded) {
                        System.out.println("downloading " + downloaded + " / " + total);
                    }

                    @Override
                    public void finish(Downloader<?> downloader, long total) {
                        System.out.println("download finished");
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
                .to(File.createTempFile("network", ".mp4"));
        synchronized (lock) {
            lock.wait();
        }
    }
```
> ## Download to OutputStream / Output
```
    @Test
    public void testToOutputStream() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .block()
                .to(out);
    }
    
    @Test
    public void testToOutput() throws Exception {
        final OutputStream out = new FileOutputStream(File.createTempFile("network", ".mp4"));
        final DataOutput output = new DataOutputStream(out);
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .block()
                .to(output);
    }
```
> ## Specify download thread pool
```
    @Test
    public void testSpecifyThreadPool() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(12);
        Network.setDefaultExecutor(executor);
    }
    
    @Test
    public void testSpecifyCustomThreadPool() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(12);
        final Object lock = new Object();
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .asynchronous(executor)
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

> ## Lambda
```
    private synchronized void lock() throws InterruptedException {
        this.wait();
    }

    private synchronized void open() {
        this.notify();
    }

    @Test
    public void testLambda() throws Exception {
        Network.download("http://qfox.oss-cn-shenzhen.aliyuncs.com/upload/video/CUSHOW/fd84dffb-f004-4f4c-9b15-780d1b8e27af.mp4")
                .asynchronous()
                .start((downloader, total) -> System.out.println("download started and resource size is " + total + " bytes"))
                .progress((downloader, total, downloaded) -> System.out.println("downloading " + downloaded + " / " + total))
                .finish((downloader, total) -> System.out.println("download finished"))
                .success(downloader -> System.out.println("download success"))
                .failure((downloader, exception) -> exception.printStackTrace())
                .complete((downloader, success, exception) -> open())
                .to(File.createTempFile("network", ".mp4"));
        lock();
    }
```
