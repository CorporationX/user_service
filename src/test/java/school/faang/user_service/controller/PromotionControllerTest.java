package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.PromotionDto;
import school.faang.user_service.dto.PromotionalPlan;
import school.faang.user_service.entity.promotion.AudienceReach;
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
    private int validImpressions;
    private int invalidImpressions;
    private int invalidImpressionsCombination;
    private String validAudienceReach;
    private String invalidAudienceReach;
    private String invalidAudienceReachCombination;
    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        eventId = 1L;
        validImpressions = 10_000;
        invalidImpressions = 0;
        invalidImpressionsCombination = 100_000;
        validAudienceReach = "local";
        invalidAudienceReach = "";
        invalidAudienceReachCombination = "local";
        promotionDto = new PromotionDto(null, userId, eventId, 0, AudienceReach.LOCAL);
    }

    @Test
    @DisplayName("Test promoting a user with valid parameters")
    void testPromoteUser() {
        when(promotionService.promoteUser(userId, PromotionalPlan.getPromotionalPlan(validImpressions, AudienceReach.LOCAL))).thenReturn(promotionDto);

        PromotionDto result = promotionController.promoteUser(userId, validImpressions, validAudienceReach);

        verify(promotionService).promoteUser(userId, PromotionalPlan.getPromotionalPlan(validImpressions, AudienceReach.LOCAL));

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }

    @Test
    @DisplayName("Test promoting a user with invalid impressions")
    void testPromoteUserInvalidInvalidImpressions() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteUser(userId, invalidImpressions, validAudienceReach));
    }

    @Test
    @DisplayName("Test promoting a user with invalid audience reach")
    void testPromoteUserInvalidAudienceReach() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteUser(userId, validImpressions, invalidAudienceReach));
    }

    @Test
    @DisplayName("Test promoting a user with invalid combination of impressions and audience reach")
    void testPromoteUserInvalidCombination() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteUser(userId, invalidImpressionsCombination, invalidAudienceReachCombination));
    }

    @Test
    @DisplayName("Test promoting an event with valid parameters")
    void testPromoteEvent() {
        when(promotionService.promoteEvent(eventId, PromotionalPlan.getPromotionalPlan(validImpressions, AudienceReach.LOCAL))).thenReturn(promotionDto);

        PromotionDto result = promotionController.promoteEvent(eventId, validImpressions, validAudienceReach);

        verify(promotionService).promoteEvent(eventId, PromotionalPlan.getPromotionalPlan(validImpressions, AudienceReach.LOCAL));

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }

    @Test
    @DisplayName("Test promoting an event with invalid impressions")
    void testPromoteEventInvalidInvalidImpressions() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteEvent(eventId, invalidImpressions, validAudienceReach));
    }

    @Test
    @DisplayName("Test promoting an event with invalid audience reach")
    void testPromoteEventInvalidAudienceReach() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteEvent(eventId, validImpressions, invalidAudienceReach));
    }

    @Test
    @DisplayName("Test promoting an event with invalid combination of impressions and audience reach")
    void testPromoteEventInvalidCombination() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteEvent(eventId, invalidImpressionsCombination, invalidAudienceReachCombination));
    }
}
