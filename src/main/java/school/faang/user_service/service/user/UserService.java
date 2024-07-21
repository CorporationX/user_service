package school.faang.user_service.service.user;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.generator.password.UserPasswordGenerator;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.amazonS3.S3Service;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.user.pic.PicProcessor;
import school.faang.user_service.threadPool.ThreadPoolForConvertCsvFile;
import school.faang.user_service.validator.UserValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.*;
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
    private final PersonMapper personMapper;
    private final ThreadPoolForConvertCsvFile threadPoolForConvertCsvFile;
    private final UserPasswordGenerator userPasswordGenerator;
    private final HashMapCountry hashMapCountry;
    private final CountryService countryService;

    @Transactional(readOnly = true)
    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("The user with id not found. id: {}", userId);
                    return new DataValidationException("Пользователь с id " + userId + " не найден");
                });
    }


    @Transactional
    public void saveUser(User user) {
        userValidator.validateUserNotExists(user);
        log.debug("Saved user to db: {}", user);
        userRepository.save(user);
    }

    public List<Long> getIdsFollowersUser(@PathVariable("userId") long userId) {
        return userRepository.getIdsFollowersUser(userId);
    }

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> users = userRepository.findPremiumUsers();
        log.debug("Received users from db: {}", users);

        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(users, userFilterDto))
                .map(userMapper::toDto).toList();
    }

    public UserDto uploadProfilePicture(Long userId, MultipartFile picture) {
        User user = getUserById(userId);

        String folder = String.format("%d_%s", user.getId(), user.getUsername());

        ByteArrayOutputStream bigPicBaos = picProcessor.getPicBaosByLength(picture, BIG_PIC_MAX_SIDE_LENGTH);
        ByteArrayOutputStream smallPicBaos = picProcessor.getPicBaosByLength(picture, SMALL_PIC_MAX_SIDE_LENGTH);

        ObjectMetadata bigPicMetadata = picProcessor.getPicMetaData(picture, bigPicBaos);
        ObjectMetadata smallPicMetadata = picProcessor.getPicMetaData(picture, smallPicBaos);

        String keyBigPic = uploadPicture(folder, bigPicBaos, bigPicMetadata);
        String keySmallPic = uploadPicture(folder, smallPicBaos, smallPicMetadata);

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

    public ResponseEntity<byte[]> downloadProfilePicture(Long userId) {
        User user = getUserById(userId);
        userValidator.checkExistPicId(user);
        String key = user.getUserProfilePic().getFileId();
        log.info("Началась загрузка картинки пользователя с id: {}", user.getId());
        InputStream inputStreamPic = s3Service.downloadPicture(key);
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

    public UserDto deleteProfilePicture(Long userId) {
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

    private String uploadPicture(String folder, ByteArrayOutputStream picture, ObjectMetadata objectMetadata) {
        return s3Service.uploadPicture(folder, picture, objectMetadata);
    }

    @Transactional
    public void convertCsvFile(List<Person> persons) {
        ExecutorService executor = threadPoolForConvertCsvFile.taskExecutor();
        List<CompletableFuture<Void>> futures = persons.stream()
                .map(person -> {
                    User user = personMapper.toEntity(person);
                    log.debug("Received user from Csv file. User: {}", user);
                    return CompletableFuture.runAsync(() -> setUpBeforeSaveToDB(user), executor);
                })
                .toList();


        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("An error occurred while waiting for completion of tasks", e);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            executor.shutdownNow();
            log.error("Timeout occurred while waiting for completion of tasks", e);
            throw new RuntimeException(e);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            log.error("An error occurred while shutting down executor", e);
            Thread.currentThread().interrupt();
        }
    }

    private void setUpBeforeSaveToDB(User user) {
        user.setPassword(userPasswordGenerator.createPassword());

        User readyUser;
        if (hashMapCountry.isContainsKey(user.getCountry().getTitle())) {
            readyUser = countryService.setCountryForUser(user);
        } else {
            readyUser = countryService.saveCountry(user.getCountry(), user);
        }
        saveUser(readyUser);
    }
}