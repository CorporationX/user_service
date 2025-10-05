package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private User author;
    private User receiver;
    private Skill skill;
    private Recommendation recommendation;
    private CreateRecommendationDto createDto;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(recommendationService, "recommendationCooldownMonths", 6);

        author = User.builder()
                .id(1L)
                .username("author")
                .email("author@test.com")
                .build();

        receiver = User.builder()
                .id(2L)
                .username("receiver")
                .email("receiver@test.com")
                .skills(new ArrayList<>())
                .build();

        skill = Skill.builder()
                .id(1L)
                .title("Java")
                .build();

        recommendation = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Great developer!")
                .skillOffers(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createDto = new CreateRecommendationDto(
                2L,
                "Great developer!",
                Collections.singletonList(1L)
        );
    }

    @Test
    void createValidDataShouldCreateRecommendation() {

        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(author);
        when(userRepository.getByIdOrThrow(2L)).thenReturn(receiver);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.empty());
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillOfferRepository.save(any(SkillOffer.class))).thenReturn(new SkillOffer());
        when(recommendationMapper.toRecommendationDto(any(Recommendation.class)))
                .thenReturn(new RecommendationDto(1L, 1L, "author", 2L, "receiver", "Great developer!",
                        Collections.singletonList(1L), LocalDateTime.now(), LocalDateTime.now()));

        RecommendationDto result = recommendationService.create(createDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.authorId());
        assertEquals(2L, result.receiverId());
        assertEquals("Great developer!", result.content());

        verify(recommendationRepository).save(any(Recommendation.class));
        verify(skillOfferRepository).save(any(SkillOffer.class));
    }

    @Test
    void createSelfRecommendationShouldThrowException() {

        when(userContext.getUserId()).thenReturn(1L);
        CreateRecommendationDto selfDto = new CreateRecommendationDto(1L, "Content", Collections.emptyList());

        assertThrows(school.faang.user_service.exception.DataValidationException.class,
                () -> recommendationService.create(selfDto));
    }

    @Test
    void updateValidDataShouldUpdateRecommendation() {

        when(userContext.getUserId()).thenReturn(1L);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(recommendationMapper.toRecommendationDto(any(Recommendation.class)))
                .thenReturn(new RecommendationDto(1L, 1L, "author", 2L, "receiver", "Updated content!",
                        Collections.emptyList(), LocalDateTime.now(), LocalDateTime.now()));

        UpdateRecommendationDto updateDto = new UpdateRecommendationDto("Updated content!", Collections.emptyList());

        RecommendationDto result = recommendationService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated content!", result.content());
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void deleteValidDataShouldDeleteRecommendation() {

        when(userContext.getUserId()).thenReturn(1L);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.deleteByIdAndAuthor_id(1L, 1L)).thenReturn(1);

        recommendationService.delete(1L);

        verify(skillOfferRepository).deleteAllByRecommendationId(1L);
        verify(recommendationRepository).deleteByIdAndAuthor_id(1L, 1L);
    }

    @Test
    void getByFiltersValidFiltersShouldReturnFilteredRecommendations() {

        when(recommendationRepository.findAll()).thenReturn(Collections.singletonList(recommendation));
        when(recommendationMapper.toRecommendationDto(any(Recommendation.class)))
                .thenReturn(new RecommendationDto(1L, 1L, "author", 2L, "receiver", "Great developer!",
                        Collections.emptyList(), LocalDateTime.now(), LocalDateTime.now()));

        RecommendationFilterDto filters = new RecommendationFilterDto("Great", 1L, 2L);

        List<RecommendationDto> result = recommendationService.getByFilters(filters);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }
}
