package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.validator.user.UserValidator;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final CountryService countryService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        userValidator.uniqueUsername(userDto.getUsername());
        userValidator.uniqueEmail(userDto.getEmail());
        userValidator.uniquePhone(userDto.getPhone());

        User user = userMapper.toEntity(userDto);
        user.setCountry(countryService.getCountryOrCreate(user.getCountry().getTitle()));

        String avatarUrl = avatarService.getRandomAvatarUrl();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatarUrl);
        user.setUserProfilePic(userProfilePic);

        userRepository.save(user);

        return userMapper.toDto(user);
    }
}