package school.faang.user_service.dto;

public record MentorshipDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        String country,
        String city,
        int experience
) {}
