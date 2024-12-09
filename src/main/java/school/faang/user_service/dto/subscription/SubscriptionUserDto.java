package school.faang.user_service.dto.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionUserDto {
    Long id;

    @NotNull(message = "The user name mustn't be null")
    @NotBlank(message = "The user name shouldn't be blank")
    String username;

    @NotNull(message = "The email mustn't be null")
    @NotBlank(message = "The email shouldn't be blank")
    String email;
}
