package com.qfox.network.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
 * @date 2015年8月14日 下午12:06:20
 *
 * @version 1.0.0
 */
public class URLDownloader {
	private static final Map<String, Downloader<?>> MAP = new HashMap<String, Downloader<?>>();

	static {
		try {
			configure("downloader.properties");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final Downloader<?> downloader;

	private URLDownloader(URL url) {
		Downloader<?> downloader = MAP.get(url.getProtocol());
		if (downloader == null) {
			throw new IllegalArgumentException("unsupport protocol " + url.getProtocol());
		}
		this.downloader = downloader.copy().from(url);
	}

	public static URLDownloader download(String url) throws MalformedURLException {
		return download(new URL(url));
	}

	public static URLDownloader download(String protocol, String host, int port, String file) throws MalformedURLException {
		return download(new URL(protocol, host, port, file));
	}

	public static URLDownloader download(String protocol, String host, String file) throws MalformedURLException {
		return download(protocol, host, file);
	}

	public static URLDownloader download(URL url) {
		URLDownloader downloader = new URLDownloader(url);
		return downloader;
	}

	public Downloader<?> block() {
		return downloader;
	}

	public AsynchronousDownloader<?> asynchronous() {
		return new AsynchronousDelegateDownloader(block());
	}

	public ResumableDownloader<?> resumable(int times) {
		return new ResumableDelegateDownloader(asynchronous()).times(times);
	}

	public ConcurrentDownloader<?> concurrent(int concurrence) {
		return new ConcurrentDelegateDownloader(resumable(0)).concurrent(concurrence);
	}

	public static void configure(String resource) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResource(resource).openStream();
		Properties properties = new Properties();
		properties.load(in);
		for (String name : properties.stringPropertyNames()) {
			String value = properties.getProperty(name);
			MAP.put(name, (Downloader<?>) Class.forName(value).newInstance());
		}
	}

}
