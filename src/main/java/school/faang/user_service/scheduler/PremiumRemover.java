package school.faang.user_service.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumService premiumService;

    public void removePremium() {
        log.info("Start premium remove");
        premiumService.removeExpiredPremiums();
    }

}
