package school.faang.user_service.dto.telegram;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetTelegramChatIdDto {
    @NotNull(message = "Token must not be null")
    @NotBlank(message = "Token must not be blank")
    private String token;

    @NotNull(message = "Telegram Chat ID must not be null")
    private Long telegramChatId;
}
