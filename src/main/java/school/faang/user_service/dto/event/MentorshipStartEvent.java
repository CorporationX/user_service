package school.faang.user_service.dto.event;

public record MentorshipStartEvent(
        long mentorId,
        long menteeId
) {
}