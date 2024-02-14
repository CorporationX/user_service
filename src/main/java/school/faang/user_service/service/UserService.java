package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final UserMapper userMapper;
    private final UserProfilePic generatedUserProfilePic;

    public UserDto createUser(UserRegistrationDto userDto) {
        User user = userMapper.toEntity(userDto);

        if (user.getUserProfilePic() == null) {
            user.setUserProfilePic(generatedUserProfilePic);
        }
        Country country = countryService.getCountryByTitle(userDto.getCountry());
        user.setCountry(country);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto getUserDtoById(long id) {
        return userMapper.toDto(getUserById(id));
    }

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id = %s not found", id)));
    }

    public UserProfilePic getUserPicUrlById(long id) {
        return getUserById(id).getUserProfilePic();
    }
}