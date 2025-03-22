package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.exception.mentorship.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.message.mentorship.ExceptionMessage;
import school.faang.user_service.message.mentorship.MentorshipMessage;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<Long> getMentees(long userId) {
        log.debug(MentorshipMessage.GET_MENTEES_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        log.debug(MentorshipMessage.GET_MENTEES_FINISH.getMessage(), userId);

        return mentorshipMapper.toDtoList(user.getMentees());
    }

    public List<Long> getMentors(long userId) {
        log.debug(MentorshipMessage.GET_MENTORS_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        log.debug(MentorshipMessage.GET_MENTORS_FINISH.getMessage(), userId);

        return mentorshipMapper.toDtoList(user.getMentors());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        validateIdsEqual(menteeId, mentorId);

        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (mentor.getMentees().remove(mentee)) {
            mentee.getMentors().remove(mentor);
            mentorshipRepository.saveAll(List.of(mentor, mentee));
            log.info(MentorshipMessage.DELETE_MENTEE.getMessage(), menteeId, mentorId);
            return;
        }

        log.info(MentorshipMessage.NO_MENTEE.getMessage(), mentorId, menteeId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        validateIdsEqual(menteeId, mentorId);

        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (mentee.getMentors().remove(mentor)) {
            mentor.getMentees().remove(mentee);
            mentorshipRepository.saveAll(List.of(mentor, mentee));
            log.info(MentorshipMessage.DELETE_MENTOR.getMessage(), mentorId, menteeId);
            return;
        }

        log.info(MentorshipMessage.NO_MENTOR.getMessage(), menteeId, mentorId);
    }

    private void validateIdsEqual(long menteeId, long mentorId) {
        if (menteeId == mentorId) {
            log.error(MentorshipMessage.EQUALS_IDS.getMessage(), menteeId, mentorId);
            throw new InvalidIdException(ExceptionMessage.EQUAL_IDS.getMessage());
        }
    }
}
