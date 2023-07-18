package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentee.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        if (!mentorshipRepository.existsById(userId)) {
            throw new RuntimeException("User with id not found");
        }
        User mentor = new User();
        mentor.setId(userId);
        mentorshipRepository.findUserById(userId);
        return userMapper.toUserListDto(mentor.getMentees());
    }
}
