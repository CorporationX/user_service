package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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
import school.faang.user_service.model.Payment;
import school.faang.user_service.model.TariffPlan;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    private static final long USER_ID = 1234L;

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private UserContext userContext;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PremiumMapper premiumMapper;

    @Test
    void whenUserAlreadyHasPremiumThenMessage() {
        PremiumRequestDto premiumRequestDto = new PremiumRequestDto();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(premiumRepository.existsByUserId(USER_ID)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> premiumService.buyPremium(premiumRequestDto)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("User already has premium access", exception.getReason());
    }

    @Test
    void buyPremiumSuccessfullyTest() {
        PremiumRequestDto premiumRequestDto = new PremiumRequestDto();
        premiumRequestDto.setPayment(new Payment());
        premiumRequestDto.setTariffPlan(TariffPlan.THREE_MONTHS);

        PremiumResponseDto expectedResponse = new PremiumResponseDto();
        PremiumDto premiumDto = new PremiumDto();
        premiumDto.setTariffPlan(TariffPlan.THREE_MONTHS);
        expectedResponse.setTariffPlan(premiumDto);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userService.getUserById(USER_ID)).thenReturn(new UserDto());
        when(userMapper.toEntity(new UserDto())).thenReturn(new User());
        when(premiumRepository.existsByUserId(USER_ID)).thenReturn(false);
        when(paymentService.makePayment(premiumRequestDto.getPayment())).thenReturn(expectedResponse);
        when(premiumRepository.save(any())).thenReturn(any());
        when(premiumMapper.toDto(new Premium())).thenReturn(new PremiumDto());

        PremiumResponseDto response = premiumService.buyPremium(premiumRequestDto);

        assertEquals(expectedResponse.getStatus(), response.getStatus());
        assertEquals(expectedResponse.getTariffPlan(), response.getTariffPlan());
    }
}
