package school.faang.user_service.service.impl;

import school.faang.user_service.dto.mentorship.MentorshipUserDto;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
public interface MentorshipService {

    List<MentorshipUserDto> getMentees(long userId);

    List<MentorshipUserDto> getMentors(long userId);

    boolean deleteMentorshipRelations(long mentorId, long menteeId);
}
