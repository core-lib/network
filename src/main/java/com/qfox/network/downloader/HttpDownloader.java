package com.qfox.network.downloader;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;

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
 * @date 2015年8月13日 上午11:00:25
 */
public class HttpDownloader extends BlockDownloader<HttpDownloader> {
    private HttpURLConnection connection;
    private boolean aborted;

    public void to(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file must not be null");
        }
        this.file = file;
        if (url == null) {
            throw new IllegalArgumentException("you should tell me the url to download");
        }
        if (override) {
            file.delete();
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        this.aborted = false;
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        connection = (HttpURLConnection) url.openConnection();
        Timer timer = null;
        try {
            timer = new Timer(true);
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    connection.disconnect();
                }

            }, timeout);

            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(timeout);
            connection.setUseCaches(false);
            connection.addRequestProperty("Range", "bytes=" + (range == null ? 0 : range.start) + "-" + (range == null || range.end <= 0 ? "" : range.end));
            connection.connect();

            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("server response unexpected code " + code + " and message " + connection.getResponseMessage());
            }

            long length = connection.getContentLength();
            if (range.end != 0 && length != range.length) {
                throw new IOException("server " + url.getHost() + " does not supported range feature");
            }
            if (raf.length() < range.start + length) {
                raf.setLength(range.start + length);
            }
            raf.seek(range.start);
            listener.start(this, length);
            InputStream input = connection.getInputStream();
            byte[] buff = new byte[buffer];
            long total = 0;
            int len;
            while ((len = input.read(buff, 0, (int) Math.min(buff.length, length - total))) != -1) {
                raf.write(buff, 0, len);
                total += len;
                listener.progress(this, length, total);
            }
            listener.finish(this, length);
        } finally {
            IOUtils.close(raf);
            connection.disconnect();
            if (timer != null) timer.cancel();
        }
    }

    public void to(DataOutput output) throws IOException {
        if (output == null) {
            throw new NullPointerException("can not output to null");
        }
        this.output = output;
        if (url == null) {
            throw new IllegalArgumentException("you should tell me the url to download");
        }
        this.aborted = false;
        connection = (HttpURLConnection) url.openConnection();
        Timer timer = null;
        try {
            timer = new Timer(true);
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    connection.disconnect();
                }

            }, timeout);

            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(timeout);
            connection.setUseCaches(false);
            connection.addRequestProperty("Range", "bytes=" + (range == null ? 0 : range.start) + "-" + (range == null || range.end <= 0 ? "" : range.end));
            connection.connect();

            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("server response unexpected code " + code + " and message " + connection.getResponseMessage());
            }

            long length = connection.getContentLength();
            if (range.end != 0 && length != range.length) {
                throw new IOException("server " + url.getHost() + " does not supported range feature");
            }
            listener.start(this, length);
            InputStream input = connection.getInputStream();
            byte[] buff = new byte[buffer];
            long total = 0;
            int len;
            while ((len = input.read(buff, 0, (int) Math.min(buff.length, length - total))) != -1) {
                output.write(buff, 0, len);
                total += len;
                listener.progress(this, length, total);
            }
            listener.finish(this, length);
        } finally {
            connection.disconnect();
            if (timer != null) timer.cancel();
        }
    }

    public void abort() {
        if (connection == null) {
            return;
        }
        connection.disconnect();
        aborted = true;
    }

    public boolean aborted() {
        return aborted;
    }

    @Override
    protected HttpDownloader self() {
        return this;
    }

}
