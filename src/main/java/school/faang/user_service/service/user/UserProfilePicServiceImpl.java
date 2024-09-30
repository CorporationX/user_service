package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.UserProfilePicService;

import java.io.IOException;

@Service
public class UserProfilePicServiceImpl implements UserProfilePicService {

    private final AmazonS3 amazonS3;

    @Autowired
    public UserProfilePicServiceImpl(@Qualifier("amazonS3") AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public void uploadAvatar(Long userId, MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentEncoding("utf-8");
        String key = String.format("%s-%s", userId, file.getOriginalFilename());
        PutObjectRequest request = new PutObjectRequest("corpbucket", key, file.getInputStream(), objectMetadata);

        amazonS3.putObject(request);
    }
}
