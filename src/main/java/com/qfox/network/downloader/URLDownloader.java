package com.qfox.network.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
 * @date 2015年8月14日 下午12:06:20
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
        return new URLDownloader(url);
    }

    public Downloader<?> block() {
        return downloader;
    }

    public AsynchronousDownloader<?> asynchronous(ExecutorService executor) {
        return new AsynchronousDelegateDownloader(block()).use(executor);
    }

    public ResumableDownloader<?> resumable(ExecutorService executor, int times) {
        return new ResumableDelegateDownloader(asynchronous(executor)).times(times);
    }

    public ConcurrentDownloader<?> concurrent(ExecutorService executor, int concurrence) {
        return new ConcurrentDelegateDownloader(resumable(executor, 0)).concurrent(concurrence);
    }

    public static void configure(String resource) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url == null) throw new IOException("resource[" + resource + "] is not exists");
        InputStream in = url.openStream();
        Properties properties = new Properties();
        properties.load(in);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();
            MAP.put(name, (Downloader<?>) Class.forName(value).newInstance());
        }
    }

}
