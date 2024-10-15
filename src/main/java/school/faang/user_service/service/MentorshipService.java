package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.mentorship.MentorshipDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

public interface MentorshipService {

    List<MentorshipDto> getMentees(long mentorId);

    List<MentorshipDto> getMentors(long menteeId);

    @Transactional
    void deleteMentee(long menteeId, long mentorId);

    @Transactional
    void deleteMentor(long menteeId, long mentorId);

    @Transactional
    void deleteMentorFromMentees(long mentorId, List<User> mentees);
}
