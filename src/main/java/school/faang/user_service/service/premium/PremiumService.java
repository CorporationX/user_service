package school.faang.user_service.service.premium;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.integration.PaymentService;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.model.TariffPlan;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final UserContext userContext;
    private final PaymentService paymentService;
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PremiumMapper premiumMapper;

    public PremiumResponseDto buyPremium(PremiumRequestDto premiumRequestDto) {
        long userId = userContext.getUserId();
        PremiumResponseDto responseDto = new PremiumResponseDto();

        if (premiumRepository.existsByUserId(userId)) {
            responseDto.setMessage("User already has premium access");
            return responseDto;
        }

        PremiumResponseDto paymentResponse = paymentService.makePayment(premiumRequestDto.getPayment());
        TariffPlan tariffPlan = premiumRequestDto.getTariffPlan();
        Premium premium = createPremiumForUser(userId, tariffPlan);
        Premium savedPremium = premiumRepository.save(premium);

        responseDto = paymentResponse;
        fillTariffPlan(responseDto, tariffPlan, savedPremium);

        return responseDto;
    }

    private void fillTariffPlan(PremiumResponseDto responseDto,
                                TariffPlan tariffPlan,
                                Premium savedPremium) {
        PremiumDto premiumDto = premiumMapper.toDto(savedPremium);
        premiumDto.setTariffPlan(tariffPlan);
        responseDto.setTariffPlan(premiumDto);
    }

    private Premium createPremiumForUser(long userId, TariffPlan tariffPlan) {
        LocalDateTime endDate = tariffPlan.getEndDate();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));

        return Premium.builder()
            .user(user)
            .startDate(LocalDateTime.now())
            .endDate(endDate)
            .build();
    }
}
