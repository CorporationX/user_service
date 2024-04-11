package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.exceptions.UserNotFoundException;
import school.faang.user_service.service.exceptions.messageerror.MessageError;
import school.faang.user_service.service.validators.UserValidator;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivate(long userId) {
        userValidator.userExistenceInRepo(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));
        user.setActive(false);
        userRepository.save(user);
        List<Long> eventIds = eventService.getOwnedEvents(userId).stream().map(EventDto::getId).toList();
        for (Long eventId : eventIds) {
            eventService.deleteEvent(eventId);
        }
        mentorshipService.deleteAllMentorMentorship(userId);
    }
}
