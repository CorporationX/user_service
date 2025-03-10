package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorMapper;
import school.faang.user_service.message.mentorship.MentorshipMessage;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorMapper mentorMapper;
    private final MenteeMapper menteeMapper;

    public List<Long> getMentees(long userId) {
        log.debug(MentorshipMessage.GET_MENTEES_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        MentorDto mentorDto = mentorMapper.toDto(user);
        log.debug(MentorshipMessage.GET_MENTEES_FINISH.getMessage(), userId);
        return mentorDto.getMentees();
    }

    public List<Long> getMentors(long userId) {
        log.debug(MentorshipMessage.GET_MENTORS_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        MenteeDto menteeDto = menteeMapper.toDto(user);
        log.debug(MentorshipMessage.GET_MENTORS_FINISH.getMessage(), userId);
        return menteeDto.getMentors();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Mentor not found"));
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException("Mentee not found"));
        if (mentor.getMentees().remove(mentee)) {
            mentee.getMentors().remove(mentor);
            mentorshipRepository.saveAll(List.of(mentor, mentee));
            log.info(MentorshipMessage.DELETE_MENTEE.getMessage(), menteeId, mentorId);
            return;
        }
        log.info(MentorshipMessage.NO_MENTEE.getMessage(), mentorId, menteeId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Mentor not found"));
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException("Mentee not found"));
        if (mentee.getMentors().remove(mentor)) {
            mentor.getMentees().remove(mentee);
            mentorshipRepository.saveAll(List.of(mentor, mentee));
            log.info(MentorshipMessage.DELETE_MENTOR.getMessage(), mentorId, menteeId);
            return;
        }
        log.info(MentorshipMessage.NO_MENTOR.getMessage(), menteeId, mentorId);
    }
}
