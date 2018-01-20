package com.qfox.network.downloader;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

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
 * @date 2015年8月13日 下午7:22:58
 */
public class AsynchronousDelegateDownloader implements AsynchronousDownloader<AsynchronousDelegateDownloader>, Listener {
    private Downloader<?> delegate;
    private CallbackWrapper callback = new CallbackWrapper();
    private ListenerWrapper listener = new ListenerWrapper();
    private File file;
    private OutputStream stream;
    private DataOutput output;
    private ExecutorService executor;

    AsynchronousDelegateDownloader(Downloader<?> delegate) {
        super();
        this.delegate = delegate;
    }

    public AsynchronousDelegateDownloader callback(Callback callback) {
        this.callback.setCallback(callback);
        return this;
    }

    public Callback callback() {
        return callback.getCallback();
    }

    public AsynchronousDelegateDownloader success(OnSuccess onSuccess) {
        this.callback.setOnSuccess(onSuccess);
        return this;
    }

    public AsynchronousDelegateDownloader failure(OnFailure onFailure) {
        this.callback.setOnFailure(onFailure);
        return this;
    }

    public AsynchronousDelegateDownloader complete(OnComplete onComplete) {
        this.callback.setOnComplete(onComplete);
        return this;
    }

    public AsynchronousDelegateDownloader use(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public AsynchronousDelegateDownloader tag(int tag) {
        delegate.tag(tag);
        return this;
    }

    public int tag() {
        return delegate.tag();
    }

    public AsynchronousDelegateDownloader from(String url) throws MalformedURLException {
        delegate.from(url);
        return this;
    }

    public AsynchronousDelegateDownloader from(String protocol, String host, int port, String file) throws MalformedURLException {
        delegate.from(protocol, host, port, file);
        return this;
    }

    public AsynchronousDelegateDownloader from(String protocol, String host, String file) throws MalformedURLException {
        delegate.from(protocol, host, file);
        return this;
    }

    public AsynchronousDelegateDownloader from(URL url) {
        delegate.from(url);
        return this;
    }

    public URL url() {
        return delegate.url();
    }

    public AsynchronousDelegateDownloader range(Range range) {
        delegate.range(range);
        return this;
    }

    public Range range() {
        return delegate.range();
    }

    public AsynchronousDelegateDownloader buffer(int buffer) {
        delegate.buffer(buffer);
        return this;
    }

    public int buffer() {
        return delegate.buffer();
    }

    public AsynchronousDelegateDownloader timeout(int timeout) {
        delegate.timeout(timeout);
        return this;
    }

    public int timeout() {
        return delegate.timeout();
    }

    public AsynchronousDelegateDownloader listener(Listener listener) {
        this.listener.setListener(listener);
        return this;
    }

    public Listener listener() {
        return listener.getListener();
    }

    @Override
    public AsynchronousDelegateDownloader start(WhenStart whenStart) {
        this.listener.setWhenStart(whenStart);
        return this;
    }

    @Override
    public AsynchronousDelegateDownloader progress(WhenProgress whenProgress) {
        this.listener.setWhenProgress(whenProgress);
        return this;
    }

    @Override
    public AsynchronousDelegateDownloader finish(WhenFinish whenFinish) {
        this.listener.setWhenFinish(whenFinish);
        return this;
    }

    public AsynchronousDelegateDownloader override(boolean override) {
        delegate.override(override);
        return this;
    }

    public boolean override() {
        return delegate.override();
    }

    public void to(String filepath) {
        to(new File(filepath));
    }

    public void to(File file) {
        if (file == null) throw new NullPointerException("can not output to null file");
        this.file = file;
        delegate.listener(this);
        executor.execute(new Downloading(delegate, this, callback, file));
    }

    public File file() {
        return file;
    }

    public void to(OutputStream stream) {
        if (stream == null) throw new NullPointerException("can not output to null stream");
        DataOutput output = new DataOutputStream(this.stream = stream);
        to(output);
    }

    public OutputStream stream() {
        return stream;
    }

    public void to(final DataOutput output) {
        if (output == null) throw new NullPointerException("can not output to null output");
        this.output = output;
        delegate.listener(this);
        executor.execute(new Downloading(delegate, this, callback, output));
    }

    public DataOutput output() {
        return output;
    }

    public void abort() {
        delegate.abort();
    }

    public boolean aborted() {
        return delegate.aborted();
    }

    public AsynchronousDelegateDownloader copy() {
        AsynchronousDelegateDownloader clone = new AsynchronousDelegateDownloader(delegate.copy());
        clone.callback = callback;
        clone.listener = listener;
        clone.executor = executor;
        return clone;
    }

    public void start(Downloader<?> downloader, long total) {
        listener.start(this, total);
    }

    public void progress(Downloader<?> downloader, long total, long downloaded) {
        listener.progress(this, total, downloaded);
    }

    public void finish(Downloader<?> downloader, long total) {
        listener.finish(this, total);
    }

    private static class Downloading implements Runnable {
        private final Downloader<?> delegate;
        private final AsynchronousDownloader<?> downloader;
        private final Callback callback;
        private final File file;
        private final DataOutput output;

        Downloading(Downloader<?> delegate, AsynchronousDownloader<?> downloader, Callback callback, File file) {
            this.delegate = delegate;
            this.downloader = downloader;
            this.callback = callback;
            this.file = file;
            this.output = null;
        }

        Downloading(Downloader<?> delegate, AsynchronousDownloader<?> downloader, Callback callback, DataOutput output) {
            this.delegate = delegate;
            this.downloader = downloader;
            this.callback = callback;
            this.output = output;
            this.file = null;
        }

        public void run() {
            Exception exception = null;
            try {
                if (file != null) delegate.to(file);
                else delegate.to(output);
            } catch (Exception e) {
                exception = e;
            }
            try {
                if (exception == null) {
                    callback.success(downloader);
                } else {
                    callback.failure(downloader, exception);
                }
            } finally {
                callback.complete(downloader, exception == null, exception);
            }
        }
    }

}
