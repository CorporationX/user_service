package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.PromotionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionControllerTest {

    @InjectMocks
    private PromotionController promotionController;

    @Mock
    private PromotionService promotionService;

    private long userId;
    private long eventId;
    private String promotionalPlan;
    private String currency;
    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        userId = 0;
        eventId = 0;
        promotionalPlan = "basic";
        currency = "USD";
        promotionDto = PromotionDto.builder().build();
    }

    @Test
    @DisplayName("Test promoting a user successfully")
    void testPromoteUser() {
        when(promotionService.promoteUser(anyLong(), anyString(), anyString())).thenReturn(promotionDto);

        PromotionDto result = promotionController.promoteUser(userId, promotionalPlan, currency);

        verify(promotionService).promoteUser(anyLong(), anyString(), anyString());

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }

    @Test
    @DisplayName("Test Promoting an event successfully")
    void testPromoteEvent() {
        when(promotionService.promoteEvent(anyLong(), anyString(), anyString())).thenReturn(promotionDto);

        PromotionDto result = promotionController.promoteEvent(eventId, promotionalPlan, currency);

        verify(promotionService).promoteEvent(anyLong(), anyString(), anyString());

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }
}
