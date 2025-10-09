package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void addMentorship(long mentorId, long menteeId) {
        log.info("Adding mentorship: mentorId={}, menteeId={}", mentorId, menteeId);

        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);

        List<User> mentors = mentee.getMentors();

        boolean alreadyExists = mentors.stream()
                .filter(Objects::nonNull)
                .anyMatch(m -> Objects.equals(m.getId(), mentorId));

        if (alreadyExists) {
            log.info("Mentorship already exists: mentorId={}, menteeId={}", mentorId, menteeId);
            throw new DataValidationException("Связь уже существует");
        }

        mentors.add(mentor);
        mentorshipRepository.save(mentee);

        log.info("Mentorship added successfully: mentorId={}, menteeId={}", mentorId, menteeId);
    }

    @Override
    @Transactional
    public void deleteMentorship(long mentorId, long menteeId) {
        log.info("Deleting mentorship: mentorId={}, menteeId={}", mentorId, menteeId);

        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);

        List<User> mentors = mentee.getMentors();

        boolean removed = mentors.removeIf(m ->
                Objects.equals(m.getId(), mentorId)
        );

        if (!removed) {
            log.warn("Mentorship not found: mentorId={}, menteeId={}", mentorId, menteeId);
            throw new DataValidationException("Связь не найдена");
        }

        mentorshipRepository.save(mentee);

        log.info("Mentorship deleted successfully: mentorId={}, menteeId={}", mentorId, menteeId);
    }

    @Override
    @Transactional
    public List<UserDto> getMentees(long userId) {
        log.debug("Fetching mentees for userId={}", userId);

        User user = mentorshipRepository.getByIdOrThrow(userId);
        List<User> mentees = user.getMentees();

        if (mentees == null || mentees.isEmpty()) {
            log.debug("No mentees found for userId={}", userId);
            return Collections.emptyList();
        }

        return mentees.stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public List<UserDto> getMentors(long userId) {
        log.debug("Fetching mentors for userId={}", userId);

        User user = mentorshipRepository.getByIdOrThrow(userId);
        List<User> mentors = user.getMentors();

        if (mentors == null || mentors.isEmpty()) {
            log.debug("No mentors found for userId={}", userId);
            return Collections.emptyList();
        }

        return mentors.stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDto)
                .toList();
    }
}