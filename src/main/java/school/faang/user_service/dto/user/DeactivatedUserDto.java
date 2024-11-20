package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Builder;
import school.faang.user_service.entity.Country;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DeactivatedUserDto(
        Long id,
        boolean active,
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String phone,
        @NotBlank Long countryId,
        @NotBlank String city,
        List<Long> idsOwnedEvents,
        List<Long> idsParticipatedEvent,
        List<Long> idsMentors,
        List<Long> idsSettingGoals,
        List<Long> idsGoals,
        List<Long> idsSkills,
        @PastOrPresent LocalDateTime createdAt,
        @PastOrPresent LocalDateTime updatedAt
) {
}
