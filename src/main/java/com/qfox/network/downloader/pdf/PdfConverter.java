package com.qfox.network.downloader.pdf;

import java.io.File;
import java.io.IOException;

/**
 * PDF 转换器
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-01-21 22:06
 **/
public interface PdfConverter {

    File convert(File file) throws IOException;

    String convert(String filepath) throws IOException;

    File convert(File file, File folder) throws IOException;

    String convert(String filepath, String folder) throws IOException;

}
