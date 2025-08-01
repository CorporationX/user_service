package school.faang.user_service.dto.mentorship;

public record CreateMentorshipRequestDto(
        String description,
        Long mentorId
) {
}