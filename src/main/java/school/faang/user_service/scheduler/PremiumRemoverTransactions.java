package school.faang.user_service.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PremiumRemoverTransactions {

    private final PremiumRepository premiumRepository;

    @Transactional
    public int deletePremiums(List<Long> ids) {
        premiumRepository.deleteAllById(ids);
        return ids.size();
    }
}
