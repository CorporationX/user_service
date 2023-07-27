package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentee.MenteeDto;
import school.faang.user_service.dto.mentor.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentee.MenteeMapper;
import school.faang.user_service.mapper.mentor.MentorMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final  UserRepository userRepository;
    private final MentorMapper mentorMapper;
    private final MenteeMapper menteeMapper;

    public List<MenteeDto> getMentees(Long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return menteeMapper.toMenteeListDto(user.getMentees());
    }

    public List<MentorDto> getMentors(Long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return mentorMapper.toMentorListDto(user.getMentors());
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id " + mentorId + " not found"));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("Mentee with id " + menteeId + " not found"));
        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id " + mentorId + " not found"));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("Mentee with id " + menteeId + " not found"));
        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }
}
