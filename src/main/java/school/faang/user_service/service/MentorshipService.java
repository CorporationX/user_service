package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto stopUserMentorship(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<User> menteeList = user.getMentees();
        menteeList.forEach(mentee -> mentee.getMentors().remove(user));

        menteeList.forEach(this::setMenteeGoalsAsSelfSet);

        return userMapper.toDto(user);
    }
    /**
     * Устанавливает цели mentee как самостоятельно поставленные.
     *
     * @param mentee подопечный пользователь
     */
    private void setMenteeGoalsAsSelfSet(User mentee) {
        mentee.getGoals().forEach(goal -> goal.setMentor(mentee));
    }
}
