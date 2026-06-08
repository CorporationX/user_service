package school.faang.user_service.service;

import school.faang.user_service.dto.users.UserDto;

import java.util.List;

public interface MentorshipService {

    List<UserDto> getMentees(long userId);

    List<UserDto> getMentors(long userId);

    void deleteMentee(long userId, long menteeId);

    void deleteMentor(long userId, long mentorId);
}
