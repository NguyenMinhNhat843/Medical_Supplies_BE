package com.product.productservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/picture/repository")
public class ImageController {

    private static final String BASE_DIR = "C://home/office";

    @GetMapping("/**")
    public void getImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uri = request.getRequestURI(); // /repository/product/abc.jpg
            String path = uri.replace("/api/picture/repository", "");

            File file = new File(BASE_DIR + path);
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String contentType = request.getServletContext().getMimeType(file.getName());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            try {
                response.setContentType(contentType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
            }
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}