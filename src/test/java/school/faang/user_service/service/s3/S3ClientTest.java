package school.faang.user_service.service.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.io.IOException;

public class S3ClientTest {

    @Test
    @DisplayName("Should upload a file")
    void testPictureUploading() throws IOException {
        S3Client s3 = new S3Client();
        ProfilePictureService profilePictureService = new ProfilePictureService();
        ProfilePictureService service = new ProfilePictureService();
        User user = User.builder().id(1L).username("Tony").build();
        String link = profilePictureService.generatePictureUrl(user);

        byte[] picture = service.downloadPicture(link);

        s3.uploadPicture(user, picture);
    }

    @Test
    @DisplayName("Should return profile picture link")
    void testGetPictureURL() {
        S3Client s3 = new S3Client();
        System.out.println(s3.getPictureURLById(1L));
    }
}
