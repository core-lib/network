package com.qfox.network.downloader;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年8月13日 下午7:22:58
 *
 * @version 1.0.0
 */
public class AsynchronousDelegateDownloader implements AsynchronousDownloader<AsynchronousDelegateDownloader>, Listener {
	private Downloader<?> delegate;
	private Callback callback = new CallbackAdapter();
	private Listener listener = new ListenerAdapter();
	private File file;
	private OutputStream stream;
	private DataOutput output;

	public AsynchronousDelegateDownloader(Downloader<?> delegate) {
		super();
		this.delegate = delegate;
	}

	public AsynchronousDelegateDownloader callback(Callback callback) {
		if (callback == null) {
			throw new IllegalArgumentException("callback must not be this");
		}
		this.callback = callback;
		return this;
	}

	public Callback callback() {
		return callback;
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
		if (listener == null) {
			new NullPointerException("listner can not be null");
		}
		this.listener = listener;
		return this;
	}

	public Listener listener() {
		return listener;
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

	public void to(final File file) {
		if (file == null) {
			throw new NullPointerException("can not output to null file");
		}
		this.file = file;
		delegate.listener(this);
		new Thread(new Runnable() {

			public void run() {
				Exception exception = null;
				try {
					delegate.to(file);
				} catch (Exception e) {
					exception = e;
				}
				try {
					if (exception == null) {
						callback.success(AsynchronousDelegateDownloader.this);
					} else {
						callback.failure(AsynchronousDelegateDownloader.this, exception);
					}
				} finally {
					callback.complete(AsynchronousDelegateDownloader.this, exception == null, exception);
				}
			}

		}).start();
	}

	public File file() {
		return file;
	}

	public void to(OutputStream stream) {
		if (stream == null) {
			throw new NullPointerException("can not output to null stream");
		}
		DataOutput output = new DataOutputStream(this.stream = stream);
		to(output);
	}

	public OutputStream stream() {
		return stream;
	}

	public void to(final DataOutput output) {
		if (output == null) {
			throw new NullPointerException("can not output to null output");
		}
		this.output = output;
		delegate.listener(this);
		new Thread(new Runnable() {

			public void run() {
				Exception exception = null;
				try {
					delegate.to(output);
				} catch (Exception e) {
					exception = e;
				}
				try {
					if (exception == null) {
						callback.success(AsynchronousDelegateDownloader.this);
					} else {
						callback.failure(AsynchronousDelegateDownloader.this, exception);
					}
				} finally {
					callback.complete(AsynchronousDelegateDownloader.this, exception == null, exception);
				}
			}

		}).start();
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

}
