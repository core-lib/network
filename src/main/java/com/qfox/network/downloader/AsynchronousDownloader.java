package com.qfox.network.downloader;

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
 * @date 2015年8月13日 下午12:19:15
 */
public interface AsynchronousDownloader<T extends AsynchronousDownloader<T>> extends Downloader<T> {

    T callback(Callback callback);

    Callback callback();

    T success(OnSuccess onSuccess);

    T failure(OnFailure onFailure);

    T complete(OnComplete onComplete);

    T use(ExecutorService executor);

}
