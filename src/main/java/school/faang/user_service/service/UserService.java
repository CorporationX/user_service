package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserAvatarDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.jpa.UserJpaRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.dicebear.DiceBearService;
import school.faang.user_service.service.s3.S3Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final S3Service s3Service;
    private final DiceBearService diceBearService;

    @Value("${images.avatar_dicebear_format}")
    private String avatarFormat;

    @Transactional
    public void deactivate(long id) {
        log.info("Deactivate user with ID: {}", id);

        User user = findById(id);

        user.getMentees().forEach(mentee -> mentee.getMentors().remove(user));
        deleteMentorFromUsersGoals(user);
        deleteUserGoals(user);
        deleteUserEvents(user);

        user.setActive(false);
        userJpaRepository.save(user);
    }

    public User findById(long id) {
        return userRepository.findById(id);
    }

    private void deleteMentorFromUsersGoals(User user) {
        log.info("Update mentor in user's setGoals where he was mentor");
        for (Goal goal : user.getSetGoals()) {
            log.info("Remove mentor from goal with ID: {}", goal.getId());
            goal.setMentor(null);
            goalRepository.save(goal);
        }
    }

    private void deleteUserEvents(User user) {
        log.info("Delete user's events");
        user.getOwnedEvents().removeIf(event -> event.getOwner().getId() == user.getId());
        user.setParticipatedEvents(Collections.emptyList());
    }

    private void deleteUserGoals(User user) {
        log.info("Delete user's goals");
        user.getGoals().forEach(goal -> {
            if (goal.getUsers().size() == 1 && goal.getUsers().get(0).equals(user)) {
                goalRepository.deleteById(goal.getId());
            }
            goal.getUsers().remove(user);
        });
    }

    public UserAvatarDto getRandomAvatar(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId);

        byte[] image = diceBearService.getAvatar(userDto.getEmail());
        String imageKey = s3Service.uploadFile(image, "avatar_user_id_" + userId);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(imageKey);
        user.setUserProfilePic(userProfilePic);

        userJpaRepository.save(user);
        return new UserAvatarDto(imageKey, avatarFormat);
    }
}
