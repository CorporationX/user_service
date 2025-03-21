package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.RecommendationService.ID_AUTHOR_NULL_EXCEPTION;
import static school.faang.user_service.service.RecommendationService.ID_NULL_EXCEPTION;
import static school.faang.user_service.service.RecommendationService.ID_RECEIVER_NULL_EXCEPTION;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.AUTHOR_ID_NULL;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.CONTENT_EMPTY_EXCEPTION;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.CONTENT_NULL_EXCEPTION;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.DATE_EXCEPTION;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.DATE_NULL_EXCEPTION;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.RECEIVER_ID_NULL;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.RECOMMENDATION_NULL_EXCEPTION;
import static school.faang.user_service.utils.validationUtils.RecommendationValidation.SKILL_OFFER_VALID_EXCEPTION;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private static String CONTENT = "Content";
    private static Long EXCEPTED_ID = 123L;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillOfferMapperImpl skillOfferMapper = new SkillOfferMapperImpl();

    @Spy
    private RecommendationMapperImpl recommendationMapper = new RecommendationMapperImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationMapper, "skillOfferMapper", skillOfferMapper);
    }

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @Captor
    private ArgumentCaptor<UserSkillGuarantee> userSkillGuaranteeCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Test
    public void testRecommendationOnNull() {
        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> recommendationService.create(null));

        assertEquals(RECOMMENDATION_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testRecommendationAuthorIdWithNull() {
        RecommendationDto recommendationDto = prepareRecommendationDto(null, 2L, 2L);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(AUTHOR_ID_NULL, exception.getMessage());
    }

    @Test
    public void testRecommendationReceiverIdWithNull() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, null, 2L);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(RECEIVER_ID_NULL, exception.getMessage());
    }

    @Test
    public void testRecommendationWithNullContent() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .content(null)
                .build();

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        assertEquals(CONTENT_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testRecommendationWithBlankContent() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .content("   ")
                .build();

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        assertEquals(CONTENT_EMPTY_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testRecommendationValidationSkills() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, 2L, 1L);

        when(skillOfferRepository.findAllSkillOffers()).
                thenReturn(List.of(SkillOffer.builder().skill(getSkill()).build()));
        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        assertEquals(SKILL_OFFER_VALID_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testValidateRecommendationDateWithNull() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, 2L, 2L);
        Skill skill = getSkill();

        when(skillOfferRepository.findAllSkillOffers())
                .thenReturn(List.of(SkillOffer.builder().skill(getSkill()).build()));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(Recommendation.builder().createdAt(null).build()));
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(DATE_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testRecommendationDateValidation() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, 2L, 2L);
        Recommendation rec = Recommendation.builder().createdAt(LocalDateTime.now().minusMonths(5)).build();
        Skill skill = getSkill();

        when(skillOfferRepository.findAllSkillOffers())
                .thenReturn(List.of(SkillOffer.builder().skill(skill).build()));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(rec));
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(DATE_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCreateRecommendation() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, 2L, 2L);
        Long exceptedId = EXCEPTED_ID;

        when(recommendationRepository.create(anyLong(), anyLong(), anyString()))
                .thenReturn(EXCEPTED_ID);
        setupRepositoryMocks(6);

        assertEquals(exceptedId, recommendationService.create(recommendationDto).getId());
    }

    @Test
    public void testCreateAndSaveSkillOfferWithSkillNull() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, 2L, 2L);

        setupRepositoryMocks(6);
        recommendationService.create(recommendationDto);

        verify(skillOfferRepository).create(longCaptor.capture(), longCaptor.capture());
    }

    @Test
    public void testSaveUserSkillGuarantee() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, 2L, 2L);
        SkillOffer skillOffer = skillOfferMapper.toSkillOffer(recommendationDto.getSkillOffersDto().get(0));

        setupRepositoryMocks(6);
        when(skillOfferRepository.findAllByUserId(anyLong())).thenReturn(List.of(skillOffer));
        recommendationService.create(recommendationDto);

        verify(userSkillGuaranteeRepository).save(userSkillGuaranteeCaptor.capture());
    }

    @Test
    public void testUpdateRecommendation() {
        RecommendationDto recommendationDto = prepareRecommendationDto(1L, 2L, 2L);

        setupRepositoryMocks(6);
        recommendationService.update(recommendationDto);

        verify(recommendationRepository).update(longCaptor.capture(), longCaptor.capture(), stringCaptor.capture());
    }

    @Test
    public void testDeleteRecommendationWithNull() {
        Long id = null;

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.delete(id));

        assertEquals(ID_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testDeleteRecommendation() {
        Long id = 1L;

        recommendationService.delete(id);

        verify(recommendationRepository).deleteById(id);
    }

    @Test
    public void testGetAllGivenRecommendationWithIdNull() {
        Long id = null;

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.getAllGivenRecommendation(id));

        assertEquals(ID_AUTHOR_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGetAllGivenRecommendation() {
        Long authorId = 1L;
        List<Recommendation> recommendations = List.of(Recommendation.builder().id(1L).build());
        Pageable pageable = Pageable.unpaged();
        Page<Recommendation> page = new PageImpl<>(recommendations);
        List<RecommendationDto> expectedDtos = List.of(RecommendationDto.builder().id(1L).build());

        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(page);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendation(authorId);

        assertEquals(expectedDtos, result);
        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
    }

    @Test
    public void testGetAllUserRecommendationsWithIdNull() {
        Long id = null;

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.getAllUserRecommendations(id));

        assertEquals(ID_RECEIVER_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGetAllUserRecommendations() {
        Long authorId = 1L;
        List<Recommendation> recommendations = List.of(Recommendation.builder().id(1L).build());
        Pageable pageable = Pageable.unpaged();
        Page<Recommendation> page = new PageImpl<>(recommendations);
        List<RecommendationDto> expectedDtos = List.of(RecommendationDto.builder().id(1L).build());

        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(page);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(authorId);

        assertEquals(expectedDtos, result);
        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
    }

    private RecommendationDto prepareRecommendationDto(Long authorId, Long receiverId, Long skillId) {
        return RecommendationDto.builder()
                .content(CONTENT)
                .authorId(authorId)
                .receiverId(receiverId)
                .skillOffersDto(List.of(SkillOfferDto.builder().skillId(skillId).build()))
                .build();
    }

    private void setupRepositoryMocks(Integer month) {
        Recommendation rec = Recommendation.builder().createdAt(LocalDateTime.now().minusMonths(month)).build();
        Skill skill = getSkill();

        when(skillOfferRepository.findAllSkillOffers())
                .thenReturn(List.of(SkillOffer.builder().skill(skill).build()));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(rec));
    }

    private Skill getSkill() {
        return Skill.builder().id(2L).build();
    }
}
