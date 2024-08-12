package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.s3.FileDownloadException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.randomAvatar.RandomAvatarService;
import school.faang.user_service.service.s3Service.S3Service;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserFilterValidation;
import school.faang.user_service.validator.user.UserValidator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserFilterValidation userFilterValidation;
    private final UserMapper userMapper;
    private final S3Service s3Service;
    private final RandomAvatarService randomAvatarService;

    private final UserValidator userValidator;

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new DataValidationException("UserFilter is nullable");
        }

        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toDtoList(premiumUsers);
        }

        return filterUsers(userFilterDto, premiumUsers);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getRegularUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new DataValidationException("UserFilter is nullable");
        }

        List<User> regularUsers = userRepository.findRegularUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toDtoList(regularUsers);
        }

        return filterUsers(userFilterDto, regularUsers);
    }

    private List<UserDto> filterUsers(UserFilterDto userFilterDto, List<User> users) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .reduce(users.stream(),
                        (stream, filter) -> filter.apply(stream, userFilterDto),
                        Stream::concat)
                .map(userMapper::toDto)
                .toList();

    }

    @Transactional
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public void existUserById(long id) {
        if (!userRepository.existsById(id)) {
            log.error("User with id :{} doesn't exist!", id);
            throw new EntityNotFoundException("User with id :" + id + " doesn't exist!");
        }
    }

    @Transactional
    public String generateRandomAvatar(Long userId) {
        userValidator.isCurrentUser(userId);

        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new DataValidationException(String.format("User with %s id doesn't exist", userId)));
        User userToRandomAvatar = user.get();

        File randomPhoto = randomAvatarService.getRandomPhoto();

        String folder = generateFolder(userToRandomAvatar);
        String avatarId = s3Service.uploadFile(randomPhoto, folder);

        userValidator.isValidUserAvatarId(avatarId);

        userToRandomAvatar.setUserProfilePic(UserProfilePic.builder().fileId(avatarId).build());
        userRepository.save(userToRandomAvatar);

        return avatarId;
    }

    @Transactional
    public byte[] getUserRandomAvatar(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new DataValidationException(String.format("User with %s id doesn't exist", userId)));
        User userWithRandomAvatar = user.get();

        String avatarId = userWithRandomAvatar.getUserProfilePic().getFileId();

        if (avatarId == null) {
            throw new FileDownloadException("Avatar id can't be nullable");
        }

        InputStream file = s3Service.getFile(avatarId);

        try {
            return file.readAllBytes();
        } catch (IOException e) {
            throw new FileDownloadException(e.getMessage());
        }
    }

    private String generateFolder(User userToRandomAvatar) {
        return String.format("users/avatar/%s_%s", userToRandomAvatar.getUsername(), userToRandomAvatar.getId());
    }
}