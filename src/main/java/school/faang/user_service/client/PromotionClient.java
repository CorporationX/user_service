package school.faang.user_service.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.user.UserSearchRequest;

import java.util.List;

@FeignClient(
        name = "promotion-service",
        url = "${promotion-service.service.url}",
        configuration = FeignConfig.class
)
public interface PromotionClient {

    @GetMapping("/api/v1/promotions/search/users")
    List<Long> searchPromotedUsers(
            @RequestParam("requiredResCount") @Positive Integer requiredResCount,
            @RequestParam("sessionId") @NotBlank String sessionId,
            @RequestBody @Validated UserSearchRequest userSearchRequest
    );
}
