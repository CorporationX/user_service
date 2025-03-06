package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public List<UserDto> getMentees(long userId) {
        User user = userService.findUserById(userId);

        return userMapper.toDto(user.getMentees());
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        User user = userService.findUserById(userId);

        return userMapper.toDto(user.getMentors());
    }

    @Override
    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = userService.findUserById(mentorId);
        boolean isRemoved = mentor.getMentees().removeIf(mentee -> mentee.getId().equals(menteeId));

        if (!isRemoved) {
            throw new EntityNotFoundException(String.format("Mentor %s does not have a mentee with %d id", mentor.getUsername(), menteeId));
        }

        userService.saveUser(mentor);
    }

    @Override
    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userService.findUserById(menteeId);
        boolean isRemoved = mentee.getMentors().removeIf(mentor -> mentor.getId().equals(mentorId));

        if (!isRemoved) {
            throw new EntityNotFoundException("User " + mentee.getUsername() + " does not have a mentor with " + mentorId + " id");
        }

        userService.saveUser(mentee);
    }
}

