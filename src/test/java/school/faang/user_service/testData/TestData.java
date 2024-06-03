package school.faang.user_service.testData;

import lombok.Getter;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.user.image.BufferedImagesHolder;

import java.awt.image.BufferedImage;

@Getter
public class TestData {
    private UserDto userDto;
    private User user;
    private BufferedImage bufferedImage;
    private BufferedImagesHolder bufferedImagesHolder;

    public TestData() {
        createUserDto();
        createUserEntity();
        createBufferedImage();
        createBufferedImagesHolder();
    }

    private void createBufferedImagesHolder() {
        bufferedImagesHolder = new BufferedImagesHolder(bufferedImage);
    }

    private void createBufferedImage() {
        bufferedImage = new BufferedImage(100, 100, 1);
    }

    private void createUserEntity() {
        user = new User();
        user.setId(1L);
        user.setUsername("nadir");
        user.setAboutMe("About nadir");
        user.setEmail("nadir@gmail.com");
        user.setPassword("12345678");
        user.setActive(true);
        var country = new Country();
        country.setId(1L);
        user.setCountry(country);
        user.setUserProfilePic(UserProfilePic.builder()
                .smallFileId("smallFileId")
                .fileId("fileId")
                .build());
    }

    private void createUserDto() {
        userDto = new UserDto(1L,
                "nadir",
                "12345678",
                "nadir@gmail.com",
                true,
                1L);
    }
}
