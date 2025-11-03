package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        @JsonProperty("id") Long id,
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("phone") String phone,
        @JsonProperty("aboutMe") String aboutMe
) {
}
