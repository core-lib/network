package com.qfox.network.downloader;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ResumableDelegateDownloader implements ResumableDownloader<ResumableDelegateDownloader>, Listener, Callback {
	private AsynchronousDownloader<?> delegate;
	private Callback callback = new CallbackAdapter();
	private Listener listener = new ListenerAdapter();
	private Range range = Range.ZERO;
	private int times = 3;
	private int retry = 0;
	private long remain;
	private long length;
	private Resumption resumption;
	private boolean aborted = false;

	public ResumableDelegateDownloader(AsynchronousDownloader<?> delegate) {
		super();
		this.delegate = delegate;
	}

	public ResumableDelegateDownloader callback(Callback callback) {
		if (callback == null) {
			throw new NullPointerException("callback is this");
		}
		this.callback = callback;
		return this;
	}

	public Callback callback() {
		return callback;
	}

	public ResumableDelegateDownloader tag(int tag) {
		delegate.tag(tag);
		return this;
	}

	public int tag() {
		return delegate.tag();
	}

	public ResumableDelegateDownloader from(String url) throws MalformedURLException {
		delegate.from(url);
		return this;
	}

	public ResumableDelegateDownloader from(String protocol, String host, int port, String file) throws MalformedURLException {
		delegate.from(protocol, host, port, file);
		return this;
	}

	public ResumableDelegateDownloader from(String protocol, String host, String file) throws MalformedURLException {
		delegate.from(protocol, host, file);
		return this;
	}

	public ResumableDelegateDownloader from(URL url) {
		delegate.from(url);
		return this;
	}

	public URL url() {
		return delegate.url();
	}

	public ResumableDelegateDownloader range(Range range) {
		delegate.range(this.range = range);
		return this;
	}

	public Range range() {
		return range;
	}

	public ResumableDelegateDownloader buffer(int buffer) {
		delegate.buffer(buffer);
		return this;
	}

	public int buffer() {
		return delegate.buffer();
	}

	public ResumableDelegateDownloader timeout(int timeout) {
		delegate.timeout(timeout);
		return this;
	}

	public int timeout() {
		return delegate.timeout();
	}

	public ResumableDelegateDownloader listener(Listener listener) {
		if (listener == null) {
			throw new NullPointerException("listener is null");
		}
		this.listener = listener;
		return this;
	}

	public Listener listener() {
		return listener;
	}

	public ResumableDelegateDownloader override(boolean override) {
		delegate.override(override);
		return this;
	}

	public boolean override() {
		return delegate.override();
	}

	public void to(final String filepath) throws IOException {
		resumption = new Resumption() {

			public void resume(Range range) throws Exception {
				delegate.override(false).range(range).to(filepath);
			}

		};
		delegate.callback(this).listener(this).to(filepath);
	}

	public void to(final File file) throws IOException {
		resumption = new Resumption() {

			public void resume(Range range) throws Exception {
				delegate.override(false).range(range).to(file);
			}

		};
		delegate.callback(this).listener(this).to(file);
	}

	public File file() {
		return delegate.file();
	}

	public void to(final OutputStream stream) throws IOException {
		resumption = new Resumption() {

			public void resume(Range range) throws Exception {
				delegate.override(false).range(range).to(stream);
			}

		};
		delegate.callback(this).listener(this).to(stream);
	}

	public OutputStream stream() {
		return delegate.stream();
	}

	public void to(final DataOutput output) throws IOException {
		resumption = new Resumption() {

			public void resume(Range range) throws Exception {
				delegate.override(false).range(range).to(output);
			}

		};
		delegate.callback(this).listener(this).to(output);
	}

	public DataOutput output() {
		return delegate.output();
	}

	public void abort() {
		this.aborted = true;
		delegate.abort();
	}

	public boolean aborted() {
		return aborted;
	}

	public ResumableDelegateDownloader copy() {
		ResumableDelegateDownloader clone = new ResumableDelegateDownloader(delegate.copy());
		clone.callback = callback;
		clone.listener = listener;
		clone.range = range;
		clone.times = times;
		clone.aborted = aborted;
		return clone;
	}

	public ResumableDelegateDownloader times(int times) {
		if (times < 0) {
			throw new IllegalArgumentException("resume times is lesser than zero");
		}
		this.times = times;
		return this;
	}

	public int times() {
		return times;
	}

	public void success(AsynchronousDownloader<?> downloader) {
		return;
	}

	public void failure(AsynchronousDownloader<?> downloader, Exception exception) {
		return;
	}

	public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
		if (success) {
			try {
				callback.success(this);
			} finally {
				callback.complete(this, true, null);
			}
		} else {
			if (times > retry++ && !aborted) {
				try {
					resumption.resume(remain == 0 ? range : Range.forLength(range.position + length - remain, remain));
				} catch (Exception e) {
					try {
						callback.failure(this, exception);
					} finally {
						callback.complete(this, false, e);
					}
				}
			} else {
				try {
					callback.failure(this, exception);
				} finally {
					callback.complete(this, false, exception);
				}
			}
		}
	}

	public void start(Downloader<?> downloader, long total) {
		if (length == 0) {
			listener.start(this, length = total);
		}
		remain = total;
	}

	public void progress(Downloader<?> downloader, long total, long downloaded) {
		remain = total - downloaded;
		listener.progress(this, length, length - remain);
	}

	public void finish(Downloader<?> downloader, long total) {
		listener.finish(this, length);
	}

	private static interface Resumption {

		void resume(Range range) throws Exception;

	}

}
