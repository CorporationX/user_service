package school.faang.user_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserIdsRequest(

        @NotNull
        @Size(min = 1, message = "UserIds list must contain at least one element")
        List<Long> ids
) {
}
