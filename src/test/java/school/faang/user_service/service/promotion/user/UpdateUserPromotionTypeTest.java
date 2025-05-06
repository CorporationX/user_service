package school.faang.user_service.service.promotion.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.user.UserDto;
import school.faang.user_service.dto.promotion.user.UserPromotionDto;
import school.faang.user_service.enums.promotion.PromotionPriority;
import school.faang.user_service.enums.promotion.user.UserPromotionType;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.promotion.UserPromotionCountRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.UserPromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.UserPromotionService.CANT_UPDATE_USER_PROMOTION_TYPE;
import static school.faang.user_service.service.UserPromotionService.USER_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.USER_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.USER_PROMOTION_TYPE_CANNOT_BE_NULL;

@ExtendWith(MockitoExtension.class)
public class UpdateUserPromotionTypeTest {
    @Mock
    private UserPromotionRepository userPromotionRepository;

    @InjectMocks
    private UserPromotionService userPromotionService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserPromotionCountRepository userPromotionCountRepository;

    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final UserPromotionType userPromotionType = UserPromotionType.FORTY_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final UserDto userDto = new UserDto(1L, 1L, "name", "city");
    private final PaymentResponseDto successResponse = new PaymentResponseDto(PaymentStatus.SUCCESS, 1,
            2L, BigDecimal.ONE, CurrencyDto.USD, "message");
    private final UserPromotionDto userPromotionDto = new UserPromotionDto(startDate, endDate,
            userPromotionType, promotionPriority);
    private final CurrencyDto currencyDto = CurrencyDto.EUR;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(userPromotionService, "paymentApiUrl", "http://localhost:9081/api/payment");
    }

    @Test
    public void testProcessUpdateUserPromotionType_nullUserDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionType(null, userPromotionDto, currencyDto)
        );
        assertEquals(USER_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionType_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionType(userDto, new UserPromotionDto(null,
                        null, userPromotionType, promotionPriority), currencyDto)
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionType_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionType(userDto, new UserPromotionDto(
                        LocalDateTime.now().plusMinutes(1), LocalDateTime.now(),
                        userPromotionType, promotionPriority), currencyDto)
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionType_nullUserId() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionType(new UserDto(1L, null,
                        "name", "city"), new UserPromotionDto(startDate, endDate,
                        userPromotionType, promotionPriority), currencyDto)
        );
        assertEquals(USER_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionType_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionType(userDto, new UserPromotionDto(startDate,
                        endDate, null, promotionPriority), currencyDto)
        );
        assertEquals(USER_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionType_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionType(userDto, new UserPromotionDto(startDate,
                        endDate, userPromotionType, null), currencyDto)
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionType_noPromotion() {
        when(userPromotionRepository.getUserPercentage(anyLong(), any(), any(), anyInt()))
                .thenReturn(null);

        PromotionNotFoundException promotionNotFoundException = assertThrows(PromotionNotFoundException.class, () ->
                userPromotionService.processUpdateUserPromotionType(userDto, userPromotionDto, currencyDto)
        );

        assertEquals(String.format(CANT_UPDATE_USER_PROMOTION_TYPE + ". No such promotion exists",
                userDto.userId(), startDate, endDate, promotionPriority), promotionNotFoundException.getMessage());
        verify(userPromotionRepository, times(1)).getUserPercentage(userDto.userId(),
                startDate, endDate, promotionPriority.getFeedRank());
    }

    @Test
    public void testProcessUpdateUserPromotionType_noChanges() {
        when(userPromotionRepository.getUserPercentage(anyLong(), any(), any(), anyInt()))
                .thenReturn(40);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionType(userDto, userPromotionDto, currencyDto)
        );

        assertEquals(String.format(CANT_UPDATE_USER_PROMOTION_TYPE + ". Such promotion already exists",
                userDto.userId(), startDate, endDate, promotionPriority), illegalArgumentException.getMessage());
        verify(userPromotionRepository).getUserPercentage(userDto.userId(), startDate, endDate,
                promotionPriority.getFeedRank());
    }

    @Test
    public void testProcessUpdateUserPromotionType_successfulPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponseDto.class)))
                .thenReturn(successResponse);

        when(userPromotionRepository.getUserPercentage(anyLong(), any(), any(), anyInt()))
                .thenReturn(25);
        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionType(userDto,
                userPromotionDto, currencyDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User promotion type updated successfully", response.getBody());
    }

    @Test
    public void testProcessUpdateUserPromotionType_failedPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponseDto.class)))
                .thenReturn(null);

        lenient().when(userPromotionRepository.getUserPercentage(anyLong(), any(), any(), anyInt()))
                .thenReturn(25);
        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionType(userDto,
                userPromotionDto, currencyDto
        );

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Payment failed for promotion type update", response.getBody());
    }
}
