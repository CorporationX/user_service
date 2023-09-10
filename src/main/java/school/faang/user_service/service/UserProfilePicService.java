package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.redis.ProfilePicEventDto;
import school.faang.user_service.dto.userProfilePic.AvatarFromAwsDto;
import school.faang.user_service.dto.userProfilePic.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.publisher.ProfilePicPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.FileStorageService;
import school.faang.user_service.util.ImageService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ImageService imageService;
    private final ProfilePicPublisher profilePicPublisher;

    @Value("${urls.base_dev}")
    private String base_url;

    @Value("${urls.avatar_url}")
    private String avatar_url;

    @Transactional
    public UserProfilePicDto uploadWithPublishProfilePicEvent(MultipartFile file, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id " + userId + " is not found"));

        byte[] big = imageService.resizeImage(file, true);
        String bigKey = fileStorageService.uploadFile(big, file, userId, "big");

        byte[] small = imageService.resizeImage(file, false);
        String smallKey = fileStorageService.uploadFile(small, file, userId, "small");

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(bigKey);
        userProfilePic.setSmallFileId(smallKey);

        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        String link = base_url + avatar_url + userId;
        profilePicPublisher.publishMessage(new ProfilePicEventDto(userId, link));

        return new UserProfilePicDto(userId, bigKey, smallKey);
    }

    @Transactional(readOnly = true)
    public List<AvatarFromAwsDto> getByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataValidException("User with id " + userId + " is not found"));
        UserProfilePic userProfilePic = user.getUserProfilePic();

        if (userProfilePic == null) {
            throw new DataValidException("User with id " + userId + " doesn't has an avatar");
        }

        List<AvatarFromAwsDto> avatars = new ArrayList<>();
        avatars.add(fileStorageService.receiveFile(userProfilePic.getFileId()));
        avatars.add(fileStorageService.receiveFile(userProfilePic.getSmallFileId()));

        return avatars;
    }

    @Transactional
    public UserProfilePicDto deleteByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataValidException("User with id " + userId + " is not found"));
        UserProfilePic userProfilePic = user.getUserProfilePic();

        if (userProfilePic == null) {
            throw new DataValidException("User with id " + userId + " doesn't has an avatar");
        }

        fileStorageService.deleteObject(userProfilePic.getFileId());
        fileStorageService.deleteObject(userProfilePic.getSmallFileId());

        user.setUserProfilePic(null);

        return new UserProfilePicDto(userId, null, null);
    }
}
