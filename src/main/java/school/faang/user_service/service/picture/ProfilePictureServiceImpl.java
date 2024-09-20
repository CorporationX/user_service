package school.faang.user_service.service.picture;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.AmazonS3Service;
import school.faang.user_service.service.GeneratorPictureService;
import school.faang.user_service.service.HelperAmazonS3Service;
import school.faang.user_service.service.ProfilePictureService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {

    private final AmazonS3Service s3Service;
    private final HelperAmazonS3Service helperS3Service;
    private final GeneratorPictureService generatorPictureService;

    @Override
    public UserProfilePic saveProfilePictures(UserRegistrationDto user) {
        List<byte[]> pictures = generatorPictureService.getProfilePictures(user.username());
        String pictureKey = uploadProfilePicture(pictures.get(0), user);
        String smallPictureKey = uploadProfilePicture(pictures.get(1), user);
        return new UserProfilePic(pictureKey, smallPictureKey);
    }

    private String uploadProfilePicture(byte[] picture, UserRegistrationDto user) {
        String key = helperS3Service.getKey(picture, user.username(), user.email());
        ObjectMetadata metadata = helperS3Service.getMetadata(picture);
        s3Service.uploadFile(picture, metadata, key);
        return key;
    }
}