package school.faang.user_service.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillOfferService;
import school.faang.user_service.service.skill.SkillService;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillService skillService;
    @Mock
    private SkillOfferService skillOfferService;
    SkillDto skillDto = SkillDto.builder().id(1L).title("flexibility").build();

    @Test
    void testBlankTitleIsInvalid() {
        assertThrows(DataValidationException.class,
                () -> skillController.create(SkillDto.builder().id(1L).title("   ").build()));
    }

    @Test
    void testNullTitleIsInvalid() {
        assertThrows(DataValidationException.class,
                () -> skillController.create(SkillDto.builder().id(1L).title(null).build()));
    }

    @Test
    void testTitleIsValid() {
        assertDoesNotThrow(
                () -> skillController.create(skillDto));
    }

    @Test
    void createNewSkill() {
        when(skillService.create(skillDto)).thenReturn(skillDto);
        SkillDto skillDto1 = skillController.create(skillDto);
        verify(skillService, times(1))
                .create(skillDto);
        assertEquals(skillDto, skillDto1);
    }

    @Test
    void testCallMethodGetUserSkillsFromSkillService(){
        skillController.getUserSkills(1L);
        verify(skillService, times(1))
                .getUserSkills(1L);
    }

    @Test
    void testCallMethodGetOfferedSkillsFromSkillService(){
        skillController.getOfferedSkills(1L);
        verify(skillService, times(1))
                .getOfferedSkills(1L);
    }

    @Test
    void testCallMethodAcquireSkillFromOffersFromSkillService(){
        skillController.acquireSkillFromOffers(1L,1L);
        verify(skillService, times(1))
                .acquireSkillFromOffers(1L,1L);
    }

    @Test
    void testCreateSkillOffer_ValidDto() {
        SkillOfferDto requestDto = new SkillOfferDto();
        when(skillOfferService.createSkillOffer(requestDto)).thenReturn(requestDto);

        SkillOfferDto response = skillController.createSkillOffer(requestDto, 1L, 2L);

        assertNotNull(response);
        assertEquals(requestDto, response);
        verify(skillOfferService, times(1)).createSkillOffer(requestDto);
    }

    @Test
    void getAllSkillOffersByUserId_ValidUserId_ReturnsSkillOffers() {
        long userId = 1L;
        List<SkillOffer> skillOffers = new ArrayList<>();
        when(skillOfferService.findAllByUserId(userId)).thenReturn(skillOffers);

        List<SkillOffer> response = skillController.getAllSkillOffersByUserId(userId);

        assertEquals(skillOffers, response);
    }

    @Test
    void getAllSkillOffersOfSkillForUser_ValidSkillIdAndUserId() {
        long skillId = 1L;
        long userId = 2L;
        List<SkillOffer> skillOffers = new ArrayList<>();
        when(skillOfferService.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        List<SkillOffer> response = skillController.getAllSkillOffersOfSkillForUser(skillId, userId);

        assertEquals(skillOffers, response);
    }
}