package school.faang.user_service.service.deeplink;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.telegram.TelegramBotProperties;

@Service
@RequiredArgsConstructor
public class DeepLinkService {
    private final TelegramBotProperties telegramBotProperties;

    public String generateTelegramDeepLink(String token) {
        return String.format("%s%s?%s=%s",
                telegramBotProperties.getBaseUrl(),
                telegramBotProperties.getName(),
                telegramBotProperties.getStartParameter(),
                token);
    }
}
