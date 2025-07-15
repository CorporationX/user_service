package school.faang.user_service.service.skill_offer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillOfferServiceTest {

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @InjectMocks
    private SkillOfferServiceImpl skillOfferService;

    private static final long SKILL_ID = 10L;
    private static final long USER_ID = 5L;
    private static final int OFFERS_COUNT = 3;

    @Test
    @DisplayName("Should return all offers for given skill and user")
    void getAllSkillOffersForGivenSkillAndUser() {
        SkillOffer offer1 = new SkillOffer();
        SkillOffer offer2 = new SkillOffer();

        List<SkillOffer> offers = List.of(offer1, offer2);

        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(offers);

        List<SkillOffer> result = skillOfferService.getAllOffersOfSkill(SKILL_ID, USER_ID);

        assertEquals(2, result.size());
        assertSame(offer1, result.get(0));
        assertSame(offer2, result.get(1));

        verify(skillOfferRepository).findAllOffersOfSkill(SKILL_ID, USER_ID);
    }

    @Test
    @DisplayName("Should return correct count of offers for given skill and user")
    void countAllOffersOfSkillShouldReturnCount() {
        when(skillOfferRepository.countAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(OFFERS_COUNT);

        int count = skillOfferService.countAllOffersOfSkill(SKILL_ID, USER_ID);

        assertEquals(OFFERS_COUNT, count);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(SKILL_ID, USER_ID);
    }
}