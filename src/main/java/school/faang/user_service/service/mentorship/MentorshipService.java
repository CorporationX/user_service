package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

//    @Transactional(readOnly = true)
//    public List<UserDto> getMentees(long mentorId) {
//        User user = validateUserId(mentorId);
//        return user.getMentees().stream()
//                .map(userMapper::toDto)
//                .toList();
//    }
//
//    @Transactional(readOnly = true)
//    public List<UserDto> getMentors(long userId) {
//        User user = validateUserId(userId);
//        return user.getMentors().stream()
//                .map(userMapper::toDto)
//                .toList();
//    }

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipRepository.findById(mentorId)
                .ifPresent(mentor -> mentor.getMentees()
                        .removeIf(mentee -> mentee.getId() == menteeId));
    }

    private void validateUserId(long userId, long menteeId) {

    }
}

