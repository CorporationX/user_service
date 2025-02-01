package school.faang.user_service.service.user;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserAvatarSize;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final String JPEG = "jpeg";
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final PersonToUserMapper personToUserMapper;
    private final S3Service s3Service;

    @Value("${services.s3.max-image-size-mb}")
    private long maxImageSizeMb;

    public List<UserDto> getUsersByIds(List<Long> ids) {
        validateUserIds(ids);
        return userMapper.userListToUserDtoList(userRepository.findAllById(ids));
    }

    public UserDto getUser(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User not found by id: %s", id))));
    }

    @Transactional
    public void updateUserAvatar(Long userId, MultipartFile avatar) {
        checkAvatarSize(avatar);
        checkUser(userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId);
        if(userProfilePic != null) {
            s3Service.deleteFile(userProfilePic.getFileId());
            s3Service.deleteFile(userProfilePic.getSmallFileId());
        }
        String largeAvatarKey = "users/" + userId + "/avatar/large-" + avatar.getOriginalFilename();
        String smallAvatarKey = "users/" + userId + "/avatar/small-" + avatar.getOriginalFilename();
        uploadAvatars(avatar, largeAvatarKey, smallAvatarKey);
        userRepository.saveUserProfilePic(userId, new UserProfilePic(largeAvatarKey, smallAvatarKey));
    }

    public byte[] getUserAvatar(Long userId, UserAvatarSize userAvatarSize) {
        UserProfilePic profilePic = userRepository.findUserProfilePicByUserId(userId);
        checkUserProfilePic(userId, profilePic);
        try (InputStream userAvatarStream = getAvatarStream(profilePic, userAvatarSize)) {
            return userAvatarStream.readAllBytes();
        } catch (IOException e) {
            log.error("File processing error", e);
            throw new RuntimeException("File processing error", e);
        }
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        UserProfilePic profilePic = userRepository.findUserProfilePicByUserId(userId);
        checkUserProfilePic(userId, profilePic);
        s3Service.deleteFile(profilePic.getFileId());
        s3Service.deleteFile(profilePic.getSmallFileId());
        userRepository.deleteUserProfilePicByUserId(userId);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User not found by id: %s", userId)));
        user.setBanned(true);
    }

    private void uploadAvatars(MultipartFile avatar, String largeAvatarKey, String smallAvatarKey) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(avatar.getContentType());
        InputStream largeAvatarStream;
        InputStream smallAvatarStream;
        try {
            largeAvatarStream = resizeImage(avatar.getInputStream(), UserAvatarSize.LARGE.getMaxSideSize());
            smallAvatarStream = resizeImage(avatar.getInputStream(), UserAvatarSize.SMALL.getMaxSideSize());
        } catch (IOException e) {
            log.error("File processing error", e);
            throw new RuntimeException("File processing error", e);
        }
        s3Service.uploadFile(largeAvatarKey, largeAvatarStream, metadata);
        s3Service.uploadFile(smallAvatarKey, smallAvatarStream, metadata);
    }

    private InputStream getAvatarStream(UserProfilePic profilePic, UserAvatarSize userAvatarSize) throws IOException {
        InputStream userAvatarStream;
        if (userAvatarSize.equals(UserAvatarSize.LARGE)) {
            userAvatarStream = s3Service.downloadFile(profilePic.getFileId());
        } else if (userAvatarSize.equals(UserAvatarSize.SMALL)) {
            userAvatarStream = s3Service.downloadFile(profilePic.getSmallFileId());
        } else {
            log.error("Unsupported avatar size");
            throw new IllegalArgumentException("Unsupported avatar size");
        }
        return userAvatarStream;
    }

    private InputStream resizeImage(InputStream inputStream, int maxSize) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(inputStream)
                .size(maxSize, maxSize)
                .outputFormat(JPEG)
                .toOutputStream(outputStream);
        byte[] resizedImageBytes = outputStream.toByteArray();
        return new ByteArrayInputStream(resizedImageBytes);
    }

    private void checkUser(Long userId) {
        if(!userRepository.existsById(userId)) {
            log.error("User with id: {} does not exist", userId);
            throw new IllegalArgumentException(String.format("User with id: %s does not exist", userId));
        }
    }

    private void checkAvatarSize(MultipartFile avatar) {
        if(avatar.getSize() > maxImageSizeMb * 1024 * 1024) {
            log.error("The image size must not exceed 5MB");
            throw new IllegalArgumentException("The image size must not exceed 5MB.");
        }
    }

    private static void checkUserProfilePic(Long userId, UserProfilePic profilePic) {
        if (profilePic == null) {
            log.error("Avatar not found for userId: {}", userId);
            throw new EntityNotFoundException(String.format("Avatar not found for userId: %s", userId));
        }
    }

    private void validateUserIds(List<Long> ids) {
        if (ids.stream().anyMatch(id -> id < 1)) {
            log.error("Invalid user ID passed. User ID must not be less than 1");
            throw new IllegalArgumentException("Invalid user ID passed. User ID must not be less than 1");
        }
    }

    @Transactional
    public void importUsersFromCSV(InputStream csvInputStream) throws IOException {
        List<Person> persons = null;
        try {
            persons = parseCsv(csvInputStream);
        } catch (IOException e) {
            log.error("File processing error", e);
            throw new IOException("File processing error", e);
        }

        for (Person person : persons) {
            Optional<User> existingUser = userRepository.findByEmail(person.getEmail());
            if (existingUser.isPresent()) {
                log.warn("User with email {} already exists. Skipping...", person.getEmail());
                continue;
            }

            User user = personToUserMapper.personToUser(person);
            user.setPassword(generateRandomPassword());

            // Check if country exists in the database, if not - create a new one
            Country country = countryRepository.findByTitle(person.getCountry())
                    .orElseGet(() -> {
                        Country c = new Country();
                        c.setTitle(person.getCountry());
                        return countryRepository.save(c);
                    });

            user.setCountry(country);

            userRepository.save(user);
        }
    }

    private List<Person> parseCsv(InputStream csvInputStream) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnReordering(true);
        MappingIterator<Person> it = csvMapper.readerFor(Person.class).with(schema).readValues(csvInputStream);
        return it.readAll();
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
