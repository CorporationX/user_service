package school.faang.user_service.service.s3;

import org.springframework.stereotype.Component;

@Component("s3StringHelper")
public class StringHelper {

    public String createAvatarKey(Long userId, String imageSize) {
        StringBuffer key = new StringBuffer();

        key.append("avatars/");
        key.append(userId);
        key.append("/");
        key.append(imageSize);
        key.append(".jpg");

        return String.valueOf(key);
    }

}
