package school.faang.user_service.service.mentorship;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

@Service
public interface MentorshipService {

    void addMentorship(long mentorId, long menteeId);

    void deleteMentorship(long mentorId, long menteeId);

    List<UserDto> getMentees(long userId);

    List<UserDto> getMentors(long userId);
}