package com.qfox.network.downloader;

import java.io.Closeable;

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
 * @date 2015年8月14日 上午10:07:58
 *
 * @version 1.0.0
 */
public class IOUtils {

	private IOUtils() {

	}

	public static void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception e) {
			return;
		}
	}

}
