package school.faang.user_service.service.user;

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
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserLifeCycleService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLifeCycleServiceImpl implements UserLifeCycleService {

    private final MentorshipService mentorshipService;

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
    @Transactional
    public UserDto registrationUser(UserRegistrationDto userRegistrationDto) {
        String seed = profilePictureService.getSeed();

        byte[] picture = profilePictureService.generatePicture(seed);
        byte[] smallPicture = profilePictureService.generateSmallPicture(seed);

        String pictureKey = s3Service.uploadFileAndGetKey(picture, userRegistrationDto);
        String smallPictureKey = s3Service.uploadFileAndGetKey(smallPicture, userRegistrationDto);
        User user = configureUser(userRegistrationDto, pictureKey, smallPictureKey);

        log.info("Saving user: {}", userRegistrationDto.username());
        return userMapper.toDto(userRepository.save(user));
    }

    private User configureUser(UserRegistrationDto userRegistrationDto, String pictureKey, String smallPictureKey) {
        log.info("Configuring user: {}", userRegistrationDto.username());

        Country country = countryRepository.findById(userRegistrationDto.countryId()).orElseThrow();
        UserProfilePic userProfilePic = userMapper.mapUserProfilePic(pictureKey, smallPictureKey);

        User user = userMapper.toEntity(userRegistrationDto);
        user.setCountry(country);
        user.setUserProfilePic(userProfilePic);

        return user;
    }
}
