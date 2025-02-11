package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validation.RecommendationValidation;

public class RecommendationServiceTest {

  @Mock private RecommendationRepository recommendationRepository;

  @Mock private SkillOfferRepository skillOfferRepository;

  @Mock private SkillRepository skillRepository;

  @Mock private RecommendationMapper recommendationMapper;

  @Mock RecommendationValidation recommendationValidation;

  @InjectMocks private RecommendationService recommendationService;

  private RecommendationDto recommendation;
  private List<SkillOfferDto> skills;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    recommendation = new RecommendationDto();
    recommendation.setReceiverId(1L);
    recommendation.setId(1L);
    recommendation.setContent("content");
    recommendation.setAuthorId(1L);

    SkillOfferDto firstSkill = new SkillOfferDto();
    SkillOfferDto secondSkill = new SkillOfferDto();
    firstSkill.setId(1L);
    secondSkill.setId(2L);
    skills = List.of(firstSkill, secondSkill);
    recommendation.setSkillOffers(skills);
  }

  @Test
  public void testCreate() {
    doNothing()
        .when(recommendationValidation)
        .validateOfLatestRecommendation(any(RecommendationDto.class));
    doNothing().when(recommendationValidation).validateOfSkills(skills);
    when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);

    recommendationService.create(recommendation);

    verify(skillOfferRepository, times(skills.size())).create(anyLong(), anyLong());
    verify(recommendationRepository, times(1)).create(anyLong(), anyLong(), anyString());
  }

  @Test
  public void testUpdate() {
    doNothing()
        .when(recommendationValidation)
        .validateOfLatestRecommendation(any(RecommendationDto.class));
    doNothing().when(recommendationValidation).validateOfSkills(skills);
    when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);

    recommendationService.update(recommendation);

    verify(recommendationRepository, times(1)).update(anyLong(), anyLong(), anyString());
    verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendation.getId());
    verify(skillOfferRepository, times(skills.size())).create(anyLong(), anyLong());
  }

  @Test
  public void testDelete() {
    long id = 1L;

    recommendationService.delete(id);
    verify(recommendationRepository, times(1)).deleteById(id);
  }

  @Test
  public void testGetAllUserRecommendations() {
    long receiverId = 1L;
    Recommendation recommendation1 = new Recommendation();
    recommendation1.setId(1L);

    Recommendation recommendation2 = new Recommendation();
    recommendation2.setId(2L);

    List<Recommendation> mockRecommendations = List.of(recommendation1, recommendation2);
    Page<Recommendation> mockPage = new PageImpl<>(mockRecommendations);

    RecommendationDto recommendationDto1 = new RecommendationDto();
    recommendationDto1.setId(1L);

    RecommendationDto recommendationDto2 = new RecommendationDto();
    recommendationDto2.setId(2L);

    when(recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged()))
        .thenReturn(mockPage);

    when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
    when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

    List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(recommendationDto1, result.get(0));
    assertEquals(recommendationDto2, result.get(1));

    verify(recommendationRepository, times(1)).findAllByReceiverId(receiverId, Pageable.unpaged());
    verify(recommendationMapper, times(1)).toDto(recommendation1);
    verify(recommendationMapper, times(1)).toDto(recommendation2);
  }

  @Test
  public void testGetAllGivenRecommendations() {

    long authorId = 2L;
    Recommendation recommendation1 = new Recommendation();
    recommendation1.setId(3L);

    Recommendation recommendation2 = new Recommendation();
    recommendation2.setId(4L);

    List<Recommendation> mockRecommendations = List.of(recommendation1, recommendation2);
    Page<Recommendation> mockPage = new PageImpl<>(mockRecommendations);

    RecommendationDto recommendationDto1 = new RecommendationDto();
    recommendationDto1.setId(3L);

    RecommendationDto recommendationDto2 = new RecommendationDto();
    recommendationDto2.setId(4L);

    when(recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()))
        .thenReturn(mockPage);

    when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
    when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

    List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(recommendationDto1, result.get(0));
    assertEquals(recommendationDto2, result.get(1));

    verify(recommendationRepository, times(1)).findAllByAuthorId(authorId, Pageable.unpaged());
    verify(recommendationMapper, times(1)).toDto(recommendation1);
    verify(recommendationMapper, times(1)).toDto(recommendation2);
  }
}
