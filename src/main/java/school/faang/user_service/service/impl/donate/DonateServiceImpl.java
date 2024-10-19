package school.faang.user_service.service.impl.donate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.event.FundRaisedEvent;
import school.faang.user_service.publisher.FundRaisedEventPublisher;
import school.faang.user_service.service.DonateService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Viktor
 * Класс заглушка, задача ранее не реализовывалась!
 */
@Service
@RequiredArgsConstructor
public class DonateServiceImpl implements DonateService {
    private final FundRaisedEventPublisher fundRaisedEventPublisher;

    @Override
    public void donate() {
        var fundRaisedEvent = FundRaisedEvent.builder()
                .userId(1)
                .projectId(2)
                .amount(BigDecimal.valueOf(243543634.50))
                .donatedAt(LocalDateTime.now())
                .build();

        fundRaisedEventPublisher.publish(fundRaisedEvent);
    }
}
