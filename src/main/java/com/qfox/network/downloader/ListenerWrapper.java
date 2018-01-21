package com.qfox.network.downloader;

/**
 * Listener封装器
 *
 * @author 杨昌沛 646742615@qq.com
 * @company 丽晶软件
 * @project 牛厂
 * @date 2018-01-20 13:40
 **/
public class ListenerWrapper implements Listener {
    private Listener listener;
    private WhenStart whenStart;
    private WhenProgress whenProgress;
    private WhenFinish whenFinish;

    public void start(Downloader<?> downloader, long total) {
        final Listener ls = listener;
        if (ls != null) ls.start(downloader, total);

        final WhenStart ws = whenStart;
        if (ws != null) ws.start(downloader, total);
    }

    public void progress(Downloader<?> downloader, long total, long downloaded) {
        final Listener ls = listener;
        if (ls != null) ls.progress(downloader, total, downloaded);

        final WhenProgress wp = whenProgress;
        if (wp != null) wp.progress(downloader, total, downloaded);
    }

    public void finish(Downloader<?> downloader, long total) {
        final Listener ls = listener;
        if (ls != null) ls.finish(downloader, total);

        final WhenFinish wf = whenFinish;
        if (wf != null) wf.finish(downloader, total);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public WhenStart getWhenStart() {
        return whenStart;
    }

    public void setWhenStart(WhenStart whenStart) {
        this.whenStart = whenStart;
    }

    public WhenProgress getWhenProgress() {
        return whenProgress;
    }

    public void setWhenProgress(WhenProgress whenProgress) {
        this.whenProgress = whenProgress;
    }

    public WhenFinish getWhenFinish() {
        return whenFinish;
    }

    public void setWhenFinish(WhenFinish whenFinish) {
        this.whenFinish = whenFinish;
    }
}
