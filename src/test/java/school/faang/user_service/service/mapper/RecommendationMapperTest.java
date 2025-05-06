package school.faang.user_service.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.service.RecommendationService.ID_NULL_EXCEPTION;

@ExtendWith(MockitoExtension.class)
public class RecommendationMapperTest {
    private static final String CONTENT = "Content";
    private Recommendation recommendation;

    @Spy
    private SkillOfferMapperImpl skillOfferMapper = new SkillOfferMapperImpl();

    @Spy
    private RecommendationMapperImpl recommendationMapper = new RecommendationMapperImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationMapper, "skillOfferMapper", skillOfferMapper);

        Skill skill = Skill.builder().id(1L).build();
        SkillOffer skillOffer = SkillOffer.builder().id(1L).skill(skill).build();
        List<SkillOffer> skillOfferList = List.of(skillOffer);
        recommendation = Recommendation.builder()
                .id(1L)
                .author(User.builder().id(1L).build())
                .receiver(User.builder().id(1L).build())
                .content(CONTENT)
                .skillOffers(skillOfferList)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testToRecommendation() {
        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(1L)
                .skillId(1L)
                .build();
        List<SkillOfferDto> skillOfferDtoList = List.of(skillOfferDto);
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(1L)
                .content(CONTENT)
                .skillOffersDto(skillOfferDtoList)
                .createdAt(LocalDateTime.now())
                .build();

        Recommendation result = recommendationMapper.toRecommendation(recommendationDto);

        assertNotNull(result);
        assertEquals(recommendationDto.getId(), result.getId());
        assertEquals(recommendationDto.getAuthorId(), result.getAuthor().getId());
        assertEquals(recommendationDto.getReceiverId(), result.getReceiver().getId());
        assertEquals(recommendationDto.getContent(), result.getContent());
        assertEquals(recommendationDto.getSkillOffersDto().get(0).getId(), result.getSkillOffers().get(0).getId());
        assertEquals(recommendationDto.getSkillOffersDto().get(0).getSkillId(),
                result.getSkillOffers().get(0).getSkill().getId());
        assertEquals(recommendationDto.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    public void testToRecommendationDto() {
        RecommendationDto result = recommendationMapper.toRecommendationDto(recommendation);

        assertNotNull(result);
        assertEquals(recommendation.getId(), result.getId());
        assertEquals(recommendation.getAuthor().getId(), result.getAuthorId());
        assertEquals(recommendation.getReceiver().getId(), result.getReceiverId());
        assertEquals(recommendation.getContent(), result.getContent());
        assertEquals(recommendation.getSkillOffers().get(0).getId(), result.getSkillOffersDto().get(0).getId());
        assertEquals(recommendation.getSkillOffers().get(0).getSkill().getId(),
                result.getSkillOffersDto().get(0).getSkillId());
        assertEquals(recommendation.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    public void testToRecommendationDtoList() {
        List<Recommendation> recommendations = List.of(recommendation);

        List<RecommendationDto> resultList = recommendationMapper.toRecommendationDtoList(recommendations);
        RecommendationDto resultDto = resultList.get(0);

        assertNotNull(resultList);
        assertNotNull(resultDto);
        assertEquals(recommendation.getId(), resultDto.getId());
        assertEquals(recommendation.getAuthor().getId(), resultDto.getAuthorId());
        assertEquals(recommendation.getReceiver().getId(), resultDto.getReceiverId());
        assertEquals(recommendation.getContent(), resultDto.getContent());
        assertEquals(recommendation.getSkillOffers().get(0).getId(), resultDto.getSkillOffersDto().get(0).getId());
        assertEquals(recommendation.getSkillOffers().get(0).getSkill().getId(),
                resultDto.getSkillOffersDto().get(0).getSkillId());
        assertEquals(recommendation.getCreatedAt(), resultDto.getCreatedAt());
    }

    @Test
    public void testMapIdToUserWithIdNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationMapper.mapIdToUser(null));

        assertEquals(ID_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testMapIdToUser() {
        User expetedUser = User.builder().id(1L).build();

        User result = recommendationMapper.mapIdToUser(1L);

        assertNotNull(result);
        assertEquals(expetedUser, result);
    }
}
