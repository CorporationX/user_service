package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.premium.PremiumRepository;

@Service
@RequiredArgsConstructor
public class PremiumRemoverService {
    private final PremiumService premiumService;
    private final PremiumRepository premiumRepository;

    
}
