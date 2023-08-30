package school.faang.user_service.dto.contact;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TgContactDto {
    @NotNull
    private Long userId;
    private String tgChatId;
}
