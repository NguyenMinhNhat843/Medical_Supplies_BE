package com.product.productservice.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class UploadFileUtils {
    private static final String BASE_DIR = "C://home/office";

    public void writeOrUpdate(String path, byte[] bytes) {
        String fullPath = BASE_DIR + path;

        File directory = new File(StringUtils.substringBeforeLast(fullPath, "/"));
        if (!directory.exists()) {
            directory.mkdirs(); // Tạo thư mục cha nếu chưa có
        }

        try (FileOutputStream fop = new FileOutputStream(fullPath)) {
            fop.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file", e);
        }
    }

    public boolean deleteFile(String path) {
        File file = new File(BASE_DIR + path);
        return file.exists() && file.delete();
    }
}
