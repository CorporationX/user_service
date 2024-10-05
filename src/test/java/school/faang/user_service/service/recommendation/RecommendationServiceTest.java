package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationServiceImpl recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private RecommendationMapper recommendationMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testCreateWithBlankContent() {
        RecommendationDto dto = prepareData(" ");

        Throwable exception = assertThrows(DataValidationException.class, () -> recommendationService.create(dto));
        assertEquals("Recommendation text cannot be empty.", exception.getMessage());
    }

    @Test
    public void testCreateWithNullContent() {
        RecommendationDto dto = prepareData("");

        Throwable exception = assertThrows(DataValidationException.class, () -> recommendationService.create(dto));
        assertEquals("Recommendation text cannot be empty.", exception.getMessage());
    }

    @Test
    public void testCreateWithInSixMonths() {
        RecommendationDto dto = new RecommendationDto();
        LocalDateTime lastDate = LocalDateTime.now().minusMonths(5);
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);
        dto.setContent("content");
        dto.setCreatedAt(lastDate);
        dto.setSkillOffers(List.of(new SkillOfferDto(1L, 2L), new SkillOfferDto(2L, 3L)));
        List<Long> skillIds = skillByIds(dto);
        when(skillRepository.findAllById(skillIds)).thenReturn(List.of(new Skill(), new Skill()));
        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(lastDate);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(recommendation));

        Throwable exception = assertThrows(DataValidationException.class, () -> recommendationService.create(dto));
        assertEquals("It is impossible to make a recommendation before 6 months have passed", exception.getMessage());
    }

    @Test
    public void testCreateSkillInSystem() {
        RecommendationDto dto = prepareData("content");
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);
        dto.setSkillOffers(List.of(new SkillOfferDto(1L, 2L), new SkillOfferDto(2L, 3L)));
        List<Long> skillIds = skillByIds(dto);
        when(skillRepository.findAllById(skillIds)).thenReturn(List.of(new Skill()));

        Throwable exception = assertThrows(DataValidationException.class, () -> recommendationService.create(dto));
        assertEquals("You offer skills that don't exist in the system.", exception.getMessage());
    }

    @Test
    public void testCreateSavesRecommendation() {
        RecommendationDto dto = prepareData("content");
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);
        dto.setSkillOffers(List.of(new SkillOfferDto(1L, 2L), new SkillOfferDto(3L, 4L)));
        when(skillRepository.findAllById(skillByIds(dto))).thenReturn(List.of(new Skill(), new Skill()));

        RecommendationDto result = recommendationService.create(dto);

        verify(recommendationRepository, times(1))
                .create(dto.getAuthorId(), dto.getReceiverId(), dto.getContent());

        assertEquals(result.getContent(), dto.getContent());
    }

    @Test
    public void testUpdateRecommendation() {
        RecommendationDto dto = prepareData("Content");
        dto.setId(1L);
        dto.setAuthorId(2L);
        dto.setReceiverId(1L);
        dto.setSkillOffers(List.of(new SkillOfferDto(6L, 2L), new SkillOfferDto(7L, 1L)));

        when(skillRepository.findAllById(skillByIds(dto))).thenReturn(List.of(new Skill(), new Skill()));

        recommendationService.update(dto);

        verify(recommendationRepository, times(1))
                .update(dto.getAuthorId(), dto.getReceiverId(), dto.getContent());

        verify(skillOfferRepository, times(1))
                .deleteAllByRecommendationId(dto.getId());

        List<Long> skillIds = skillByIds(dto);
        for (Long skillId : skillIds) {
            verify(skillOfferRepository).create(skillId, dto.getId());
        }
    }

    @Test
    public void testGetAllUserRecommendationsRecommendation() {
        long receiverId = 1L;
        Recommendation recommendation = new Recommendation();

        when(recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(recommendation)));

        recommendationService.getAllUserRecommendations(receiverId);

        verify(recommendationRepository).findAllByReceiverId(receiverId, Pageable.unpaged());
        verify(recommendationMapper).toDto(recommendation);
    }

    @Test
    public void testGetAllGivenRecommendationsRecommendation() {
        long authorId = 1L;
        Recommendation recommendation = new Recommendation();

        when(recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(recommendation)));

        recommendationService.getAllGivenRecommendations(authorId);

        verify(recommendationRepository).findAllByAuthorId(authorId, Pageable.unpaged());
        verify(recommendationMapper).toDto(recommendation);
    }

    private List<Long> skillByIds(RecommendationDto dto) {
        return dto.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
    }

    private RecommendationDto prepareData(String correctContent) {
        RecommendationDto dto = new RecommendationDto();
        dto.setContent(correctContent);
        return dto;
    }
}