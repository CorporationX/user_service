package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.NoUserInDataBaseException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        List<UserDto> result = validateId(userId).getMentees()
                .stream().map(userMapper::toDto).toList();
        log.info("Mentees for userId={} have taken successfully from DB", userId);

        return result;
    }

    public List<UserDto> getMentors(long userId) {
        List<UserDto> result = validateId(userId).getMentors()
                .stream().map(userMapper::toDto).toList();
        log.info("Mentors for userId={} have taken successfully from DB", userId);

        return result;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = validateId(mentorId);
        User mentee = validateId(menteeId);

        removeUserFromList(mentor.getMentees(), mentee);
        log.info("Mentee was deleted from mentor successfully, menteeId={}, mentorId={}", menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = validateId(mentorId);
        User mentee = validateId(menteeId);

        removeUserFromList(mentee.getMentors(), mentor);
        log.info("Mentor was deleted from mentee successfully, menteeId={}, mentorId={}", menteeId, mentorId);
    }

    private User validateId(long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(() -> {
            throw new NoUserInDataBaseException("User with this id does not exist");
        });
    }

    private void removeUserFromList(List<User> list, User deletedUser) {
        if (list.contains(deletedUser)) {
            list.remove(deletedUser);
        }
    }
}