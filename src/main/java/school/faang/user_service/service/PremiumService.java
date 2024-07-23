package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    public PremiumDto buyPremium(long userId, int days, String currencyName) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        Currency currency = Currency.getFromName(currencyName);
        premiumValidator.validateUserAlreadyHasPremium(userId);
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(createPaymentRequest(premiumPeriod.getCost(), currency));
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
        List<User> combinedUsers = combineUsers(regularUsers, premiumUsers);
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
        userRepository.save(user);
        return premium;
    }

    private PaymentRequest createPaymentRequest(double amount, Currency currency) {
        return PaymentRequest.builder()
            .amount(amount)
            .currency(currency)
            .build();
    }

    private List<User> combineUsers(List<User> regularUsers, List<User> premiumUsers) {
        Set<User> combinedUsersSet = new LinkedHashSet<>(premiumUsers);
        combinedUsersSet.addAll(regularUsers);
        return combinedUsersSet.stream().toList();
    }
}
