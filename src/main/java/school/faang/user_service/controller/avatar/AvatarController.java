package school.faang.user_service.controller.avatar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.picture.PictureDto;
import school.faang.user_service.dto.picture.PictureType;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.s3.S3service;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/avatar")
@RequiredArgsConstructor
@Tag(name = "Avatar", description = "Эндпоинты для управления аватарами пользователей")
public class AvatarController {
    private final UserContext userContext;
    private final AvatarService avatarService;
    private final S3service s3service;

    @PutMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Загрузка аватара пользователя",
            description = "Принимает файл изображения, сжимает, загружает в S3 и возвращает URL."
    )
    @ApiResponse(responseCode = "201", description = "Аватар успешно загружен",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PictureDto.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content)
    @Parameter(name = "x-user-id", required = true, in = ParameterIn.HEADER)
    public ResponseEntity<List<PictureDto>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        long userId = userContext.getUserId();
        log.info("Received request to upload avatar for user ID: {}. File name: {}, size: {} bytes",
                userId, file.getOriginalFilename(), file.getSize());

        UserProfilePic userProfilePic = avatarService.uploadAvatar(userId, file);

        List<PictureDto> pictures = List.of(
                new PictureDto(s3service.getUrl(userProfilePic.getFileId()), PictureType.AVATAR_MEDIUM),
                new PictureDto(s3service.getUrl(userProfilePic.getSmallFileId()), PictureType.AVATAR_SMALL)
        );

        log.info("Successfully uploaded avatar for user ID: {}. Returning URLs.", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(pictures);
    }

    @GetMapping("/download")
    @Operation(summary = "Скачивание аватара пользователя",
            description = "Возвращает файл аватара в виде массива байт.")
    @ApiResponse(responseCode = "200",
            description = "Файл аватара",
            content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE))
    @ApiResponse(responseCode = "404", description = "Аватар не найден", content = @Content)
    @Parameter(name = "x-user-id", required = true, in = ParameterIn.HEADER)
    public ResponseEntity<byte[]> downloadAvatar() {
        long userId = userContext.getUserId();
        log.info("Received request to download avatar for user ID: {}", userId);

        byte[] imageBytes = avatarService.downloadAvatar(userId);

        log.info("Successfully retrieved avatar for user ID: {}. Image size: {} bytes", userId, imageBytes.length);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удаление аватара пользователя",
            description = "Удаляет текущий аватар и устанавливает аватар по умолчанию.")
    @ApiResponse(responseCode = "200", description = "URL нового аватара по умолчанию",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(implementation = String.class)))
    @Parameter(name = "x-user-id", required = true, in = ParameterIn.HEADER)
    public ResponseEntity<String> deleteAvatar() {
        long userId = userContext.getUserId();
        log.info("Received request to delete avatar for user ID: {}", userId);

        String defaultImageUrl = avatarService.deleteAvatar(userId);

        log.info("Successfully deleted avatar and set default for user ID: {}. New URL: {}", userId, defaultImageUrl);
        return ResponseEntity.ok().body(defaultImageUrl);
    }
}