package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.s3.S3Config;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.exceptions.UserNotFoundException;
import school.faang.user_service.service.exceptions.messageerror.MessageError;
import school.faang.user_service.service.validators.UserValidator;
import school.faang.user_service.service.s3_minio_service.S3Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${dicebear.pic-base-url}")
    private String large_avatar;

    @Value("${dicebear.pic-base-url-small}")
    private String small_avatar;

    private final S3Service s3Service;
    private final S3Config s3Config;
    private final String bucketName;
    private final UserValidator userValidator;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    public UserDto getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));
        return userMapper.toDto(user);
    }

    public UserDto create(UserDto userDto) {

        checkUserAlreadyExists(userDto);

        User user = userMapper.toEntity(userDto);
        user.setUserProfilePic(getRandomAvatar());
        user.setActive(true);

        User createdUser = userRepository.save(user);
        String fileNameSmallAva = "small_" + user.getId() + ".svg";
        String fileNameLargeAva = "large_" + user.getId() + ".svg";

        s3Service.
                saveSvgToS3(user.getUserProfilePic().getSmallFileId(),
                        bucketName,
                        fileNameSmallAva);
        s3Service.
                saveSvgToS3(user.getUserProfilePic().getFileId(),
                        bucketName,
                        fileNameLargeAva);
        return userMapper.toDto(createdUser);

    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userMapper.toDto(userRepository.findAllById(ids));
    }

    @Transactional
    public void deactivate(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));
        user.setActive(false);
        List<Long> eventIds = eventService.getOwnedEvents(userId).stream().map(EventDto::getId).toList();
        for (Long eventId : eventIds) {
            eventService.deleteEvent(eventId);
        }
        mentorshipService.deleteAllMentorMentorship(userId);
    }

    private UserProfilePic getRandomAvatar() {

        UUID seed = UUID.randomUUID();
        return UserProfilePic.builder().
                smallFileId(small_avatar + seed).
                fileId(large_avatar + seed).build();

    }

    private void checkUserAlreadyExists(UserDto userDto) {

        boolean exists = userRepository.findById(userDto.getId()).isPresent();

        if (exists) {
            log.debug("User with id " + userDto.getId() + " exists");
            throw new DataValidationException("User with id " + userDto.getId() + " exists");
        }
    }
}
