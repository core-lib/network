package com.qfox.network.downloader.pdf;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 抽象的 PDF 转换器
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-01-21 22:17
 **/
public abstract class AbstractPdfConverter implements PdfConverter {

    @Override
    public File convert(File file) throws IOException {
        return new File(convert(file.getAbsolutePath()));
    }

    @Override
    public String convert(String filepath) throws IOException {
        return convert(filepath, System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID());
    }

    @Override
    public File convert(File file, File folder) throws IOException {
        return new File(convert(file.getAbsolutePath(), folder.getAbsolutePath()));
    }

    protected String normalize(String filepath) throws IOException {
        return filepath.replaceAll("\\\\+", "/").replaceAll("/+", "/");
    }

}
