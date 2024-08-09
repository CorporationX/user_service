package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.image.ImageMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;

    private final UserMapper userMapper;
    private final S3Service s3Service;
    private final ImageMapper imageMapper;

    private final static int MAX_IMAGE_PIC = 1080;
    private final static int MIN_IMAGE_PIC = 170;

    public UserDto findUserById(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public List<UserDto> findUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto deactivateUserById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        stopUserGoals(user);
        deleteUserEvents(user);
        user.setActive(false);
        mentorshipService.stopUserMentorship(user.getId());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    private void stopUserGoals(User user) {
        for (Goal goal : user.getGoals()) {
            var userList = goal.getUsers();
            if (userList != null) {
                if (userList.size() == 1 && userList.get(0).getId() == user.getId()) {
                    goalRepository.delete(goal);
                } else {
                    userList.remove(user);
                    goalRepository.save(goal);
                }
            }
        }
    }

    private void deleteUserEvents(User user) {
        eventRepository.deleteAll(user.getParticipatedEvents());
    }

    public UserProfilePicDto addUserPic(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        UserProfilePic userProfilePic = new UserProfilePic();

        userProfilePic.setFileId(s3Service.uploadFile(imageMapper.convertFilePermissions(file, MAX_IMAGE_PIC)));
        userProfilePic.setSmallFileId(s3Service.uploadFile(imageMapper.convertFilePermissions(file, MIN_IMAGE_PIC)));

        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return UserProfilePicDto.fromUserProfilePic(userProfilePic);
    }

    public InputStream getUserPic(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        if (user.getUserProfilePic() == null) {
            throw new EntityNotFoundException("User " + userId + " not found");
        }
        return s3Service.downloadFile(user.getUserProfilePic().getFileId());
    }

    public void deleteUserPic(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());

        user.getUserProfilePic().setFileId(null);
        user.getUserProfilePic().setSmallFileId(null);

        userRepository.save(user);
    }
}
