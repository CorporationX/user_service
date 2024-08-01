package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;

import java.io.InputStream;

@Service
public class UserProfilePicService {

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.smallSize}")
    private int smallSize;

    @Value("${services.s3.largeSize}")
    private int largeSize;


    public UserProfilePicDto saveUserProfilePic(@RequestParam MultipartFile file) {
        return null;
    }

    public InputStream getUserProfilePic(@RequestParam Long userId) {
        return null;
    }

    public UserProfilePicDto deleteUserProfilePic(@RequestParam Long userId) {
        return null;
    }

}
