package school.faang.user_service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Data
@Component
public class AvatarConfig {

    @Value("${services.s3.bucket-name}")
    private String BUCKET_NAME;

    @Value("${services.avatar.pattern}")
    private String AVATAR_ID_PATTERN;

    @Value("${services.avatar.small-pattern}")
    private String SMALL_AVATAR_ID_PATTERN;

    @Value("${dice-bear.url}/${dice-bear.version}/%s/${dice-bear.file-type}?${dice-bear.params}")
    private String GENERATION_URL_PATTERN;

    @Value("${dice-bear.file-type}")
    private String EXTENSION;

    @Value("${services.avatar.small-file-width}")
    private int SMALL_FILE_WIDTH;

    @Value("${services.avatar.small-file-height}")
    private int SMALL_FILE_HEIGHT;

    @Value("${services.avatar.seed-range}")
    private int SEED_RANGE;

    @Value("${dice-bear.styles}")
    private String[] STYLES;

    @Value("${services.avatar.content-type}")
    private String CONTENT_TYPE;
}
