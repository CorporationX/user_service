package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.NonUniqueFieldsException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.ProfilePictureService;
import school.faang.user_service.service.UserLifeCycleService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLifeCycleServiceImpl implements UserLifeCycleService {

    private final MentorshipService mentorshipService;
    private final ProfilePictureService profilePictureService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;

    @Override
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
    public UserDto registrationUser(UserRegistrationDto userRegistrationDto) {
        existsByUsernameEmailAndPhone(userRegistrationDto);
        Country country = countryRepository.findById(userRegistrationDto.countryId())
                .orElseThrow(() -> new EntityNotFoundException("Country not found"));

        UserProfilePic userProfilePic = profilePictureService.saveProfilePictures(userRegistrationDto);
        User user = configureUser(userRegistrationDto, userProfilePic, country);
        return userMapper.toDto(userRepository.save(user));
    }

    private void existsByUsernameEmailAndPhone(UserRegistrationDto dto) {
        if (userRepository.existsByUsernameAndEmailAndPhone(dto.username(), dto.email(), dto.phone())) {
            throw new NonUniqueFieldsException("Username/email/phone already in use");
        }
    }

    private User configureUser(UserRegistrationDto dto, UserProfilePic profilePic, Country country) {
        User user = userMapper.toEntity(dto);
        user.setCountry(country);
        user.setUserProfilePic(profilePic);
        return user;
    }
}
