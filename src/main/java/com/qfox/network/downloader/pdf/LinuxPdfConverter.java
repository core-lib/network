package com.qfox.network.downloader.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Linux 系统 PDF 转换器
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-01-21 22:25
 **/
public class LinuxPdfConverter extends AbstractPdfConverter implements PdfConverter {

    @Override
    public String convert(String filepath, String folder) throws IOException {
        try {
            filepath = normalize(filepath);
            folder = normalize(folder);
            File file = new File(filepath);
            if (!file.exists()) throw new FileNotFoundException(filepath);

            File directory = new File(folder);
            if (directory.isFile() || (!directory.exists() && !directory.mkdirs())) throw new IOException("can not make directory:" + folder);

            String name = file.getName();
            int index = name.lastIndexOf('.');
            String destination = index < 0 ? normalize(folder + File.separator + name + ".pdf") : normalize(folder + File.separator + name.substring(0, index) + ".pdf");

            String command = "doc2pdf --output=\"" + destination + "\" \"" + filepath + "\"";
            Process process = Runtime.getRuntime().exec(command);
            int code = process.waitFor();
            if (code != 0) {
                InputStream in = process.getErrorStream();
                String msg = IOKit.toString(in);
                throw new IOException("转换失败:" + msg);
            }
            return destination;
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
