package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Component
@RequiredArgsConstructor//or I can use @AutoWired and constructor with required fields instead
public class MentorshipService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;

    public List<UserDto> getMentees(long userId) {
        List<User> mentees = userRepository.findById(userId).orElseThrow().getMentees();
        return mentees.stream().map(mentee -> userMapper.toDto(mentee)).toList();
        //   return userMapper.toDto(mentees);

    }

    public List<UserDto> getMentors(long userId) {
        return null;//logic to find mentor's mentees
    }


}