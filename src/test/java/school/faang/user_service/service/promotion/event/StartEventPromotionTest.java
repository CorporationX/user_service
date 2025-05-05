package school.faang.user_service.service.promotion.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.event.EventDto;
import school.faang.user_service.dto.promotion.event.EventPromotionDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.enums.promotion.PromotionPriority;
import school.faang.user_service.enums.promotion.event.EventPromotionType;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.EventPromotionCountRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.service.EventPromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.EventPromotionService.EVENT_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.EVENT_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.EVENT_PROMOTION_TYPE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;

@TestPropertySource(properties = "payment.api.url=http://localhost:9081/api/payment")
@ExtendWith(MockitoExtension.class)
public class StartEventPromotionTest {
    @Mock
    private EventPromotionRepository eventPromotionRepository;

    @Mock
    private EventPromotionCountRepository eventPromotionCountRepository;

    @InjectMocks
    private EventPromotionService eventPromotionService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EventRepository eventRepository;

    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final EventPromotionType eventPromotionType = EventPromotionType.TEN_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final EventDto eventDto = new EventDto(1L, 1L, "title", "description",
            startDate, endDate, "location");
    private final PaymentResponseDto successResponse = new PaymentResponseDto(PaymentStatus.SUCCESS, 1, 2L,
            BigDecimal.ONE, CurrencyDto.USD, "message");
    private final EventPromotionDto eventPromotionDto = new EventPromotionDto(startDate, endDate,
            eventPromotionType, promotionPriority);
    private final CurrencyDto currencyDto = CurrencyDto.EUR;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(eventPromotionService, "paymentApiUrl", "http://localhost:9081/api/payment");
    }

    @Test
    public void testProcessStartEventPromotion_nullEventDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processStartEventPromotion(null, eventPromotionDto, currencyDto)
        );
        assertEquals(EVENT_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartEventPromotion_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processStartEventPromotion(eventDto, new EventPromotionDto(null,
                        null, eventPromotionType, promotionPriority), currencyDto)
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartEventPromotion_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processStartEventPromotion(eventDto, new EventPromotionDto(
                        LocalDateTime.now().plusMinutes(1), LocalDateTime.now(),
                        eventPromotionType, promotionPriority), currencyDto)
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartEventPromotion_nullEventId() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processStartEventPromotion(new EventDto(1L, null, "title",
                                "description", startDate, endDate, "location"),
                        eventPromotionDto, currencyDto)
        );
        assertEquals(EVENT_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartEventPromotion_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processStartEventPromotion(eventDto, new EventPromotionDto(startDate,
                        endDate, null, promotionPriority), currencyDto)
        );
        assertEquals(EVENT_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartEventPromotion_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processStartEventPromotion(eventDto, new EventPromotionDto(startDate,
                        endDate, eventPromotionType, null), currencyDto)
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartEventPromotion_callFindCountByEventIdAndSaveCount() {
        Event mockEvent = new Event();
        mockEvent.setId(eventDto.eventId());
        when(eventPromotionCountRepository.save(any())).thenReturn(null);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(mockEvent));
        when(eventPromotionCountRepository.findCountByEventId(anyLong())).thenReturn(null);
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponseDto.class)))
                .thenReturn(successResponse);

        eventPromotionService.processStartEventPromotion(eventDto, eventPromotionDto, currencyDto);

        verify(eventPromotionCountRepository, times(2))
                .findCountByEventId(anyLong());
        verify(eventPromotionCountRepository, times(1))
                .save(any());
    }

    @Test
    public void testProcessStartEventPromotion_saveEventPromotion() {
        Event mockEvent = new Event();
        mockEvent.setId(eventDto.eventId());
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponseDto.class)))
                .thenReturn(successResponse);
        when(eventPromotionCountRepository.findCountByEventId(anyLong())).thenReturn(null);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(mockEvent));

        eventPromotionService.processStartEventPromotion(eventDto, eventPromotionDto, currencyDto);

        ArgumentCaptor<EventPromotion> captor = ArgumentCaptor.forClass(EventPromotion.class);
        verify(eventPromotionRepository, times(1)).save(captor.capture());

        EventPromotion capturedEventPromotion = captor.getValue();
        assertEquals(eventDto.eventId(), capturedEventPromotion.getEvent().getId());
        assertEquals(eventPromotionType.getUserPercentage(), capturedEventPromotion.getPercentage());
        assertEquals(startDate, capturedEventPromotion.getStartDate());
        assertEquals(endDate, capturedEventPromotion.getEndDate());
        assertEquals(promotionPriority.getFeedRank(), capturedEventPromotion.getFeedRank());
    }

    @Test
    public void testProcessStartEventPromotion_successfulPayment() {
        Event mockEvent = new Event();
        mockEvent.setId(eventDto.eventId());
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(mockEvent));
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponseDto.class)))
                .thenReturn(successResponse);

        ResponseEntity<String> response = eventPromotionService.processStartEventPromotion(eventDto,
                eventPromotionDto, currencyDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event promotion started successfully", response.getBody());
    }

    @Test
    public void testProcessStartEventPromotion_failedPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponseDto.class)))
                .thenReturn(null);

        ResponseEntity<String> response = eventPromotionService.processStartEventPromotion(eventDto,
                eventPromotionDto, currencyDto
        );

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Payment failed for event promotion", response.getBody());
    }
}
