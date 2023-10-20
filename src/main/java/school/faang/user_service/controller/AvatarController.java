package school.faang.user_service.controller;

import com.amazonaws.util.Base64;
import io.netty.handler.codec.base64.Base64Decoder;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.AvatarService;

import java.util.Map;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class AvatarController {

    @Value("${aws.s3.maxImageSize}")
    long maxImageSize;
    private final AvatarService service;

    @PostMapping
    public ResponseEntity<Object> uploadAvatar(@RequestParam long userId, @RequestBody MultipartFile file) {
        if (file.getSize() < maxImageSize * 1024 * 1024){
            return service.uploadAvatar(userId, file);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    @GetMapping
    public void getAvatar(long userId) {
        service.getAvatar(userId);
    }

    @GetMapping
    public void getSmallAvatar(long userId) {
        service.getSmallAvatar(userId);
    }
}
