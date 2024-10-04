package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;

    public void removePremium() {
        System.out.println("JOB: Remove premium");
    }
}
