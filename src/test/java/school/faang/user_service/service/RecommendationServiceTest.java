package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.mapper.RecommendationMapper;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.jpa.UserJpaRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private RecommendationValidator recommendationValidator;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testCreateValidInputSuccess() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(
                new SkillOfferDto(1L, 1L),
                new SkillOfferDto(2L, 2L)));

        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        User author = new User();
        author.setId(2L);
        recommendationEntity.setAuthor(author);
        User receiver = new User();
        receiver.setId(3L);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent("content");
        recommendationEntity.setSkillOffers(List.of(
                new SkillOffer(1L, new Skill(), recommendationEntity),
                new SkillOffer(2L, new Skill(), recommendationEntity)));

        doNothing().when(recommendationValidator).validateAll(recommendationDto);
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertEquals(recommendationDto, result);
        verify(recommendationRepository, times(1)).save(recommendationEntity);
    }

    @Test
    public void testCreateRecommendationWithNullSkills() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(null);

        doThrow(DataValidationException.class).when(recommendationValidator).validateAll(recommendationDto);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateRecommendationWithEmptySkill() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(new ArrayList<>());

        doThrow(DataValidationException.class).when(recommendationValidator).validateAll(recommendationDto);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testUpdateWithValidateException() {
        RecommendationDto recommendationDto = new RecommendationDto();

        doThrow(DataValidationException.class).when(recommendationValidator).validateRecommendationForUpdate(recommendationDto);

        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendationDto));
    }

    @Test
    public void testSuccessfulUpdate() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(
                new SkillOfferDto(1L, 1L),
                new SkillOfferDto(2L, 2L)));

        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        User author = new User();
        author.setId(2L);
        recommendationEntity.setAuthor(author);
        User receiver = new User();
        receiver.setId(3L);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent("content");
        recommendationEntity.setSkillOffers(List.of(
                new SkillOffer(1L, new Skill(), recommendationEntity),
                new SkillOffer(2L, new Skill(), recommendationEntity)));

        doNothing().when(recommendationValidator).validateRecommendationForUpdate(recommendationDto);
        doNothing().when(recommendationValidator).validateAll(recommendationDto);
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.update(recommendationDto);

        verify(recommendationRepository, times(1)).save(recommendationEntity);

        assertEquals(result, recommendationDto);
    }

    @Test
    public void testDeleteById() {
        Long idToDelete = 1L;

        recommendationService.delete(idToDelete);

        verify(recommendationRepository, times(1)).deleteById(idToDelete);
    }

    @Test
    public void testGetAllUserRecommendation() {
        long receiverId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        List<Recommendation> recommendationsList = List.of(recommendation1, recommendation2);

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        List<RecommendationDto> recommendationDtosList = List.of(recommendationDto1, recommendationDto2);

        Page<Recommendation> page = new PageImpl<>(recommendationsList, PageRequest.of(pageNumber, pageSize), recommendationsList.size());

        when(recommendationRepository.findAllByReceiverId(anyLong(), any(Pageable.class))).thenReturn(page);

        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        Page<RecommendationDto> resultPage = recommendationService.getAllUserRecommendation(receiverId, pageNumber, pageSize);

        assertEquals(recommendationDtosList, resultPage.getContent());
    }

    @Test
    public void testGetAllRecommendation() {
        long receiverId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        List<Recommendation> recommendationsList = List.of(recommendation1, recommendation2);

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        List<RecommendationDto> recommendationDtosList = List.of(recommendationDto1, recommendationDto2);

        Page<Recommendation> page = new PageImpl<>(recommendationsList, PageRequest.of(pageNumber, pageSize), recommendationsList.size());

        when(recommendationRepository.findAllByAuthorId(anyLong(), any(Pageable.class))).thenReturn(page);

        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        Page<RecommendationDto> resultPage = recommendationService.getAllRecommendation(receiverId, pageNumber, pageSize);

        assertEquals(recommendationDtosList, resultPage.getContent());
    }
}