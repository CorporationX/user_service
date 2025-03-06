package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {
    List<UserDto> getMentees(long userId);

    List<UserDto> getMentors(long userId);

    void deleteMentee(long mentorId, long menteeId);

    void deleteMentor(long menteeId, long mentorId);
}
