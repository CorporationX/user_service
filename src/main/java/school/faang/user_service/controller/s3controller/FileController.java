package school.faang.user_service.controller.s3controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.S3Service;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String uniqueKey = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        s3Service.uploadFile(file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok("File uploaded successfully with key: " + uniqueKey);
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String key) {
        return s3Service.downloadFile(key)
                .map(inputStream -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(inputStream)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{key}")
    public ResponseEntity<String> deleteFile(@PathVariable String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.ok("File deleted successfully");
    }
}
