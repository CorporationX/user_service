package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipDeleteDto;
import school.faang.user_service.dto.SuccessResponseDto;
import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {
    void createMentorship(long mentorId, long menteeId);

    boolean existsByMentorIdAndMenteeId(long mentorId, long menteeId);

    Long findMentorshipConnectionId(long mentorId, long menteeId);

    List<UserDto> getMentees(long mentorId);

    List<UserDto> getMentors(long menteeId);

    SuccessResponseDto deleteMentee(MentorshipDeleteDto mentorshipDeleteDto);

    SuccessResponseDto deleteMentor(MentorshipDeleteDto mentorshipDeleteDto);
}
