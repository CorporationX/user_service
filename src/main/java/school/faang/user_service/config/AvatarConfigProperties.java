package school.faang.user_service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class AvatarConfigProperties {

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Value("${dicebear.url}")
    private String url;

    @Value("${dicebear.avatars}")
    private  List<String> avatars;

    @Value("${dicebear.type}")
    private String type;

    @Value("${services.avatar.small-file-width}")
    private int smallFileWidth;

    @Value("${services.avatar.small-file-height}")
    private int smallFileHeight;

    @Value("${services.avatar.avatar-file-name}")
    private String avatarName;

    @Value("${services.avatar.small-avatar-file-name}")
    private String smallAvatarName;

    @Value("${services.avatar.folder}")
    private String folder;
}


