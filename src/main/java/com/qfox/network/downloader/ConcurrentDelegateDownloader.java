package com.qfox.network.downloader;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
 * @date 2015年8月13日 下午7:57:32
 *
 * @version 1.0.0
 */
public class ConcurrentDelegateDownloader implements ConcurrentDownloader<ConcurrentDelegateDownloader>, Listener, Callback {
	private ResumableDownloader<?> delegate;
	private int concurrent = 5;
	private Callback callback = new CallbackAdapter();
	private Listener listener = new ListenerAdapter();
	private Range range = Range.ZERO;
	private boolean override = true;
	private boolean aborted;
	private transient List<AsynchronousDownloader<?>> downloaders = new ArrayList<AsynchronousDownloader<?>>();

	private transient File file;
	private transient boolean started = false;

	private transient long all;
	private transient int finish = 0;

	private transient int complete = 0;
	private transient boolean success = true;
	private transient Exception exception;

	private transient long[] progresses;

	public ConcurrentDelegateDownloader(ResumableDownloader<?> delegate) {
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
		if (callback == null) {
			throw new IllegalArgumentException("callback must be not this");
		}
		this.callback = callback;
		return this;
	}

	public Callback callback() {
		return callback;
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
		return timeout();
	}

	public ConcurrentDelegateDownloader listener(Listener listener) {
		if (listener == null) {
			new NullPointerException("listner can not be null");
		}
		this.listener = listener;
		return this;
	}

	public Listener listener() {
		return listener;
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
		if (override) {
			file.delete();
		}
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
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
		delegate.listener(this).callback(this).to(System.out);
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

	public synchronized void start(Downloader<?> downloader, long total) {
		if (started) {
			return;
		} else {
			started = true;
		}

		this.all = total;

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");
			if (raf.length() < range.start + total) {
				raf.setLength(range.start + total);
			}

			long block = total / concurrent;
			for (int i = 0; i < downloaders.size(); i++) {
				Range r = Range.forLength(range.start + (i * block), block + (i == concurrent - 1 ? total % concurrent : 0));
				downloaders.get(i).callback(this).listener(this).range(r).override(false).to(file);
			}

			listener.start(this, total);

			downloader.abort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.close(raf);
		}
	}

	public synchronized void progress(Downloader<?> downloader, long total, long downloaded) {
		progresses[downloader.tag()] = downloaded;
		long sum = 0;
		for (long progress : progresses) {
			sum += progress;
		}
		listener.progress(this, all, sum);
	}

	public synchronized void finish(Downloader<?> downloader, long total) {
		finish += 1;
		if (finish == downloaders.size()) {
			listener.finish(this, all);
		}
	}

	public synchronized void success(AsynchronousDownloader<?> downloader) {
		return;
	}

	public synchronized void failure(AsynchronousDownloader<?> downloader, Exception exception) {
		return;
	}

	public synchronized void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
		if (downloader == delegate && downloader.aborted()) {
			return;
		}
		complete += 1;
		this.success = this.success && success;
		if (success == false) {
			this.exception = exception;
		}
		if (complete == downloaders.size()) {
			try {
				if (success) {
					callback.success(this);
				} else {
					callback.failure(this, this.exception);
				}
			} finally {
				callback.complete(this, this.success, this.exception);
			}
		}
	}

}
