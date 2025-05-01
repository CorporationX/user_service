package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapperImpl;
import school.faang.user_service.mapper.recommendation.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private static final Long AUTHOR_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long SKILL_ID = 1L;
    private static final Long SKILL2_ID = 2L;
    private static final Long SKILL3_ID = 3L;
    private static final Long RECOMMENDATION_ID = 1L;
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String CONTENT = "Test content";

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;

    @Spy
    private SkillOfferMapperImpl skillOfferMapper;
    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    private User author;
    private User receiver;
    private Skill skill;
    private Skill skill2;
    private Skill skill3;
    private SkillOffer skillOffer;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;
    private RecommendationDto recommendationDto;
    private RecommendationDto oldRecommendationDto;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        Field mapperField = RecommendationMapperImpl.class.getDeclaredField("skillOfferMapper");
        mapperField.setAccessible(true);
        mapperField.set(recommendationMapper, skillOfferMapper);
        author = createUser(AUTHOR_ID);
        receiver = createUser(RECEIVER_ID);
        skill = createSkill(SKILL_ID, "Java");
        skill2 = createSkill(SKILL2_ID, "Spring");
        skill3 = createSkill(SKILL3_ID, "Hibernate");
        skillOffer = createSkillOffer(skill);
        skillOffer2 = createSkillOffer(skill2);
        skillOffer3 = createSkillOffer(skill3);
        recommendationDto = createRecommendationDto(
                null,
                null,
                CONTENT,
                author.getId(),
                receiver.getId(),
                List.of(
                        createSkillOfferDto(SKILL_ID),
                        createSkillOfferDto(SKILL2_ID),
                        createSkillOfferDto(SKILL3_ID)
                )
        );
        oldRecommendationDto = createRecommendationDto(
                1L,
                NOW,
                CONTENT,
                author.getId(),
                receiver.getId(),
                List.of(
                        createSkillOfferDto(SKILL_ID),
                        createSkillOfferDto(SKILL2_ID),
                        createSkillOfferDto(SKILL3_ID)
                )
        );
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setSkills(new ArrayList<>());
        return user;
    }

    private Skill createSkill(Long id, String title) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setTitle(title);
        return skill;
    }

    private SkillOffer createSkillOffer(Skill skill) {
        SkillOffer offer = new SkillOffer();
        offer.setSkill(skill);
        return offer;
    }

    private SkillOfferDto createSkillOfferDto(Long skillId) {
        SkillOfferDto offer = new SkillOfferDto();
        offer.setSkillId(skillId);
        return offer;
    }

    private RecommendationDto createRecommendationDto(
            Long id,
            LocalDateTime createdAt,
            String content,
            Long authorId,
            Long receiverId,
            List<SkillOfferDto> offers
    ) {
        RecommendationDto rec = new RecommendationDto();
        rec.setId(id);
        rec.setCreatedAt(createdAt);
        rec.setContent(content);
        rec.setAuthorId(authorId);
        rec.setReceiverId(receiverId);
        rec.setSkillOffers(offers);
        return rec;
    }

    private Recommendation createRecommendation(
            Long id,
            LocalDateTime createdAt,
            String content,
            List<SkillOffer> offers
    ) {
        Recommendation rec = new Recommendation();
        rec.setId(id);
        rec.setCreatedAt(createdAt);
        rec.setContent(content);
        rec.setAuthor(author);
        rec.setReceiver(receiver);
        rec.setSkillOffers(offers);
        return rec;
    }

    @Test
    void testGetAllGivenRecommendations() {
        List<Recommendation> recommendations = List.of(createRecommendation(RECOMMENDATION_ID, NOW, CONTENT,
                List.of(skillOffer, skillOffer2, skillOffer3)));
        List<RecommendationDto> dtos = List.of(oldRecommendationDto);

        when(recommendationRepository.findAllByAuthorId(AUTHOR_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendations));

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(AUTHOR_ID);
        assertEquals(dtos, result);
        verify(recommendationRepository).findAllByAuthorId(AUTHOR_ID, Pageable.unpaged());
        verify(recommendationMapper).toDto(recommendations);
    }

    @Test
    void testGetAllUserRecommendations() {
        List<Recommendation> recommendations = List.of(createRecommendation(RECOMMENDATION_ID, NOW,
                CONTENT, List.of()));
        List<RecommendationDto> dtos = List.of(recommendationDto);

        when(recommendationRepository.findAllByReceiverId(RECEIVER_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendations));
        when(recommendationMapper.toDto(recommendations)).thenReturn(dtos);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(RECEIVER_ID);
        assertEquals(dtos, result);
        verify(recommendationRepository).findAllByReceiverId(RECEIVER_ID, Pageable.unpaged());
    }

    @Test
    void testDelete() {
        recommendationService.delete(RECOMMENDATION_ID);
        verify(recommendationRepository).deleteById(RECOMMENDATION_ID);
    }

    @Test
    void testUpdateSuccess() {
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW, "Updated",
                List.of(skillOffer, skillOffer2, skillOffer3));
        Recommendation lastRecommendation = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());
        oldRecommendationDto.setContent(recommendation.getContent());

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));
        when(userRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(author));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillRepository.findById(SKILL2_ID)).thenReturn(Optional.of(skill2));
        when(skillRepository.findById(SKILL3_ID)).thenReturn(Optional.of(skill3));
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));
        when(recommendationMapper.toDto(recommendation)).thenReturn(oldRecommendationDto);

        RecommendationDto result = recommendationService.update(oldRecommendationDto);
        assertNotNull(result);
        verify(recommendationRepository).update(RECOMMENDATION_ID, RECEIVER_ID, recommendation.getContent());
        verify(skillOfferRepository).deleteAllByRecommendationId(RECOMMENDATION_ID);
        verify(skillOfferRepository).create(RECOMMENDATION_ID, SKILL_ID);

        verify(recommendationMapper).toEntity(any(RecommendationDto.class));
        verify(recommendationMapper).toDto(recommendation);
    }

    @Test
    void testUpdateThrowsExceptionIfLessThan6Months() {
        Recommendation lastRecommendation = createRecommendation(3L, NOW.minusMonths(5), CONTENT,
                List.of(skillOffer));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.update(recommendationDto));
        assertEquals("the author 1 gives a recommendation 3 earlier than 6 months after his last " +
                "recommendation to user 2", ex.getMessage());
    }

    @Test
    void testUpdateThrowsExceptionIfSkillDoesNotExist() {
        Recommendation lastRecommendation = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.update(recommendationDto));
        assertEquals("Skill 1 does not exist", ex.getMessage());
    }

    @Test
    void testCreateSuccess() {
        Recommendation recommendation = createRecommendation(
                RECOMMENDATION_ID,
                NOW,
                CONTENT,
                List.of(
                        skillOffer,
                        skillOffer2,
                        skillOffer3
                )
        );
        Recommendation lastRecommendation = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));
        when(userRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(author));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillRepository.findById(SKILL2_ID)).thenReturn(Optional.of(skill2));
        when(skillRepository.findById(SKILL3_ID)).thenReturn(Optional.of(skill3));
        when(recommendationRepository.create(AUTHOR_ID, RECEIVER_ID, CONTENT)).thenReturn(RECOMMENDATION_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));

        RecommendationDto result = recommendationService.create(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto.getAuthorId(), result.getAuthorId());
        assertEquals(recommendationDto.getReceiverId(), result.getReceiverId());
        assertEquals(recommendationDto.getContent(), result.getContent());
        assertEquals(recommendationDto.getSkillOffers(), result.getSkillOffers());

        verify(recommendationRepository).create(AUTHOR_ID, RECEIVER_ID, CONTENT);
        verify(skillOfferRepository).create(SKILL_ID, RECOMMENDATION_ID);
        verify(skillOfferRepository).create(SKILL2_ID, RECOMMENDATION_ID);
        verify(skillOfferRepository).create(SKILL3_ID, RECOMMENDATION_ID);

        verify(recommendationMapper).toEntity(any(RecommendationDto.class));
        verify(skillOfferMapper).toEntityList(any());
        verify(skillOfferMapper, times(3)).toEntity(any());
    }

    @Test
    void testCreateThrowsExceptionIfLessThan6Months() {
        Recommendation lastRecommendation =
                createRecommendation(3L, NOW.minusMonths(5), "Recent", List.of());

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("the author 1 gives a recommendation 3 earlier than " +
                "6 months after his last recommendation to user 2", ex.getMessage());
    }

    @Test
    void testCreateThrowsExceptionIfNoSkillOffers() {
        recommendationDto.setSkillOffers(Collections.emptyList());
        Recommendation lastRecommendation = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("Skill offers list is empty", ex.getMessage());
    }

    @Test
    void testCreateThrowsExceptionIfSkillDoesNotExist() {
        Recommendation lastRecommendation = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("Skill 1 does not exist", ex.getMessage());
    }
}
