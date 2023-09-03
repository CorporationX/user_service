package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.mapper.skill.EventSkillOfferedMapperImpl;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventSkillOfferedDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillOfferedEventValidator;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SkillOfferServiceTest {
    @InjectMocks
    private SkillOfferService skillOfferService;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillOfferedEventValidator validator;
    @Spy
    private EventSkillOfferedMapperImpl eventSkillOfferedMapper;

    @Test
    void testCreateSkillOfferValid() {
        EventSkillOfferedDto eventDto = new EventSkillOfferedDto();
        eventDto.setAuthorId(1L);
        eventDto.setReceiverId(2L);
        eventDto.setSkillOfferedId(3L);

        SkillOffer entity = new SkillOffer();
        entity.setId(1L);

        when(skillOfferRepository.save(any(SkillOffer.class))).thenReturn(entity);

        EventSkillOfferedDto result = skillOfferService.createSkillOffer(eventDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testCreateSkillOffer_ValidationFailed() {
        EventSkillOfferedDto eventDto = new EventSkillOfferedDto();
        eventDto.setAuthorId(0L);

        doThrow(DataValidationException.class).when(validator).validate(eventDto);

        assertThrows(DataValidationException.class, () -> skillOfferService.createSkillOffer(eventDto));
        verifyNoInteractions(skillOfferRepository);
    }

    @Test
    void testFindAllOffersOfSkill() {
        long skillId = 1L;
        long userId = 2L;

        List<SkillOffer> offers = new ArrayList<>();
        SkillOffer offer1 = new SkillOffer();
        offer1.setId(1L);
        SkillOffer offer2 = new SkillOffer();
        offer2.setId(2L);
        offers.add(offer1);
        offers.add(offer2);

        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);

        List<SkillOffer> result = skillOfferService.findAllOffersOfSkill(skillId, userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindAllByUserId() {
        long userId = 1L;

        List<SkillOffer> offers = new ArrayList<>();
        SkillOffer offer1 = new SkillOffer();
        offer1.setId(1L);
        SkillOffer offer2 = new SkillOffer();
        offer2.setId(2L);
        offers.add(offer1);
        offers.add(offer2);

        when(skillOfferRepository.findAllByUserId(userId)).thenReturn(offers);

        List<SkillOffer> result = skillOfferService.findAllByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }
}