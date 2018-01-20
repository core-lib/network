package com.qfox.network.downloader;

/**
 * 回调封装器
 *
 * @author 杨昌沛 646742615@qq.com
 * @company 丽晶软件
 * @project 牛厂
 * @date 2018-01-20 13:26
 **/
public class CallbackWrapper implements Callback {
    private Callback callback;
    private OnSuccess onSuccess;
    private OnFailure onFailure;
    private OnComplete onComplete;

    public void success(AsynchronousDownloader<?> downloader) {
        final Callback cb = callback;
        if (cb != null) cb.success(downloader);

        final OnSuccess os = onSuccess;
        if (os != null) os.success(downloader);
    }

    public void failure(AsynchronousDownloader<?> downloader, Exception exception) {
        final Callback cb = callback;
        if (cb != null) cb.failure(downloader, exception);

        final OnFailure of = onFailure;
        if (of != null) of.failure(downloader, exception);
    }

    public void complete(AsynchronousDownloader<?> downloader, boolean success, Exception exception) {
        final Callback cb = callback;
        if (cb != null) cb.complete(downloader, success, exception);

        final OnComplete oc = onComplete;
        if (oc != null) oc.complete(downloader, success, exception);
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public OnSuccess getOnSuccess() {
        return onSuccess;
    }

    public void setOnSuccess(OnSuccess onSuccess) {
        this.onSuccess = onSuccess;
    }

    public OnFailure getOnFailure() {
        return onFailure;
    }

    public void setOnFailure(OnFailure onFailure) {
        this.onFailure = onFailure;
    }

    public OnComplete getOnComplete() {
        return onComplete;
    }

    public void setOnComplete(OnComplete onComplete) {
        this.onComplete = onComplete;
    }
}
