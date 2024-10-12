package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.premium.RedisPremiumBoughtEventPublisher;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBoughtEventService {
    private final RedisPremiumBoughtEventPublisher redisPremiumBoughtEventPublisher;
    private final List<PremiumBoughtEventDto> premiumBoughtEventDtos = new CopyOnWriteArrayList<>();

    @Async("premiumBoughtEventServicePool")
    public void addToPublish(PremiumBoughtEventDto premiumBoughtEventDto) {
        premiumBoughtEventDtos.add(premiumBoughtEventDto);
    }

    public boolean premiumBoughtEventDtosIsEmpty() {
        return premiumBoughtEventDtos.isEmpty();
    }

    public void publishAllPremiumBoughtEvents() {
        List<PremiumBoughtEventDto> premiumBoughtEventDtosCopy = new ArrayList<>();
        try {
            synchronized (premiumBoughtEventDtos) {
                log.info("Publish premium bought events, size: {}", premiumBoughtEventDtos.size());
                premiumBoughtEventDtosCopy = new ArrayList<>(premiumBoughtEventDtos);
                premiumBoughtEventDtos.clear();
            }
            redisPremiumBoughtEventPublisher.publish(premiumBoughtEventDtosCopy);
        } catch (Exception e) {
            log.error("Premium bought events publish failed:", e);
            synchronized (premiumBoughtEventDtos) {
                log.info("Save back to main list, size: {}", premiumBoughtEventDtosCopy.size());
                premiumBoughtEventDtos.addAll(premiumBoughtEventDtosCopy);
            }
        }
    }
}
