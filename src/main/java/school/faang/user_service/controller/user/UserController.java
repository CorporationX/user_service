package school.faang.user_service.controller.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.PaymentDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserDtoForRegistration;
import school.faang.user_service.service.image.AvatarSize;
import school.faang.user_service.service.image.ImageProcessor;
import school.faang.user_service.dto.subscription.responses.SuccessResponse;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.file.ValidFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    @Value("${avatar.size.maxFileSize}")
    private double maxAvatarFileSize;
    private final UserService userService;
    private final ImageProcessor imageProcessor;

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable @Positive long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody @NotEmpty List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/{userId}/avatar")
    public UserDto uploadUserAvatar(@PathVariable long userId, MultipartFile file) {
        if (file.getSize() > maxAvatarFileSize) {
            throw new IllegalArgumentException("Avatar size is too large");
        }
        return userService.uploadUserAvatar(userId, imageProcessor.getBufferedImage(file));
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable long userId, @RequestParam AvatarSize size) {
        Resource avatarResource = userService.downloadUserAvatar(userId, size);
        if (avatarResource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok()
                .headers(headers)
                .body(avatarResource);
    }

    @DeleteMapping("/{userId}/avatar")
    public void deleteUserAvatar(@PathVariable long userId) {
        userService.deleteUserAvatar(userId);
    }

    @PostMapping("/registration")
    public UserDto register(@Validated @RequestBody UserDtoForRegistration userDto) {
        return userService.register(userDto);
    }

    @PostMapping("/users/upload")
    SuccessResponse uploadUser(@RequestParam("file") @ValidFile MultipartFile file) {
        userService.uploadUsers(file);
        return new SuccessResponse("The file is uploaded. Processing...");
    }

    @PostMapping("/users/premium")
    public void buyPremium(@RequestBody PaymentDto paymentDto) {
        /**
         * Метод-пустышка, нужен для задачи сбора аналитики по покупки премиум доступа
         * реализация самой покупки - отсутствует
         */
        userService.buyPremium(paymentDto);
    }
}
