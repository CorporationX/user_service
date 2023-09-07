package school.faang.user_service.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.SkillOfferService;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.EventSkillOfferedDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSkillOfferedControllerTest {
    @InjectMocks
    private EventSkillOfferedController eventSkillOfferedController;
    @Mock
    private SkillOfferService skillOfferService;

    @Test
    void createSkillOffer_ValidInput_ReturnsSkillOfferedDto() {
        EventSkillOfferedDto requestDto = new EventSkillOfferedDto();
        when(skillOfferService.createSkillOffer(requestDto)).thenReturn(requestDto);

        EventSkillOfferedDto response = eventSkillOfferedController.createSkillOffer(requestDto, 1L, 2L);

        assertEquals(requestDto, response);
    }

    @Test
    void getAllSkillOffersByUserId_ValidUserId_ReturnsSkillOffers() {
        long userId = 1L;
        List<SkillOffer> skillOffers = new ArrayList<>();
        when(skillOfferService.findAllByUserId(userId)).thenReturn(skillOffers);

        List<SkillOffer> response = eventSkillOfferedController.getAllSkillOffersByUserId(userId);

        assertEquals(skillOffers, response);
    }

    @Test
    void getAllSkillOffersOfSkillForUser_ValidSkillIdAndUserId_ReturnsSkillOffers() {
        long skillId = 1L;
        long userId = 2L;
        List<SkillOffer> skillOffers = new ArrayList<>();
        when(skillOfferService.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        List<SkillOffer> response = eventSkillOfferedController.getAllSkillOffersOfSkillForUser(skillId, userId);

        assertEquals(skillOffers, response);
    }
}