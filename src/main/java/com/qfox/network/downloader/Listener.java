package com.qfox.network.downloader;

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
 * @date 2015年8月13日 上午10:41:00
 *
 * @version 1.0.0
 */
public interface Listener {

	void start(Downloader<?> downloader, long total);

	void progress(Downloader<?> downloader, long total, long downloaded);

	void finish(Downloader<?> downloader, long total);

}
