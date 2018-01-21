package com.qfox.network.downloader.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO工具类
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-01-21 22:52
 **/
public abstract class IOKit {

    /**
     * 将输入流输出成字符串
     *
     * @param in 输入流
     * @return 字符串
     * @throws IOException IO error occur
     */
    public static String toString(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len, ofs = 0;
        while ((len = in.read(buf)) != -1) baos.write(buf, ofs, len);
        return baos.toString();
    }

}
