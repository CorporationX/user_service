package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.AmazonS3Service;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {

    private final AmazonS3Service s3Service;

    @Transactional
    public UserProfilePic uploadProfilePic(MultipartFile file, long userId) {
        // validate user

        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId("1000");
        profilePic.setSmallFileId("500");

        s3Service.uploadProfilePic(file, "profilePic", userId);


        return profilePic;

    }
}
