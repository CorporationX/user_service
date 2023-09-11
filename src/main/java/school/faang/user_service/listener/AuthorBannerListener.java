package school.faang.user_service.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@Component
public class AuthorBannerListener extends AbstractListener<Long> {

    private final UserService userService;

    @Autowired
    public AuthorBannerListener(ObjectMapper objectMapper, UserService userService) {
        super(objectMapper);
        this.userService = userService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received message in AuthorBannerListener");
        List<Long> idsForBan = deserializeListJson(message, new TypeReference<>() {});
        userService.banAuthors(idsForBan);
    }
}
