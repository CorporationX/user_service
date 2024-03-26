package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import school.faang.user_service.dto.payment.CurrencyRate;

@FeignClient(name = "exchange-service", url = "${services.exchange.endpoint}")
public interface ExchangeServiceClient {

    @GetMapping
    CurrencyRate getCurrencyRate();
}