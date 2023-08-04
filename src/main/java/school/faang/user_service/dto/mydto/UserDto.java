package school.faang.user_service.dto.mydto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @NotBlank(message = "Username shouldn't be empty")
        String username,

        @Email
        @NotBlank(message = "Email shouldn't be empty")
        String email,

        @NotBlank(message = "Phone number shouldn't be empty")
        String phone,

        @NotBlank(message = "About me shouldn't be empty")
        String aboutMe,

        @NotNull(message = "Active should be determined")
        boolean active,

        @NotBlank(message = "City shouldn't be empty")
        String city,

        @NotNull(message = "Enter your experience")
        Integer experience,

        List<Long> followers,

        List<Long> followees,

        List<Long> mentors,

        List<Long> mentees,

        @NotBlank(message = "Enter country")
        CountryDto country,

        List<GoalDto> goals,

        List<SkillDto> skills) {
}
