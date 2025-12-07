package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import school.faang.user_service.dto.picture.PictureDto;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

public record UserDto(
        @Schema(description = "Уникальный идентификатор пользователя", example = "1")
        Long id,

        @Schema(description = "Имя пользователя", example = "john_doe")
        String username,

        @Schema(description = "Email пользователя", example = "john@example.com")
        String email,

        @Schema(description = "Телефон пользователя", example = "+79991234567")
        String phone,

        @Schema(description = "Информация о пользователе", example = "Java developer")
        String aboutMe,

        @Schema(description = "Опыт работы в месяцах", example = "24")
        Integer experience,

        @Schema(description = "Ссылка на аватарку и тип аватарки «medium» или «small»")
        List<PictureDto> pictures,

        @Schema(description = "Уникальные идентификаторы подписчиков пользователя", example = "1, 2, 3")
        List<Long> followersIds,

        @Schema(description = "Уникальный идентификатор диалога пользователя с телеграм-ботом", example = "123456")
        Long chatId,

        @Schema(description = "Предпочитаемый способ получения уведомлений", example = "TELEGRAM")
        PreferredContact preference
) {
}