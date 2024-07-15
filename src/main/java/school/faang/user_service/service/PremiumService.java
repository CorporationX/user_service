package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.*;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PremiumMapper premiumMapper;
    private final UserMapper userMapper;
    private final PremiumValidator premiumValidator;
    private final PaymentValidator paymentValidator;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        premiumValidator.validateUserAlreadyHasPremium(userId);
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(
            new BigDecimal(premiumPeriod.getCost()),
            Currency.USD
        );
        paymentValidator.validatePaymentSuccess(paymentResponse);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID: %d does not exist.", userId)));
        Premium premium = createPremiumForUser(user, premiumPeriod);
        return premiumMapper.toDto(premium);
    }

    @Transactional(readOnly = true)
    public List<UserDto> showPremiumUsersFirst() {
        List<User> regularUsers = userRepository.findAll();
        List<User> premiumUsers = userRepository.findPremiumUsers();
        ArrayList<User> combinedUsers = new ArrayList<>(premiumUsers);
        combinedUsers.retainAll(regularUsers);
        return userMapper.usersToUserDTOs(combinedUsers);
    }

    private Premium createPremiumForUser(User user, PremiumPeriod premiumPeriod) {
        LocalDateTime startDate = LocalDateTime.now();
        Premium premium = Premium.builder()
            .user(user)
            .startDate(startDate)
            .endDate(startDate.plusDays(premiumPeriod.getDays()))
            .build();
        premiumRepository.save(premium);
        return premium;
    }
}
