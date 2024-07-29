package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    /**
     * Этот метод(createUser) создан для проверки генерации случайных аватаров,
     * все что этого не касается ни имеет никакого логического смысла, когда будете реализовывать
     * логику создания аккаунта пользователя можете удаль этот комментарий xD
     * **/
    @Transactional
    public String createUser() {
        User user = new User();
        user.setUsername("username");
        user.setEmail("email.com");
        user.setPassword("password");
        Country country = new Country();
        country.setId(1);
        user.setCountry(country);

        String avatarUrl = avatarService.getRandomAvatarUrl();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatarUrl);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return String.format("User created! His avatar is here: %s", avatarUrl);
    }
}