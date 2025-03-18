package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(Long mentorId) {
        List<User> mentees = mentorshipRepository.findAllMenteesByMentorId(mentorId);
        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(Long menteeId) {
        List<User> mentors = mentorshipRepository.findAllMentorsByMenteeId(menteeId);
        return mentors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        int deletedRows = mentorshipRepository.deleteMenteeFromMentor(menteeId, mentorId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("No mentorship relationship found to delete");
        }
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        int deletedRows = mentorshipRepository.deleteMentorFromMentee(menteeId, mentorId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("No mentorship relationship found to delete");
        }
    }

}
