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
 * @date 2015年8月13日 下午2:29:00
 *
 * @version 1.0.0
 */
public interface ConcurrentDownloader<T extends ConcurrentDownloader<T>> extends ResumableDownloader<T> {

	T concurrent(int concurrent);

	int concurrent();

}
