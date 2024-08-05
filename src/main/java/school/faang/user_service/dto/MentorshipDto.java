package school.faang.user_service.dto;

import lombok.Builder;

/**
 * DTO для предоставления мета информации наставничества.
 */
@Builder
public record MentorshipDto(long id, String username, String email, String phone, String aboutMe, String city) {

}
