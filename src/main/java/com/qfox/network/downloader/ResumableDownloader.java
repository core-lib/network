package com.qfox.network.downloader;

public interface ResumableDownloader<T extends ResumableDownloader<T>> extends AsynchronousDownloader<T> {

	T times(int times);

	int times();

}
