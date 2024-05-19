package school.faang.user_service.service.mentorship;

public interface MentorshipRequestService {
    Boolean requestMentorship(Long userId, Long mentorId, String description);
}
