package com.qfox.network.downloader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
 * @date 2015年8月14日 上午10:07:58
 */
public abstract class IOUtils {

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

    public static byte[] hash(InputStream in, String algorithm) throws IOException {
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            final byte[] buf = new byte[1024];
            final int ofs = 0;
            int len;
            while ((len = in.read(buf)) != -1) md.update(buf, ofs, len);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

}
