package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapperImpl;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapperImpl;
import school.faang.user_service.mapper.recommendation.UserSkillGuaranteeMapper;
import school.faang.user_service.mapper.recommendation.UserSkillGuaranteeMapperImpl;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private final long RECOMMENDATION_ID = 1L;
    private final long AUTHOR_ID = 1L;
    private final long RECEIVER_ID = 3L;
    private final long SKILL_OFFER_ID = 1L;
    private final long FIRST_SKILL_ID = 1L;
    private final long SECOND_SKILL_ID = 2L;

    @Mock
    private UserService userService;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Spy
    private RecommendationMapper recommendationMapper = new RecommendationMapperImpl(new SkillOfferMapperImpl());
    @Spy
    private SkillOfferMapper skillOfferMapper =new SkillOfferMapperImpl();
    @Spy
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper = new UserSkillGuaranteeMapperImpl();
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    RecommendationService recommendationService;

    RecommendationDto recommendationDto;
    Recommendation recommendation;

    SkillOfferDto skillOfferDto;
    SkillOffer skillOffer;

    ArrayList<SkillOfferDto> skillOfferDtos = new ArrayList<>();

    @BeforeEach
    void init() {
        skillOffer = SkillOffer.builder()
                .id(SKILL_OFFER_ID)
                .skill(Skill.builder().id(FIRST_SKILL_ID).build())
                .recommendation(Recommendation.builder().id(RECOMMENDATION_ID).build())
                .build();

        skillOfferDto = SkillOfferDto.builder()
                .id(SKILL_OFFER_ID)
                .skillId(FIRST_SKILL_ID)
                .recommendationId(RECOMMENDATION_ID)
                .build();

        skillOfferDtos.add(skillOfferDto);
        recommendationDto = RecommendationDto.builder()
                .id(RECOMMENDATION_ID)
                .authorId(AUTHOR_ID)
                .receiverId(RECEIVER_ID)
                .content("TEST CONTENT")
                .skillOffers(skillOfferDtos)
                .build();

        recommendation = Recommendation.builder()
                .id(RECOMMENDATION_ID)
                .author(User.builder().id(AUTHOR_ID).build())
                .receiver(User.builder().id(RECEIVER_ID).build())
                .content("TEST CONTENT")
                .build();
    }

    @Test
    @DisplayName("Test add recommendation : Last recommendation date Limit")
    public void testAddRecommendationDateLimit() {
        String errorMessage = "The author (ID : 2) cannot give a recommendation to a user (ID : 3) because it hasn't been 6 months or more.";
        doThrow(new DataValidationException(errorMessage)).when(recommendationValidator).validateDateOfLastRecommendation(AUTHOR_ID, RECEIVER_ID);

        Exception exception = assertThrows(DataValidationException.class, () -> recommendationService.createRecommendation(recommendationDto));

        verifyNoMoreInteractions(recommendationValidator, recommendationRepository, recommendationMapper);

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test add recommendation : Not valid skills offered")
    public void testAddRecommendationNotValidSkillsOffered() {
        doNothing().when(recommendationValidator).validateDateOfLastRecommendation(AUTHOR_ID, RECEIVER_ID);

        String errorMessage = "The skill (ID : 3L) doesn't exists in the system";
        doThrow(new EntityNotFoundException(errorMessage)).when(recommendationValidator).validateSkillOffers(recommendationDto);
        Exception exception = assertThrows(EntityNotFoundException.class, () -> recommendationService.createRecommendation(recommendationDto));
        verifyNoMoreInteractions(recommendationRepository, recommendationMapper);

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test add recommendation : All ok")
    public void testAddRecommendationAllOk() {
        doNothing().when(recommendationValidator).validateDateOfLastRecommendation(AUTHOR_ID, RECEIVER_ID);
        doNothing().when(recommendationValidator).validateSkillOffers(recommendationDto);
        //recommendation = recommendationMapper.toEntity(recommendationDto);

        //skillOffer = skillOfferMapper.toEntity(skillOfferDto);

        //when(recommendationRepository.save(any())).thenReturn(recommendationTest);

        when(recommendationService.saveSkillOffers(Collections.singletonList(skillOfferDto), RECOMMENDATION_ID)).thenReturn(Collections.singletonList(skillOffer));

        RecommendationDto result = recommendationService.createRecommendation(recommendationDto);

        assertNotNull(result);
    }

    @Test
    void testCreateRecommendationFillsSkillOffers() {
        ArrayList<SkillOffer> offers = new ArrayList<>();
        offers.add(skillOffer);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(skillOfferRepository.saveAll(any())).thenReturn(offers);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.createRecommendation(recommendationDto);
        assertEquals(skillOfferDtos, result.getSkillOffers());
    }
}
