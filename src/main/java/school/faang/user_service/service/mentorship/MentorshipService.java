package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MenteeDto> getMentees(Long userId) {
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(userId);
        return mentorshipMapper.menteesToMenteesDtos(mentees);
    }

    public List<MentorDto> getMentors(Long userId) {
        List<User> mentors = mentorshipRepository.findMentorsByMenteeId(userId);
        return mentorshipMapper.mentorsToMentorsDtos(mentors);
    }

    @Transactional
    public void deleteMentee(Long mentorId, Long menteeId) {
        boolean exists = mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId);
        if (exists) {
            mentorshipRepository.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        } else {
            throw new IllegalArgumentException("Mentor does not have this mentee.");
        }
    }

    @Transactional
    public void deleteMentor(Long menteeId, Long mentorId) {
        boolean exists = mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId);
        if (exists) {
            mentorshipRepository.deleteByMenteeIdAndMentorId(menteeId, mentorId);
        } else {
            throw new IllegalArgumentException("Mentee does not have this mentor.");
        }
    }
}
