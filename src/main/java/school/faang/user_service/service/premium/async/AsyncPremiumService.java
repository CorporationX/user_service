package school.faang.user_service.service.premium.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.premium.PremiumService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncPremiumService {
    private final PremiumService premiumService;

    @Async("premiumServicePool")
    public void deleteAllPremiumsByIdAsync(List<Premium> premiums) {
        log.info("Delete all premiums async");
        premiumService.deleteAllPremiumsById(premiums);
    }
}
