package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.controller.handler.ErrorResponse;
import school.faang.user_service.dto.csv.CsvUploadResponseDto;
import school.faang.user_service.dto.UserDto;

import java.util.List;

@Tag(name = "User Management", description = "API для управления пользователями системы")
public interface UserControllerApi {

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователе по его уникальному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{userId}")
    UserDto getUser(
            @Parameter(description = "Уникальный идентификатор пользователя", example = "5")
            @PathVariable Long userId
    );

    @Operation(
            summary = "Получить пользователей по списку ID",
            description = "Возвращает список пользователей по их идентификаторам"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список пользователей получен",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный список ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/by-ids")
    List<UserDto> getUsersByIds(
            @Parameter(description = "Список ID пользователей", example = "[1, 2, 3]")
            @RequestBody List<Long> ids
    );

    @Operation(
            summary = "Загрузить пользователей из CSV",
            description = "Массовая загрузка пользователей из CSV файла"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл успешно обработан",
                    content = @Content(schema = @Schema(implementation = CsvUploadResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный формат файла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/upload")
    CsvUploadResponseDto uploadStudentsCsv(
            @Parameter(description = "CSV файл с данными пользователей")
            @RequestParam("file") MultipartFile file
    );
}