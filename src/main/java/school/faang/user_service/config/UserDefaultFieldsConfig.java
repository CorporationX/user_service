package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.entity.UserProfilePic;

import java.util.UUID;

@Configuration
public class UserDefaultFieldsConfig {
    @Value("${dicebear.pic-base-url}")
    private String avatarBaseUrl;
    @Value("${dicebear.pic-base-url-small}")
    private String smallAvatarBaseUrl;

    @Bean
    public UserProfilePic generateProfilePic() {
        String seed = UUID.randomUUID().toString();

        return UserProfilePic.builder()
                .fileId(avatarBaseUrl + seed)
                .smallFileId(smallAvatarBaseUrl + seed)
                .build();
    }

    @Bean
    public String generatedPassword() {
        return UUID.randomUUID().toString();
    }
}
