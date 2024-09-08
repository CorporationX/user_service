package school.faang.user_service.dto.mentorship;

public record MentorshipDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe
) {}
