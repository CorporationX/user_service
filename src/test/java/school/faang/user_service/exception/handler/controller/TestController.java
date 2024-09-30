package school.faang.user_service.exception.handler.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileUploadException;

@RestController
public class TestController {

    @GetMapping("/file-upload-exception")
    public void fileUploadException() {
        throw new FileUploadException("Test exception", new RuntimeException());
    }

    @GetMapping("/data-validation-exception")
    public void dataValidationException() {
        throw new DataValidationException("Test exception");
    }

    @PostMapping("/method-argument-not-valid-exception")
    public ResponseEntity<String> methodArgumentNotValidException(@Valid @RequestBody UserRegistrationDto userDto) {
        return ResponseEntity.ok("☺");
    }
}
