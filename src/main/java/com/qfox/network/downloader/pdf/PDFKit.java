package com.qfox.network.downloader.pdf;

import java.util.HashMap;
import java.util.Map;

/**
 * PDF工具类
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-01-21 23:54
 **/
public abstract class PDFKit {
    private static final Map<String, PdfConverter> MAP = new HashMap<String, PdfConverter>();

    static {
        MAP.put("windows", new WindowsPdfConverter());
        MAP.put("linux", new LinuxPdfConverter());
    }

    public static void main(String... args) {
        PdfConverter pdfConverter = getPdfConverter();
        System.out.println(pdfConverter);
    }

    public static PdfConverter getPdfConverter() {
        String os = System.getProperty("os.name").split("\\s+")[0];
        return getPdfConverter(os);
    }

    public static PdfConverter getPdfConverter(String os) {
        return os != null ? MAP.get(os.toLowerCase()) : null;
    }

}
