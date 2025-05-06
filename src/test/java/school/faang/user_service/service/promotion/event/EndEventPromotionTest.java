package school.faang.user_service.service.promotion.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.promotion.event.EventDto;
import school.faang.user_service.dto.promotion.event.EventPromotionDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.enums.promotion.PromotionPriority;
import school.faang.user_service.enums.promotion.event.EventPromotionType;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.service.EventPromotionService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.EventPromotionService.EVENT_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.EVENT_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.EVENT_PROMOTION_TYPE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;

@ExtendWith(MockitoExtension.class)
public class EndEventPromotionTest {
    @Mock
    private EventPromotionRepository eventPromotionRepository;

    @InjectMocks
    private EventPromotionService eventPromotionService;

    @Mock
    private RestTemplate restTemplate;

    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final EventPromotionType eventPromotionType = EventPromotionType.TEN_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final EventDto eventDto = new EventDto(1L, 1L, "title", "description",
            startDate, endDate, "location");
    private final EventPromotion eventPromotion = new EventPromotion(1L, startDate, endDate, Event.builder()
            .id(eventDto.eventId()).build(), eventPromotionType.getUserPercentage(), promotionPriority.getFeedRank());
    private final EventPromotionDto eventPromotionDto = new EventPromotionDto(startDate, endDate,
            eventPromotionType, promotionPriority);

    @Test
    public void testProcessEndEventPromotion_nullEventDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processEndEventPromotion(null, eventPromotionDto)
        );
        assertEquals(EVENT_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndEventPromotion_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processEndEventPromotion(eventDto, new EventPromotionDto(null,
                        null, eventPromotionType, promotionPriority))
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndEventPromotion_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processEndEventPromotion(eventDto, new EventPromotionDto(
                        LocalDateTime.now().plusMinutes(1), LocalDateTime.now(), eventPromotionType, promotionPriority))
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndEventPromotion_nullEventId() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processEndEventPromotion(new EventDto(1L, null, "title",
                        "description", startDate, endDate, "location"), eventPromotionDto)
        );
        assertEquals(EVENT_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndEventPromotion_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processEndEventPromotion(eventDto, new EventPromotionDto(startDate,
                        endDate, null, promotionPriority))
        );
        assertEquals(EVENT_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndEventPromotion_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processEndEventPromotion(eventDto, new EventPromotionDto(startDate,
                        endDate, eventPromotionType, null))
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessEndEventPromotion_promotionNotFound() {
        when(eventPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(null);

        eventPromotionService.processEndEventPromotion(eventDto, eventPromotionDto);

        verify(eventPromotionRepository, times(1))
                .findSamePromotion(eventDto.eventId(), startDate, endDate,
                        eventPromotionType.getUserPercentage(), promotionPriority.getFeedRank());
    }

    @Test
    public void testProcessEndEventPromotion_callDelete() {
        when(eventPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(eventPromotion);

        eventPromotionService.processEndEventPromotion(eventDto, eventPromotionDto);

        ArgumentCaptor<EventPromotion> captor = ArgumentCaptor.forClass(EventPromotion.class);
        verify(eventPromotionRepository).delete(captor.capture());

        EventPromotion capturedEventPromotion = captor.getValue();
        assertEquals(eventDto.eventId(), capturedEventPromotion.getEvent().getId());
        assertEquals(eventPromotionType.getUserPercentage(), capturedEventPromotion.getPercentage());
        assertEquals(startDate, capturedEventPromotion.getStartDate());
        assertEquals(endDate, capturedEventPromotion.getEndDate());
        assertEquals(promotionPriority.getFeedRank(), capturedEventPromotion.getFeedRank());
    }

    @Test
    public void testProcessEndEventPromotion_successful() {
        when(eventPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(eventPromotion);

        ResponseEntity<String> response = eventPromotionService.processEndEventPromotion(eventDto,
                eventPromotionDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event promotion ended successfully", response.getBody());
    }
}
