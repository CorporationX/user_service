package school.faang.user_service.service.recommendation;

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
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.messaging.RecommendationEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationEventPublisher eventPublisher;

    @Test
    public void testCreate() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L)));

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

        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto updatedRecommendationDto = recommendationService.create(recommendationDto);

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));

        assertEquals(updatedRecommendationDto, recommendationDto);
    }

    @Test
    public void testCreate_InvalidSkillId_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L)));

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        verify(recommendationRepository, never()).save(any());
    }

    @Test
    public void testCreate_RecommendationIntervalNotExceeded_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L)));

        Recommendation previousRecommendation = new Recommendation();
        previousRecommendation.setUpdatedAt(LocalDateTime.now().minusMonths(6 - 1));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(2L, 3L))
                .thenReturn(Optional.of(previousRecommendation));

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        verify(recommendationRepository, never()).save(any());
    }

    @Test
    public void testCreate_RecommendationEmptySkillsList_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(new ArrayList<>());

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testsUpdate() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L)));

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

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendationEntity));
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto updatedRecommendationDto = recommendationService.update(recommendationDto);

        verify(recommendationRepository).deleteById(1L);
        verify(recommendationRepository).save(recommendationEntity);

        assertEquals(updatedRecommendationDto, recommendationDto);
    }

    @Test
    public void testUpdate_InvalidSkillId_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L)));

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

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendationEntity));
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendationDto));
    }

    @Test
    public void testUpdate_RecommendationIntervalNotExceeded_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L)));

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

        Recommendation previousRecommendation = new Recommendation();
        previousRecommendation.setUpdatedAt(LocalDateTime.now().minusMonths(6 - 1));

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendationEntity));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(2L, 3L))
                .thenReturn(Optional.of(previousRecommendation));

        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendationDto));
    }

    @Test
    public void testUpdate_RecommendationEmptySkillsList_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(new ArrayList<>());

        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        User author = new User();
        author.setId(2L);
        recommendationEntity.setAuthor(author);
        User receiver = new User();
        receiver.setId(3L);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent("content");

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendationEntity));
        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendationDto));
    }

    @Test
    public void testGetAllUserRecommendations() {
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

        Page<RecommendationDto> resultPage = recommendationService.getAllUserRecommendations(receiverId, pageNumber, pageSize);

        assertEquals(recommendationDtosList, resultPage.getContent());
    }

    @Test
    public void testGetAllGivenRecommendations() {
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

        Page<RecommendationDto> resultPage = recommendationService.getAllGivenRecommendations(receiverId, pageNumber, pageSize);

        assertEquals(recommendationDtosList, resultPage.getContent());
    }
}
