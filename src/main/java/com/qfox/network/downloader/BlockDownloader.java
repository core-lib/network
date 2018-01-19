package com.qfox.network.downloader;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
 * @date 2015年8月14日 下午5:39:02
 *
 * @version 1.0.0
 */
public abstract class BlockDownloader<T extends BlockDownloader<T>> implements Downloader<T> {
	protected URL url;
	protected Range range = Range.ZERO;
	protected Listener listener = new ListenerAdapter();
	protected int timeout = 60 * 60 * 1000;
	protected int buffer = 1024 * 8;
	protected boolean override = true;
	protected File file;
	protected OutputStream stream;
	protected DataOutput output;
	protected int tag;

	public BlockDownloader() {
		super();
	}

	public T tag(int tag) {
		this.tag = tag;
		return self();
	}

	public int tag() {
		return tag;
	}

	public T from(String url) throws MalformedURLException {
		return from(new URL(url));
	}

	public T from(String protocol, String host, int port, String file) throws MalformedURLException {
		return from(new URL(protocol, host, port, file));
	}

	public T from(String protocol, String host, String file) throws MalformedURLException {
		return from(new URL(protocol, host, file));
	}

	public T from(URL url) {
		if (url == null) {
			throw new NullPointerException("url to download can not be null");
		}
		this.url = url;
		return self();
	}

	public URL url() {
		return url;
	}

	public T range(Range range) {
		if (range == null) {
			throw new IllegalArgumentException("range must not be null");
		}
		this.range = range;
		return self();
	}

	public Range range() {
		return range;
	}

	public T override(boolean override) {
		this.override = override;
		return self();
	}

	public boolean override() {
		return override;
	}

	public T buffer(int bufferSize) {
		if (bufferSize <= 0) {
			throw new IllegalArgumentException("buffer size must greater than zero");
		}
		this.buffer = bufferSize;
		return self();
	}

	public int buffer() {
		return buffer;
	}

	public T timeout(int timeout) {
		if (timeout <= 0) {
			throw new IllegalArgumentException("timeout must greater than zero");
		}
		this.timeout = timeout;
		return self();
	}

	public int timeout() {
		return timeout;
	}

	public T listener(Listener listener) {
		if (listener == null) {
			throw new NullPointerException("listener can not be null");
		}
		this.listener = listener;
		return self();
	}

	public Listener listener() {
		return listener;
	}

	public void to(String filepath) throws IOException {
		if (filepath == null) {
			throw new NullPointerException("filepath is null");
		}
		to(new File(filepath));
	}

	public File file() {
		return file;
	}

	public void to(OutputStream stream) throws IOException {
		if (stream == null) {
			throw new NullPointerException("stream is null");
		}
		DataOutput output = new DataOutputStream(this.stream = stream);
		to(output);
	}

	public OutputStream stream() {
		return stream;
	}

	public DataOutput output() {
		return output;
	}

	protected Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public T copy() {
		@SuppressWarnings("unchecked")
		T clone = (T) this.clone();
		clone.url = url;
		clone.range = range.copy();
		clone.listener = listener;
		clone.timeout = timeout;
		clone.buffer = buffer;
		clone.override = override;
		clone.tag = tag;
		return clone;
	}

	protected abstract T self();

}