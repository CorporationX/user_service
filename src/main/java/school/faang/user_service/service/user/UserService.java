package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final CountryService countryService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final S3Service s3Service;

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    @Transactional
    public UserDto createUser(UserDto userDto) throws IOException, TranscoderException {
        userValidator.uniqueUsername(userDto.getUsername());
        userValidator.uniqueEmail(userDto.getEmail());
        userValidator.uniquePhone(userDto.getPhone());

        User user = userMapper.toEntity(userDto);
        user.setCountry(countryService.getCountryOrCreate(user.getCountry().getTitle()));

        userRepository.save(user);

        s3Service.uploadAvatar(user.getId(), avatarService.downloadSvgAsMultipartFile(avatarService.getRandomAvatarUrl()));

        return userMapper.toDto(user);
    }
}