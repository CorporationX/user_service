package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Positive;

import java.util.List;

public record UserSearchRequest(
        String query,
        List<String> skillNames,
        @Positive Integer experienceFrom,
        @Positive Integer experienceTo
) {
    public boolean expBoundsIsNotNull() {
        return experienceFrom != null && experienceTo != null;
    }
}
