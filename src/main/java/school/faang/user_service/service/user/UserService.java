package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.user.UserProfilePicMapper;
import school.faang.user_service.parser.PersonParser;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.UserProfilePicS3Service;
import school.faang.user_service.util.PasswordGenerator;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordGenerator passwordGenerator;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;
    private final PersonParser personParser;
    private final UserProfilePicMapper profilePicMapper;
    private final Executor taskExecutor;
    private final UserProfilePicS3Service userProfilePicS3Service;
    @Value("${spring.students.partitionSize}")
    private int partitionSize;
    @Value("${users.profile_picture.max_file_size}")
    private long maxFileSize;
    @Value("${users.profile_picture.folder}")
    private String folder;

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public void validateUsers(Long... userIds) {
        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User with id " + userId + " not found.");
            }
        }
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        return userMapper.toDto(getUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void saveStudents(MultipartFile studentsFile) {
        List<Person> students = personParser.parse(studentsFile);

        if (students.size() > partitionSize) {
            ListUtils.partition(students, partitionSize)
                    .forEach(partition -> taskExecutor.execute(() -> mapAndSaveStudents(partition)));
        } else {
            taskExecutor.execute(() -> mapAndSaveStudents(students));
        }
    }

    @Transactional
    public UserProfilePicDto saveProfilePic(MultipartFile file, Long userId) {
        if (file.getSize() >= maxFileSize) {
            throw new DataValidationException("File size must be less than 5MB");
        }
        User user = getUser(userId);
        String folder = getFolder(user);

        UserProfilePic userProfilePic = userProfilePicS3Service.upload(file, folder);
        user.setUserProfilePic(userProfilePic);

        return profilePicMapper.toDto(userProfilePic);
    }

    @Transactional(readOnly = true)
    public InputStream getProfilePic(Long userId) {
        User user = getUser(userId);
        String key = user.getUserProfilePic().getFileId();
        return userProfilePicS3Service.download(key);
    }

    @Transactional
    public void deleteProfilePic(Long userId) {
        User user = getUser(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();
        userProfilePicS3Service.delete(userProfilePic.getFileId());
        userProfilePicS3Service.delete(userProfilePic.getSmallFileId());

        user.setUserProfilePic(null);
        userRepository.save(user);
    }

    private void mapAndSaveStudents(List<Person> students) {
        List<User> users = students.stream()
                .map(personMapper::toUser)
                .peek(user -> {
                    user.setPassword(passwordGenerator.generatePassword());
                    countryRepository.findByTitle(user.getCountry().getTitle())
                            .ifPresentOrElse(
                                    user::setCountry,
                                    () -> user.setCountry(countryRepository.save(user.getCountry()))
                            );
                })
                .toList();

        log.info("Successfully mapped from Person and saved {} users", users.size());
        userRepository.saveAll(users);
    }

    private String getFolder(User user) {
        return String.format("%s/%d_%s", folder, user.getId(), user.getUsername());
    }

    @Transactional
    public void setBanForUser(Long userId) {
        userRepository.setBanUser(userId);
        log.info("Banned user with id: {}", userId);
    }
}