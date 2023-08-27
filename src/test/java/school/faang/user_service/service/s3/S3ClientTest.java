package school.faang.user_service.service.s3;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.io.IOException;

@Data
public class S3ClientTest {

    private final Environment environment;

    @Autowired
    private final S3Client s3Client;

    private ProfilePictureService profilePictureService = new ProfilePictureService(s3Client);


    @Test
    @DisplayName("Should upload a file")
    void testPictureUploading() throws IOException {

        User user = User.builder().id(1L).username("Tony").build();
        String url = profilePictureService.generatePictureUrl(user);
        byte[] pic = profilePictureService.downloadPicture(url);
        s3Client.upload(user, pic, ".svg");
    }

//    @Test
//    @DisplayName("Should return profile picture link")
//    void testGetPictureURL() {
//        S3Client s3 = new S3Client();
//        System.out.println(s3.getPictureURLById(1L));
//    }
}
