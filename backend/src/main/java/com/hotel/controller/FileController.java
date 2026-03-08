package com.hotel.controller;

import com.hotel.common.BusinessException;
import com.hotel.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) throw new BusinessException("文件不能为空");

        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            Path filePath = dir.resolve(newName);
            file.transferTo(filePath.toFile());
            return Result.success("/uploads/" + newName);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
    }
}
