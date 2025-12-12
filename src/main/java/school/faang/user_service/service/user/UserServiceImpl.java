package school.faang.user_service.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.avatar.RandomAvatarService;
import school.faang.user_service.service.s3.S3service;

import java.util.Objects;
import java.util.stream.Stream;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final S3service s3service;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final RandomAvatarService randomAvatarService;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${user.password.min.length}")
    private int minPasswordLength;

    @Transactional
    @Override
    public UserDto create(CreateUserDto userDto) {
        createValidation(userDto, minPasswordLength);
        User user = userMapper.toUser(userDto);
        user.setActive(true);
        user.setCountry(countryRepository.getByIdOrThrow(userDto.countryId()));
        user.setUserProfilePic(randomAvatarService.generateRandomAvatarForUser(userDto.username()));
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public Long delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with id " + userId + " not found!"));
        deleteAvatarIfStoredInS3(user.getUserProfilePic());
        userRepository.delete(user);
        log.info("User {} deleted", user.getId());
        return user.getId();
    }

    @Transactional
    @Override
    public UserDto update(long userId, UpdateUserDto updateUserDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }

        User user = userRepository.getByIdOrThrow(userId);
        if (!updateUserDto.username().equals(user.getUsername())) {
            deleteAvatarIfStoredInS3(user.getUserProfilePic());
            user.setUserProfilePic(randomAvatarService.generateRandomAvatarForUser(updateUserDto.username()));
        }

        userMapper.update(updateUserDto, user);
        user.setCountry(countryRepository.getByIdOrThrow(updateUserDto.countryId()));
        user = userRepository.save(user);

        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateChatIdByEmail(long chatId, String email) { //TODO: изменить параметры на @RequestBody
        User currentUser = userRepository.findByEmailIgnoreCase(email);
        if (currentUser == null) {
            throw new EntityNotFoundException(String.format("User with email '%s' does not exist", email));
        }
        currentUser.setChatId(chatId);
        return userMapper.toUserDto(currentUser);
    }

    @Transactional
    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toUserDtos(users);
    }

    private void createValidation(CreateUserDto userDto, int minPasswordLength) {
        if (userRepository.existsByUsername(userDto.username())) {
            throw new DataValidationException("User with username " + userDto.username() + " already exists!");
        }
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        if (userRepository.existsByEmailIgnoreCase(userDto.email())) {
            throw new DataValidationException("User with email " + userDto.email() + " already exists!");
        }
        if (userRepository.existsByPhone(userDto.phone())) {
            throw new DataValidationException("User with phone " + userDto.phone() + " already exists!");
        }
    }

    private void deleteAvatarIfStoredInS3(UserProfilePic pic) {
        if (pic == null) {
            return;
        }

        Stream.of(pic.getFileId(), pic.getSmallFileId())
                .filter(Objects::nonNull)
                .filter(id -> !id.startsWith("http"))
                .forEach(id -> {
                    try {
                        s3service.deleteFileFromS3(id);
                        log.info("Deleted avatar file from S3: {}", id);
                    } catch (Exception e) {
                        log.error("Error deleting avatar file {} from S3: ", id, e);
                    }
                });
    }
}
