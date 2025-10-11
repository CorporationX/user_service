package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Override
    public void addMentorship(long mentorId, long menteeId) {
        if (isMentorshipExist(mentorId, menteeId)) {
            log.info("Такая связь уже существует");
            return;
        }
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);

        log.info("Добавляем ментора {} для менти {}", mentorId, menteeId);
        mentee.getMentors().add(mentor);
        mentor.getMentees().add(mentee);
        mentorshipRepository.save(mentee);
    }

    @Override
    public void deleteMentorship(long menteeId, long mentorId) {
        if (!isMentorshipExist(mentorId, menteeId)) {
            log.info("Такой связи нет");
            return;
        }
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);

        log.info("Удаляем ментора {} у менти {}", mentorId, menteeId);
        mentee.getMentors().remove(mentor);
        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentee);
    }

    @Override
    public List<UserDto> getMentees(long userId) {
        User mentor = mentorshipRepository.getByIdOrThrow(userId);

        return mentor.getMentees().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        User mentee = mentorshipRepository.getByIdOrThrow(userId);

        return mentee.getMentors().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    private boolean isMentorshipExist(long mentorId, long menteeId) {
        return mentorshipRepository.getByIdOrThrow(mentorId)
                .getMentees()
                .contains(mentorshipRepository.getByIdOrThrow(menteeId));
    }
}