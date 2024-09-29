package school.faang.user_service.service.user;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.remote.AmazonS3CustomException;
import school.faang.user_service.exception.remote.ImageGeneratorException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.RemoteImageService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.util.file.CsvUtil;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final RemoteImageService remoteImageService;
    private final S3Service s3Service;
    private final CsvUtil csvUtil;
    private final CountryService countryService;

    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with this id does not exist in the database"));
    }

    public UserDto getUser(long userId) {
        User existedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " does not exist"));

        return userMapper.toDto(existedUser);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        ids.forEach(userValidator::validateUserIdIsPositiveAndNotNull);

        return userMapper.toDtos(userRepository.findAllById(ids));
    }

    @Transactional
    public List<UserDto> saveUsersFromCsvFile(MultipartFile multipartFile) {
        List<UserDto> response = new ArrayList<>();

        CompletableFuture.supplyAsync(() -> csvUtil.parseCsvMultipartFile(multipartFile, Person.class))
                .thenAccept(result -> {
                    List<User> users = userMapper.toEntities(result);
                    users.parallelStream()
                            .forEach(user -> {
                                setDefaultPassword(users);

                                try {
                                    Country country = countryService.findCountryByTitle(user.getCountry().getTitle());
                                    user.setCountry(country);
                                } catch (EntityNotFoundException e) {
                                    Country country = countryService.saveCountry(user.getCountry());
                                    user.setCountry(country);
                                }
                            });
                    userRepository.saveAll(users);
                    users.forEach(user -> response.add(userMapper.toDto(user)));
                })
                .join();

        return response;
    }

    private void setDefaultPassword(List<User> users) {
        users.forEach(user -> user.setPassword(user.getUsername()));
    }

    public List<User> getUsersById(List<Long> usersId) {
        return userRepository.findAllById(usersId);
    }

    @Transactional
    public UserDto registerUser(UserRegistrationDto userRegistrationDto) {
        log.info("registerUser() - start : userRegistrationDto = {}", userRegistrationDto);

        User user = userMapper.toEntity(userRegistrationDto);
        userValidator.validateUserConstrains(user);

        setUserDefaultAvatar(user);
        log.info("Trying save new user {}", user);
        userRepository.save(user);

        log.info("registerUser() - end : user = {}", user);
        return userMapper.toDto(user);
    }

    private void setUserDefaultAvatar(User user) {
        String key;

        try {
            key = assignUserRemoteRandomPicture(user);
        } catch (Exception e) {
            log.info("Couldn't apply remote picture to user profile. Trying to apply default picture from cloud");

            if (s3Service.isDefaultPictureExistsOnCloud()) {
                log.info("Found default picture on cloud");

                key = s3Service.getDefaultPictureName();
            } else {
                log.info("Couldn't found default picture on cloud");
                return;
            }
        }

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(key)
                .build());

        log.info("Finish assignment random picture for user {}", user);
    }

    private String assignUserRemoteRandomPicture(User user) {
        log.info("Trying to assign random picture for user {}", user);

        try {
            ResponseEntity<byte[]> pictureContent = remoteImageService.getUserProfileImageFromRemoteService();

            String s3Folder = user.getUsername() + user.getId() + "profilePic";
            return s3Service.uploadHttpData(pictureContent, s3Folder);
        } catch (FeignException | AmazonS3CustomException e) {
            log.error(e.getMessage());
            throw new ImageGeneratorException(e.getMessage());
        }
    }
}
