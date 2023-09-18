package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.mymappers.User1Mapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PaymentServiceClient paymentService;
    private final UserService userService;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final User1Mapper userMapper;

    public PremiumDto buyPremium(long userId, int days) {
        var user = userService.getUserEntity(userId);
        if (premiumRepository.existsByUserId(userId)) {
            throw new PremiumAlreadyExistsException("User already has premium access");
        }
        var period = PremiumPeriod.fromDays(days);
        var price = period.getPrice();

        PaymentRequest paymentRequest = createPaymentRequest(price);
        PaymentResponse paymentResponse = paymentService.sendPayment(paymentRequest).getBody();
        Premium premium = new Premium(-1, user, LocalDateTime.now(), LocalDateTime.now().plusDays(period.getDays()));
        premiumRepository.save(premium);
        user.setPremium(premium);
        return buildPremiumDto(premium, user);
    }

    private PaymentRequest createPaymentRequest(double price) {
        long paymentNumber = new Random().nextInt(900000) + 100000;
        return new PaymentRequest(paymentNumber, BigDecimal.valueOf(price), Currency.USD);
    }

    private PremiumDto buildPremiumDto(Premium premium, User user) {
        PremiumDto dto = premiumMapper.toDto(premium);
        dto.setUserDto(userMapper.toDto(user));
        return dto;
    }
}
