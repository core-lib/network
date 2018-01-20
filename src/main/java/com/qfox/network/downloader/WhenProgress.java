package com.qfox.network.downloader;

/**
 * 为了适配JDK1.8 的Lambda表达式从Listener中抽出来的progress方法
 *
 * @author 杨昌沛 646742615@qq.com
 * @company 丽晶软件
 * @project 牛厂
 * @date 2018-01-20 13:39
 **/
public interface WhenProgress {

    void progress(Downloader<?> downloader, long total, long downloaded);

}
