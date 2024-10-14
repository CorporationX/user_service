package school.faang.user_service.dto.event;

public record MentorshipAcceptedEventDto(
        Long requestId,
        Long mentorId,
        Long menteeId) {
}
