package school.faang.user_service.service.expiration;

import java.time.LocalDateTime;

public interface ExpirationService {
    void processExpiredItems(LocalDateTime currentDate);
}
