package com.qfox.network.downloader.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Windows 系统 PDF 转换器
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-01-21 22:25
 **/
public class WindowsPdfConverter extends AbstractPdfConverter implements PdfConverter {

    public static void main(String[] args) throws IOException {
        PdfConverter converter = PdfKit.getPdfConverter();
        File pdf = converter.convert(new File("C:\\Users\\Chang\\Downloads\\MySql开发规范.docx"), new File("C:\\Users\\Chang\\Downloads"));
        System.out.println(pdf);
    }

    @Override
    public String convert(String filepath, String folder) throws IOException {
        try {
            filepath = normalize(filepath);
            folder = normalize(folder);
            File file = new File(filepath);
            if (!file.exists()) throw new FileNotFoundException(filepath);

            File directory = new File(folder);
            if (directory.isFile() || (!directory.exists() && !directory.mkdirs())) throw new IOException("can not make directory:" + folder);

            String command = "soffice --convert-to pdf \"" + filepath + "\" --outdir \"" + folder + "\"";
            Process process = Runtime.getRuntime().exec(command);
            int code = process.waitFor();
            switch (code) {
                case 0: {
                    String name = file.getName();
                    int index = name.lastIndexOf('.');
                    if (index < 0) return normalize(folder + File.separator + name + ".pdf");
                    else return normalize(folder + File.separator + name.substring(0, index) + ".pdf");
                }
                default: {
                    InputStream in = process.getErrorStream();
                    String msg = IoKit.toString(in);
                    throw new IOException("转换失败:" + msg);
                }
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
