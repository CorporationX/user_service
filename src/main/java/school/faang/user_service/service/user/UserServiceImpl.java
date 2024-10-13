package school.faang.user_service.service.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final UserMapper mapper;

    @Transactional
    public void deactivateUser(@NonNull Long id) {
        log.info("deactivating user with id: {}", id);
        goalRepository.deleteUnusedGoalsByMentorId(id);
        eventRepository.deleteAllByOwnerId(id);
        mentorshipService.stopMentorship(id);
        userRepository.updateUserActive(id, false);
        log.info("deactivated user with id: {}", id);
    }

    @Override
    public UserDto getUser(long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public UserDto getUserWithLocaleAndContactPreference(long id) {
        return userRepository.findByIdWithCountryAndContactPreference(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
