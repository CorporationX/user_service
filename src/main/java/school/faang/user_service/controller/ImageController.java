package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.minio.ImageService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/image")
public class ImageController {
    private final ImageService imageService;
    private final UserContext userContext;

    @PutMapping("/add")
    public UserProfilePic addImage(@RequestBody MultipartFile file){
        return imageService.addImage(userContext.getUserId(), file);
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadImage(){
        byte[] imageBytes = null;
        try {
            imageBytes = imageService.downloadImage(userContext.getUserId()).readAllBytes();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error in downloading image");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteImage(){
        imageService.deleteImage(userContext.getUserId());
        return ResponseEntity.ok("File was deleted successfully");
    }
}
