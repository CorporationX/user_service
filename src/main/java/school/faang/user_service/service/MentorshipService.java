package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.exception.mentorship.NoUserMenteeException;
import school.faang.user_service.exception.mentorship.NoUserMentorException;
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

    public List<MentorshipUserDto> getMentees(long userId) {
        log.info(MentorshipMessage.GET_MENTEES_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), userId)
                        )
                );
        log.info(MentorshipMessage.GET_MENTEES_FINISH.getMessage(), userId);

        return mentorshipMapper.toDtoList(user.getMentees());
    }

    public List<MentorshipUserDto> getMentors(long userId) {
        log.info(MentorshipMessage.GET_MENTORS_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), userId)
                        )
                );
        log.info(MentorshipMessage.GET_MENTORS_FINISH.getMessage(), userId);

        return mentorshipMapper.toDtoList(user.getMentors());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        validateIdsEqual(menteeId, mentorId);

        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentorId)
                        )
                );
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), menteeId)
                        )
                );

        if (mentor.getMentees().remove(mentee)) {
            mentee.getMentors().remove(mentor);
            mentorshipRepository.saveAll(List.of(mentor, mentee));
            log.info(MentorshipMessage.DELETE_MENTEE.getMessage(), menteeId, mentorId);
            return;
        }

        log.info(MentorshipMessage.NO_MENTEE.getMessage(), mentorId, menteeId);
        throw new NoUserMenteeException(ExceptionMessage.NO_USER_MENTEE.getMessage());
    }

    public void deleteMentor(long menteeId, long mentorId) {
        validateIdsEqual(menteeId, mentorId);

        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), mentorId)
                        )
                );
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ExceptionMessage.USER_NOT_FOUND.getMessage(), menteeId)
                        )
                );

        if (mentee.getMentors().remove(mentor)) {
            mentor.getMentees().remove(mentee);
            mentorshipRepository.saveAll(List.of(mentor, mentee));
            log.info(MentorshipMessage.DELETE_MENTOR.getMessage(), mentorId, menteeId);
            return;
        }

        log.info(MentorshipMessage.NO_MENTOR.getMessage(), menteeId, mentorId);
        throw new NoUserMentorException(ExceptionMessage.NO_USER_MENTOR.getMessage());
    }

    private void validateIdsEqual(long menteeId, long mentorId) {
        if (menteeId == mentorId) {
            log.error(MentorshipMessage.EQUALS_IDS.getMessage(), menteeId, mentorId);
            throw new InvalidIdException(ExceptionMessage.EQUAL_IDS.getMessage());
        }
    }
}
