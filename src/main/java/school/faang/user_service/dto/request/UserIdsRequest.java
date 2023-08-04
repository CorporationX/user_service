package school.faang.user_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record UserIdsRequest(

        @NotNull(message = "UserIds list can't be empty")
        @Size(min = 1, message = "UserIds list must contain at least one element")
        List<Long> ids
) {
}
