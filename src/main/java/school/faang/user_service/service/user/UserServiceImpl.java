package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.amazon_s3.S3Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int MAX_IMG_SIZE_IN_BYTES = 5_242_880;

    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final S3Service s3Service;

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto setUserAvatar(long userId, MultipartFile avatar) throws IOException {
        int maxAvatarWidthAndLength = 1080;
        int maxSmallCopyAvatarWidthAndLength = 170;
        if (avatar.getSize() > MAX_IMG_SIZE_IN_BYTES) {
            log.error("Пользователь с id: {} пытается загрузить фото размером более 5мб.", userId);
            throw new DataValidationException("Нельзя загрузить фото размером более 5мб.");
        }
        User user = userRepository.getByIdOrThrow(userId);
        String folder = userId + user.getUsername();
        user.getUserProfilePic()
                .setFileId(s3Service.uploadFile(userId, avatar, folder, maxAvatarWidthAndLength));
        user.getUserProfilePic()
                .setSmallFileId(s3Service.uploadFile(userId, avatar, folder, maxSmallCopyAvatarWidthAndLength));
        return userMapper.toUserDto(user);
    }

    @Override
    public InputStream getUserAvatar(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return s3Service.downloadFile(user.getUserProfilePic().getFileId());
    }

    @Override
    public void deleteUserAvatar(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());
        user.getUserProfilePic().setFileId(null);
        user.getUserProfilePic().setSmallFileId(null);
    }
}
