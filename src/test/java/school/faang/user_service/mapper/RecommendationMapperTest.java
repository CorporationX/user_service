package school.faang.user_service.mapper;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationMapperTest {

    private final RecommendationMapper recommendationMapper = RecommendationMapper.INSTANCE;
    String str = "2023-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    @Test
    void toDtoTest() {
        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        List<SkillOffer> skillOffers = new ArrayList<>();
        SkillOffer skillOffer = new SkillOffer();
        Skill skill = new Skill();
        skillOffer.setId(3L);
        skillOffer.setSkill(skill);
        skillOffers.add(skillOffer);

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent("anyText");

        recommendation.setCreatedAt(dateTime);
        recommendation.setSkillOffers(skillOffers);

        RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);
        assertNotNull(recommendationDto);
        assertNotNull(recommendationDto.getSkillOffers());
        assertEquals(1, recommendationDto.getSkillOffers().size());

        assertEquals(1L, recommendationDto.getAuthorId());
        assertEquals(2L, recommendationDto.getReceiverId());
        assertEquals("anyText", recommendationDto.getContent());
        assertEquals(dateTime, recommendationDto.getCreatedAt());
    }

    @Test
    void toEntityTest() {
        RecommendationDto recommendationDto = new RecommendationDto();
        List<SkillOfferDto> skillOffersDto = new ArrayList<>();
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setId(1L);
        skillOfferDto.setSkillId(2L);
        skillOffersDto.add(skillOfferDto);
        User user = new User();
        user.setId(3L);
        recommendationDto.setContent("anyText");
        recommendationDto.setCreatedAt(dateTime);
        recommendationDto.setSkillOffers(skillOffersDto);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        assertNotNull(recommendation);
        assertNotNull(recommendation.getId());
        assertNotNull(recommendation.getSkillOffers());
        assertEquals(1, recommendation.getSkillOffers().size());

        assertNull(recommendation.getAuthor());
        assertNull(recommendation.getReceiver());
        assertEquals("anyText", recommendationDto.getContent());
        assertEquals(dateTime, recommendationDto.getCreatedAt());

    }
}