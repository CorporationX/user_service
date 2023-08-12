package school.faang.user_service.service.premium;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.integration.PaymentService;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.model.TariffPlan;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final UserContext userContext;
    private final PaymentService paymentService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;

    @Transactional
    public PremiumResponseDto buyPremium(PremiumRequestDto premiumRequestDto) {
        long userId = userContext.getUserId();

        if (premiumRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has premium access");
        }

        PremiumResponseDto paymentResponse = paymentService.makePayment(premiumRequestDto.getPayment());
        TariffPlan tariffPlan = premiumRequestDto.getTariffPlan();
        Premium premium = createPremiumForUser(userId, tariffPlan);
        Premium savedPremium = premiumRepository.save(premium);

        fillTariffPlan(paymentResponse, tariffPlan, savedPremium);

        return paymentResponse;
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
        UserDto userById = userService.getUserById(userId);
        User user = userMapper.toEntity(userById);

        return Premium.builder()
            .user(user)
            .startDate(LocalDateTime.now())
            .endDate(endDate)
            .build();
    }
}
