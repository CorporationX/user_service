package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.s3.S3CompatibleService;
import school.faang.user_service.service.avatar_api.AvatarApiService;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;
    private final List<UserFilter> userFilters;
    private final AvatarApiService avatarApiService;
    private final S3CompatibleService s3CompatibleService;

    @Value("${avatar_api.dice_bear.url_format}")
    private String DEFAULT_AVATAR_FORMAT;

    @Transactional(readOnly = true)
    public List<User> findPremiumUser(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(premiumUsers, (stream, filter) -> filter.apply(stream, filterDto), (stream, filter) -> stream)
                .toList();
    }

    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User does not exist"));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    @Transactional
    public User registerNewUser(User newUser) {
        validateUsername(newUser.getUsername());
        validateEmail(newUser.getEmail());
        validatePhone(newUser.getPhone());
        validateCountry(newUser.getCountry().getId());

        User saved = userRepository.save(newUser);
        generateAndSaveDefaultAvatar(saved);
        log.info("Successfully registered a new user (ID={})", saved.getId());
        return userRepository.save(newUser);
    }

    private void generateAndSaveDefaultAvatar(User created) {
        byte[] defaultAvatarData = avatarApiService.generateDefaultAvatar(created.getUsername());
        String fileKey = String.format(DEFAULT_AVATAR_FORMAT, created.getId());
        s3CompatibleService.uploadFile(defaultAvatarData, fileKey, "image/svg+xml");
        setAvatarKeyForUser(created, fileKey);
    }

    private void setAvatarKeyForUser(User toUpdate, String fileKey) {
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(fileKey);
        profilePic.setSmallFileId(fileKey);
        toUpdate.setUserProfilePic(profilePic);
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException(String.format("Username %s is already in use", username));
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(String.format("Email %s is already in use", email));
        }
    }

    private void validatePhone(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new IllegalStateException(String.format("Phone %s is already in use", phone));
        }
    }

    private void validateCountry(Long countryId) {
        if (!countryRepository.existsById(countryId)) {
            throw new IllegalStateException(String.format("Country with ID %d does not exist", countryId));
        }
    }
}
