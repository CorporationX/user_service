package school.faang.user_service.service.user;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.amazonS3.S3Service;
import school.faang.user_service.service.user.pic.PicProcessor;
import school.faang.user_service.validator.UserValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final PicProcessor picProcessor;
    private final UserValidator userValidator;

    private static final int BIG_PIC_MAX_SIDE_LENGTH = 1080;
    private static final int SMALL_PIC_MAX_SIDE_LENGTH = 170;


    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Пользователь с id " + userId + " не найден"));
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> users = userRepository.findPremiumUsers();

        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(users, userFilterDto))
                .map(userMapper::toDto).toList();
    }

    public UserDto savePic(Long userId, MultipartFile pic) {
        User user = getUserById(userId);

        String folder = String.format("%d_%s", user.getId(), user.getUsername());

        ByteArrayOutputStream bigPicBaos = picProcessor.getPicBaosByLength(pic, BIG_PIC_MAX_SIDE_LENGTH);
        ByteArrayOutputStream smallPicBaos = picProcessor.getPicBaosByLength(pic, SMALL_PIC_MAX_SIDE_LENGTH);

        ObjectMetadata bigPicMetadata = picProcessor.getPicMetaData(pic, bigPicBaos);
        ObjectMetadata smallPicMetadata = picProcessor.getPicMetaData(pic, smallPicBaos);

        String keyBigPic = uploadPic(folder, bigPicBaos, bigPicMetadata);
        String keySmallPic = uploadPic(folder, smallPicBaos, smallPicMetadata);

        if (user.getUserProfilePic() == null) {
            UserProfilePic userProfilePic = UserProfilePic.builder()
                    .fileId(keyBigPic)
                    .smallFileId(keySmallPic)
                    .build();
            user.setUserProfilePic(userProfilePic);
        } else {
            user.getUserProfilePic().setFileId(keyBigPic);
            user.getUserProfilePic().setSmallFileId(keySmallPic);
        }
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public ResponseEntity<byte[]> getPic(Long userId) {
        User user = getUserById(userId);
        userValidator.checkExistPicId(user);
        String key = user.getUserProfilePic().getFileId();
        log.info("Началась загрузка картинки пользователя с id: {}", user.getId());
        InputStream inputStreamPic = s3Service.downloadPic(key);
        log.info("Загрузка картинки пользователя с id: {} завершена", user.getId());

        try {
            byte[] picBytes = inputStreamPic.readAllBytes();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(picBytes, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            log.error("IOException", e);
            throw new RuntimeException(e);
        }
    }

    public UserDto deletePic(Long userId) {
        User user = getUserById(userId);
        userValidator.checkExistPicId(user);
        s3Service.deletePic(user.getUserProfilePic().getFileId());
        s3Service.deletePic(user.getUserProfilePic().getSmallFileId());
        user.setUserProfilePic(null);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("Пользователь с id " + userId + " не найден"));
    }

    private String uploadPic(String folder, ByteArrayOutputStream pic, ObjectMetadata objectMetadata) {
        return s3Service.savePic(folder, pic, objectMetadata);
    }
}