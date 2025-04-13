package school.faang.user_service.service.premium;

import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.PremiumActivatedDto;
import school.faang.user_service.dto.PremiumRequestDto;

public interface PremiumService {

    ResponseEntity<PremiumActivatedDto> getPremiumForUserId(Long userId);

    ResponseEntity<PremiumActivatedDto> subscribeToPremium(PremiumRequestDto premiumRequestDto);
}
