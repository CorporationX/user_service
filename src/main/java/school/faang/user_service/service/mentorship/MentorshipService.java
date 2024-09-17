package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long mentorId) {
        log.info("Getting mentees for mentor with ID: {}", mentorId);
        if (!mentorshipRepository.existsById(mentorId)) {
            log.error("Mentor with ID {} does not exist", mentorId);
            throw new IllegalArgumentException("Mentor does not exist");
        }
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(mentorId);
        List<UserDto> menteesDtos = userMapper.toDtos(mentees);

        log.info("Found {} mentees for mentor with ID: {}", menteesDtos.size(), mentorId);
        return menteesDtos;
    }

    public List<UserDto> getMentors(long menteeId) {
        log.info("Getting mentors for mentee with ID: {}", menteeId);
        if (!mentorshipRepository.existsById(menteeId)) {
            log.error("Mentee with ID {} does not exist", menteeId);
            throw new IllegalArgumentException("Mentee does not exist");
        }
        List<User> mentors = mentorshipRepository.findMentorsByMenteeId(menteeId);
        List<UserDto> mentorDtos = userMapper.toDtos(mentors);

        log.info("Found {} mentors for mentee with ID: {}", mentorDtos.size(), menteeId);
        return mentorDtos;
    }

    @Transactional
    public void deleteMenteeOfMentor(long mentorId, long menteeId) {
        log.info("Deleting mentee with ID {} for mentor with ID {}", menteeId, mentorId);
        if (!mentorshipRepository.existsById(mentorId) || !mentorshipRepository.existsById(menteeId)) {
            log.error("Mentor with ID {} or mentee with ID {} does not exist", mentorId, menteeId);
            throw new IllegalArgumentException("Mentor or mentee does not exist");
        }
        mentorshipRepository.deleteMenteeOfMentor(mentorId, menteeId);
        log.info("Successfully deleted mentee with ID {} for mentor with ID {}", menteeId, mentorId);
    }

    @Transactional
    public void deleteMentorOfMentee(long mentorId, long menteeId) {
        log.info("Deleting mentor with ID {} for mentee with ID {}", mentorId, menteeId);
        if (!mentorshipRepository.existsById(mentorId) || !mentorshipRepository.existsById(menteeId)) {
            log.error("Mentor with ID {} or mentee with ID {} does not exist", mentorId, menteeId);
            throw new IllegalArgumentException("Mentor or mentee does not exist");
        }
        mentorshipRepository.deleteMentorOfMentee(mentorId, menteeId);
        log.info("Successfully deleted mentor with ID {} for mentee with ID {}", mentorId, menteeId);
    }


}
