package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.dto.register.UserProfileDto;
import school.faang.user_service.dto.register.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarGenerationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.S3UploadException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.service.S3Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final DiceBearClient diceBearClient;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;

    public User getUser(Long userId) {
        if (userId == null) {
            logger.error("User ID is null");
            throw new IllegalArgumentException("User ID must not be null");
        }

        return userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found", userId);
            return new EntityNotFoundException("User with ID: " + userId + " not found");
        });
    }

    @Transactional
    public UserProfileDto registerUser(UserRegistrationDto registrationDto) {
        validateRegistrationDto(registrationDto);

        Country country = countryRepository.findById(registrationDto.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid country ID"));

        User user = userMapper.userRegistrationDtoToUser(registrationDto);
        user.setActive(true);
        user.setCountry(country);

        byte[] avatarData = generateAvatar(registrationDto.getSeed());

        String avatarFileId = uploadAvatarToS3(avatarData, registrationDto.getSeed());

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatarFileId);
        userProfilePic.setSmallFileId(avatarFileId);

        user.setUserProfilePic(userProfilePic);

        userRepository.save(user);

        return userMapper.userToUserProfileDto(user);
    }

    private void validateRegistrationDto(UserRegistrationDto registrationDto) {
        if (registrationDto.getCountryId() == null || registrationDto.getCountryId() <= 0) {
            throw new IllegalArgumentException("Country ID is required and must be a positive number.");
        }
        if (registrationDto.getUsername() == null || registrationDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (registrationDto.getEmail() == null || registrationDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (registrationDto.getPassword() == null || registrationDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
    }

    private byte[] generateAvatar(String seed) {
        String avatarSeed = seed != null ? seed : "default-seed";
        try {
            byte[] avatarData = diceBearClient.generateAvatar(avatarSeed).getBody();
            if (avatarData == null || avatarData.length == 0) {
                throw new AvatarGenerationException("Failed to generate avatar from DiceBear API.");
            }
            return avatarData;
        } catch (Exception e) {
            throw new AvatarGenerationException("Error generating avatar.", e);
        }
    }

    private String uploadAvatarToS3(byte[] avatarData, String seed) {
        String uniqueFileName = (seed != null ? seed : "default-seed") + "-" + UUID.randomUUID() + "-avatar.svg";
        try {
            return s3Service.uploadFile(new ByteArrayInputStream(avatarData), avatarData.length, "image/svg+xml");
        } catch (Exception e) {
            throw new S3UploadException("Error uploading avatar to S3.", e);
        }
    }
}
