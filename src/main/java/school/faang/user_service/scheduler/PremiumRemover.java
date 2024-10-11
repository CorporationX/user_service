package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumRemover {

    private final PremiumService premiumService;

    @Scheduled(cron = "${premium.removal.interval}")
    public void removePremium() {
        List<List<Long>> expiredPremiumsIds = premiumService.findAndSplitExpiredPremiums();
        String currentTime;

        if (!expiredPremiumsIds.isEmpty()) {
            List<CompletableFuture<Integer>> futures = expiredPremiumsIds.stream()
                    .map(premiumService::deleteExpiredPremiumsByIds)
                    .toList();

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            int totalDeletedRecords = allFutures.thenApply(entry -> futures.stream()
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .reduce(0, Integer::sum))
                    .join();

            currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("A total of {} expired premiums were deleted at {}", totalDeletedRecords, currentTime);
        } else {
            currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("No expired premiums to delete at {}", currentTime);
        }
    }
}
