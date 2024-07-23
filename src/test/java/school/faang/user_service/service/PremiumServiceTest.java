package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.IllegalEntityException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumMapper premiumMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    private long userId;
    private int validDays;
    private int invalidDays;
    private String validCurrencyName;
    private String invalidCurrencyName;
    private User user;
    private PremiumDto premiumDto;
    private PaymentResponse paymentResponse;
    private List<User> regularUsers;
    private List<User> premiumUsers;
    private List<User> combinedUsers;
    private List<UserDto> usersDto;

    @BeforeEach
    void setUp() {
        userId = 0;
        validDays = 30;
        invalidDays = 0;
        validCurrencyName = "usd";
        invalidCurrencyName = "";
        user = new User();
        premiumDto = new PremiumDto(
            null,
            userId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        paymentResponse = new PaymentResponse(
            PaymentStatus.SUCCESS,
            0,
            0,
           0,
            Currency.USD,
            "Success payment"
        );
        User firstUser = User.builder().id(1).build();
        User secondUser = User.builder().id(2).build();
        User thirdUser = User.builder().id(3).build();
        regularUsers = List.of(
            firstUser,
            secondUser,
            thirdUser
        );
        premiumUsers = List.of(
            secondUser,
            thirdUser
        );
        combinedUsers = List.of(
            secondUser,
            thirdUser,
            firstUser
        );
        UserDto firstUserDto = UserDto.builder().id(1L).build();
        UserDto secondUserDto = UserDto.builder().id(2L).build();
        UserDto thirdUserDto = UserDto.builder().id(3L).build();
        usersDto = List.of(
            secondUserDto,
            thirdUserDto,
            firstUserDto
        );
    }

    @Test
    @DisplayName("Buy Premium Successfully")
    void testBuyPremium() {
        when(paymentServiceClient.sendPaymentRequest(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(premiumMapper.toDto(any(Premium.class))).thenReturn(premiumDto);

        PremiumDto result = premiumService.buyPremium(userId, validDays, validCurrencyName);

        verify(premiumValidator).validateUserAlreadyHasPremium(anyLong());
        verify(paymentServiceClient).sendPaymentRequest(any(PaymentRequest.class));
        verify(paymentValidator).validatePaymentSuccess(any(PaymentResponse.class));
        verify(userRepository).findById(anyLong());
        verify(premiumRepository).save(any(Premium.class));
        verify(userRepository).save(any(User.class));

        assertNotNull(result);
        assertEquals(premiumDto, result);
    }

    @Test
    void testBuyPremiumUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> premiumService.buyPremium(userId, validDays, validCurrencyName));
    }

    @Test
    void testBuyPremiumInvalidDays() {
        assertThrows(IllegalEntityException.class, () -> premiumService.buyPremium(userId, invalidDays, validCurrencyName));
    }

    @Test
    void testBuyPremiumInvalidCurrency() {
        assertThrows(IllegalEntityException.class, () -> premiumService.buyPremium(userId, validDays, invalidCurrencyName));
    }

    @Test
    void testShowPremiumFirst() {
        when(userRepository.findAll()).thenReturn(regularUsers);
        when(userRepository.findPremiumUsers()).thenReturn(premiumUsers);
        when(userMapper.usersToUserDTOs(anyList())).thenReturn(usersDto);

        List<UserDto> result = premiumService.showPremiumUsersFirst();

        verify(userRepository).findAll();
        verify(userRepository).findPremiumUsers();
        verify(userMapper).usersToUserDTOs(combinedUsers);

        assertNotNull(result);
        assertEquals(usersDto, result);
    }
}