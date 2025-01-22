package school.faang.user_service.service;

import school.faang.user_service.entity.User;

import java.util.List;

public interface MentorshipService {
    List<User> getMentors(long userId);
    List<User> getMentees(long userId);
    void deleteMentee(long menteeId, long mentorId);
    void deleteMentor(long menteeId, long mentorId);
}
