package school.faang.user_service.service.promotion.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.user.UserDto;
import school.faang.user_service.dto.promotion.user.UserPromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.user.UserPromotion;
import school.faang.user_service.enums.promotion.PromotionPriority;
import school.faang.user_service.enums.promotion.user.UserPromotionType;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.UserPromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.UserPromotionService.USER_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.USER_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.USER_PROMOTION_TYPE_CANNOT_BE_NULL;

@ExtendWith(MockitoExtension.class)
public class EndUserPromotionTest {
    @Mock
    private UserPromotionRepository userPromotionRepository;

    @InjectMocks
    private UserPromotionService userPromotionService;

    private final Long id = 1L;
    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final UserPromotionType userPromotionType = UserPromotionType.TEN_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final UserDto userDto = new UserDto(1L, 1L, "name", "city");
    private final PaymentResponseDto successResponse = new PaymentResponseDto(PaymentStatus.SUCCESS, 1, 2L,
            BigDecimal.ONE, CurrencyDto.USD, "message");
    private final UserPromotionDto userPromotionDto = new UserPromotionDto(startDate, endDate,
            userPromotionType, promotionPriority);

    @Test
    public void testProcessEndUserPromotion_nullUserDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processEndUserPromotion(null, new UserPromotionDto(startDate,
                        endDate, userPromotionType, promotionPriority))
        );
        assertEquals(USER_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndUserPromotion_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processEndUserPromotion(userDto, new UserPromotionDto(null,
                        null, userPromotionType, promotionPriority))
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndUserPromotion_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processEndUserPromotion(userDto, new UserPromotionDto(
                        LocalDateTime.now().plusMinutes(1), LocalDateTime.now(), userPromotionType, promotionPriority))
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndUserPromotion_nullUserId() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processEndUserPromotion(new UserDto(1L, null, "name",
                        "city"), userPromotionDto)
        );
        assertEquals(USER_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndUserPromotion_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processEndUserPromotion(userDto, new UserPromotionDto(startDate,
                        endDate, null, promotionPriority))
        );
        assertEquals(USER_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndUserPromotion_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processEndUserPromotion(userDto, new UserPromotionDto(startDate,
                        endDate, userPromotionType, null))
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndUserPromotion_promotionNotFound() {
        when(userPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(null);

        userPromotionService.processEndUserPromotion(userDto, userPromotionDto);

        verify(userPromotionRepository, times(1))
                .findSamePromotion(userDto.userId(), startDate, endDate,
                        userPromotionType.getUserPercentage(), promotionPriority.getFeedRank());
    }

    @Test
    public void testProcessEndUserPromotion_callDelete() {
        when(userPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new UserPromotion(id, startDate, endDate, User.builder().id(userDto.userId()).build(),
                        userPromotionType.getUserPercentage(), promotionPriority.getFeedRank()));

        userPromotionService.processEndUserPromotion(userDto, userPromotionDto);

        ArgumentCaptor<UserPromotion> captor = ArgumentCaptor.forClass(UserPromotion.class);
        verify(userPromotionRepository).delete(captor.capture());

        UserPromotion capturedUserPromotion = captor.getValue();
        assertEquals(userDto.userId(), capturedUserPromotion.getUser().getId());
        assertEquals(userPromotionType.getUserPercentage(), capturedUserPromotion.getPercentage());
        assertEquals(startDate, capturedUserPromotion.getStartDate());
        assertEquals(endDate, capturedUserPromotion.getEndDate());
        assertEquals(promotionPriority.getFeedRank(), capturedUserPromotion.getFeedRank());
    }

    @Test
    public void testProcessEndUserPromotion_successful() {
        when(userPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new UserPromotion(id, startDate, endDate, new User(),
                        userPromotionType.getUserPercentage(), promotionPriority.getFeedRank()));

        ResponseEntity<String> response = userPromotionService.processEndUserPromotion(userDto,
                userPromotionDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User promotion ended successfully", response.getBody());
    }
}
