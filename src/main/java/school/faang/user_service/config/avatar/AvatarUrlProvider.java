package school.faang.user_service.config.avatar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AvatarUrlProvider {

    @Value("${avatar.provider.base-url:https://api.dicebear.com/7.x}")
    private String baseUrl;

    @Value("${avatar.provider.style:bottts}")
    private String style;

    @Value("${avatar.size.large:256}")
    private int largeSize;

    @Value("${avatar.size.small:64}")
    private int smallSize;

    @Value("${avatar.provider.version:1}")
    private int version;

    public String buildLarge(String seed) {
        return build(seed, largeSize);
    }

    public String buildSmall(String seed) {
        return build(seed, smallSize);
    }

    private String build(String seed, int size) {
        StringBuilder sb = new StringBuilder();
        return sb.append(baseUrl)
                .append('/')
                .append(style)
                .append("/png?seed=")
                .append(seed)
                .append("&size=")
                .append(size)
                .append("&v=")
                .append(version)
                .toString();
    }
}
