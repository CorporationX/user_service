package school.faang.user_service.mapper;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RecommendationMapperTest {

    @Spy
    private SkillOfferMapper skillOfferMapper = new SkillOfferMapperImpl();
    @Spy
    private RecommendationMapper recommendationMapper = new RecommendationMapperImpl(skillOfferMapper);

    @Test
    void testToDto() {

        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);
        recommendation.setContent("Test recommendation");

        User author = new User();
        author.setId(10L);

        User receiver = new User();
        receiver.setId(20L);

        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);

        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setId(100L);

        List<SkillOffer> skillOffers = new ArrayList<>();
        skillOffers.add(skillOffer);

        recommendation.setSkillOffers(skillOffers);

        RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);

        assertEquals(1L, recommendationDto.getId());
        assertEquals("Test recommendation", recommendationDto.getContent());
        assertEquals(10L, recommendationDto.getAuthorId());
        assertEquals(20L, recommendationDto.getReceiverId());
        assertEquals(1, recommendationDto.getSkillOffers().size());
        assertEquals(100L, recommendationDto.getSkillOffers().get(0).getId());
    }

    @Test
    void testToRecommendationDtos() {
        List<Recommendation> recommendations = new ArrayList<>();
        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        User author1 = new User();
        author1.setId(101L);
        User receiver1 = new User();
        receiver1.setId(201L);
        recommendation1.setAuthor(author1);
        recommendation1.setReceiver(receiver1);
        recommendations.add(recommendation1);

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        User author2 = new User();
        author2.setId(102L);
        User receiver2 = new User();
        receiver2.setId(202L);
        recommendation2.setAuthor(author2);
        recommendation2.setReceiver(receiver2);
        recommendations.add(recommendation2);


        List<RecommendationDto> dtos = recommendationMapper.toRecommendationDtos(recommendations);

        assertEquals(2, dtos.size());
        assertEquals(recommendations.get(0).getId(), dtos.get(0).getId());
        assertEquals(recommendations.get(0).getAuthor().getId(), dtos.get(0).getAuthorId());
        assertEquals(recommendations.get(0).getReceiver().getId(), dtos.get(0).getReceiverId());
        assertEquals(recommendations.get(1).getId(), dtos.get(1).getId());
        assertEquals(recommendations.get(1).getAuthor().getId(), dtos.get(1).getAuthorId());
        assertEquals(recommendations.get(1).getReceiver().getId(), dtos.get(1).getReceiverId());
    }
}
