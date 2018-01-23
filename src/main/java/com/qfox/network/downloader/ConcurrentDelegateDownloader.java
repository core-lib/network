package com.qfox.network.downloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

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
 * @date 2015年8月13日 下午7:57:32
 */
public class ConcurrentDelegateDownloader implements ConcurrentDownloader<ConcurrentDelegateDownloader>, Listener, Callback {
    private ResumableDownloader<?> delegate;
    private int concurrent = 5;
    private CallbackWrapper callback = new CallbackWrapper();
    private ListenerWrapper listener = new ListenerWrapper();
    private Range range = Range.ZERO;
    private boolean override = true;
    private boolean aborted;
    private List<ResumableDownloader<?>> downloaders = new ArrayList<ResumableDownloader<?>>();

    private volatile File file;
    private volatile boolean started = false;
    private final Object lock = new Object();

    private volatile long all;
    private AtomicInteger finish = new AtomicInteger();

    private AtomicInteger complete = new AtomicInteger();
    private volatile boolean success = true;
    private volatile Exception exception;

    private volatile long[] progresses;

    ConcurrentDelegateDownloader(ResumableDownloader<?> delegate) {
        super();
        this.delegate = delegate;
    }

    public ConcurrentDelegateDownloader concurrent(int concurrent) {
        if (concurrent < 1) {
            throw new IllegalArgumentException("concurrence must not lesser than 1");
        }
        this.concurrent = concurrent;
        return this;
    }

    public int concurrent() {
        return concurrent;
    }

    public ConcurrentDelegateDownloader times(int times) {
        delegate.times(times);
        return this;
    }

    public int times() {
        return delegate.times();
    }

    public ConcurrentDelegateDownloader callback(Callback callback) {
        this.callback.setCallback(callback);
        return this;
    }

    public Callback callback() {
        return callback.getCallback();
    }

    public ConcurrentDelegateDownloader success(OnSuccess onSuccess) {
        this.callback.setOnSuccess(onSuccess);
        return this;
    }

    public ConcurrentDelegateDownloader failure(OnFailure onFailure) {
        this.callback.setOnFailure(onFailure);
        return this;
    }

    public ConcurrentDelegateDownloader complete(OnComplete onComplete) {
        this.callback.setOnComplete(onComplete);
        return this;
    }

    public ConcurrentDelegateDownloader use(ExecutorService executor) {
        delegate.use(executor);
        return this;
    }

    public ConcurrentDelegateDownloader tag(int tag) {
        delegate.tag(tag);
        return this;
    }

    public int tag() {
        return delegate.tag();
    }

    public ConcurrentDelegateDownloader from(String url) throws MalformedURLException {
        delegate.from(url);
        return this;
    }

    public ConcurrentDelegateDownloader from(String protocol, String host, int port, String file) throws MalformedURLException {
        delegate.from(protocol, host, port, file);
        return this;
    }

    public ConcurrentDelegateDownloader from(String protocol, String host, String file) throws MalformedURLException {
        delegate.from(protocol, host, file);
        return this;
    }

    public ConcurrentDelegateDownloader from(URL url) {
        delegate.from(url);
        return this;
    }

    public URL url() {
        return delegate.url();
    }

    public ConcurrentDelegateDownloader range(Range range) {
        delegate.range(range);
        this.range = range;
        return this;
    }

    public Range range() {
        return range;
    }

    public ConcurrentDelegateDownloader buffer(int buffer) {
        delegate.buffer(buffer);
        return this;
    }

    public int buffer() {
        return delegate.buffer();
    }

    public ConcurrentDelegateDownloader timeout(int timeout) {
        delegate.timeout(timeout);
        return this;
    }

    public int timeout() {
        return delegate.timeout();
    }

    public ConcurrentDelegateDownloader listener(Listener listener) {
        this.listener.setListener(listener);
        return this;
    }

    public Listener listener() {
        return listener.getListener();
    }

    public ConcurrentDelegateDownloader start(WhenStart whenStart) {
        this.listener.setWhenStart(whenStart);
        return this;
    }

    public ConcurrentDelegateDownloader progress(WhenProgress whenProgress) {
        this.listener.setWhenProgress(whenProgress);
        return this;
    }

    public ConcurrentDelegateDownloader finish(WhenFinish whenFinish) {
        this.listener.setWhenFinish(whenFinish);
        return this;
    }

    public ConcurrentDelegateDownloader override(boolean override) {
        this.override = override;
        return this;
    }

    public boolean override() {
        return override;
    }

    public void to(String filepath) throws IOException {
        if (filepath == null) {
            throw new IllegalArgumentException("unresolved file path to output");
        }
        to(new File(filepath));
    }

    public void to(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("destination file must not be null");
        }
        if (override) file.delete();
        if (!file.exists()) {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            file.createNewFile();
        }
        downloaders.clear();
        for (int i = 0; i < concurrent; i++) {
            downloaders.add(delegate.copy().tag(i));
        }
        this.aborted = false;
        this.file = file;
        this.started = false;
        this.progresses = new long[concurrent];
        delegate.listener(this)
                .callback(this)
                .to(file);
    }

    public File file() {
        return file;
    }

    public void to(OutputStream stream) {
        throw new UnsupportedOperationException("concurrent download does not support output to a output stream");
    }

    public OutputStream stream() {
        return null;
    }

    public void to(final DataOutput output) {
        throw new UnsupportedOperationException("concurrent download does not support output to a data output");
    }

    public DataOutput output() {
        return null;
    }

    public void abort() {
        delegate.abort();
        for (Downloader<?> downloader : downloaders) {
            downloader.abort();
        }
        aborted = true;
    }

    public boolean aborted() {
        return aborted;
    }

    public ConcurrentDelegateDownloader copy() {
        ConcurrentDelegateDownloader clone = new ConcurrentDelegateDownloader(delegate.copy());
        clone.concurrent = concurrent;
        clone.callback = callback;
        clone.listener = listener;
        clone.range = range.copy();
        clone.override = override;
        return clone;
    }

    public void start(Downloader<?> downloader, long total) {
        if (started) return;
        // volatile修饰过的基本类型变量赋值操作是原子性的(引用类型不是!!!), 所以这样才能保证不会有重复调用的风险
        synchronized (lock) {
            if (started) return;
            started = true;
            all = total;
        }

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            if (raf.length() < range.start + total) {
                raf.setLength(range.start + total);
            }

            long block = total / concurrent;
            for (int i = 0; i < downloaders.size(); i++) {
                Range r = Range.forLength(range.start + (i * block), block + (i == concurrent - 1 ? total % concurrent : 0));
                downloaders.get(i)
                        .callback(this)
                        .listener(this)
                        .range(r)
                        .override(false)
                        .to(file);
            }

            listener.start(downloader, total);

            downloader.abort();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.close(raf);
        }
    }

    public void progress(Downloader<?> downloader, long total, long downloaded) {
        progresses[downloader.tag()] = downloaded;
        long sum = 0;
        for (long progress : progresses) sum += progress;
        listener.progress(downloader, all, sum);
    }

    public void finish(Downloader<?> downloader, long total) {
        if (finish.incrementAndGet() == downloaders.size()) listener.finish(downloader, all);
    }

    public void success(AsynchronousDownloader<?> downloader) {
    }

    public void failure(AsynchronousDownloader<?> downloader, Exception exception) {
    }

    public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
        if (downloader == delegate && downloader.aborted()) return;
        if (!success) this.success = false;
        if (!success) this.exception = exception;
        // 因为获取Content-Length请求也要complete所以会多一个
        if (complete.incrementAndGet() == downloaders.size() + 1) {
            try {
                if (this.success) callback.success(this);
                else callback.failure(this, this.exception);
            } finally {
                callback.complete(this, this.success, this.exception);
            }
        }
    }

}
