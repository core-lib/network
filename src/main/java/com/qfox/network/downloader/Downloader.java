package com.qfox.network.downloader;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * Description:项目力图构建一个强大,方便,链式编程风格的数据下载器.提供一个统一的架构来拓展下载数据的多种功能如:同步,异步,断点续传,
 * 多线程下载...而且能够随意组合不同的协议如HTTP,HTTPS,FTP...
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年8月13日 上午10:40:38
 *
 * @version 1.0.0
 */
public interface Downloader<T extends Downloader<T>> extends Cloneable {

	T tag(int tag);

	int tag();

	T from(String url) throws MalformedURLException;

	T from(String protocol, String host, int port, String file) throws MalformedURLException;

	T from(String protocol, String host, String file) throws MalformedURLException;

	T from(URL url);

	URL url();

	T range(Range range);

	Range range();

	T buffer(int buffer);

	int buffer();

	T timeout(int timeout);

	int timeout();

	T listener(Listener listener);

	Listener listener();

	T override(boolean override);

	boolean override();

	void to(String filepath) throws IOException;

	void to(File file) throws IOException;

	File file();

	void to(OutputStream stream) throws IOException;

	OutputStream stream();

	void to(DataOutput output) throws IOException;

	void abort();

	boolean aborted();

	DataOutput output();

	T copy();

}
