package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationMapper;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(value = {MockitoExtension.class})
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testCreateShouldReturnCannotBeEmptyMsg() {
        DataValidationException dataValidationException = Assert.assertThrows(DataValidationException.class,
                () -> recommendationService
                        .create(new RecommendationDto(1L,
                                2L,
                                3L,
                                null,
                                null,
                                LocalDateTime.now())));
        String expectedMessage = "recommendation cannot be empty";
        String actualMessage = dataValidationException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCreateShouldReturnEarlyThan6MonthsMsg() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("content");
        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(LocalDateTime.now());
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(lastRecommendation));
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        String expectedMessage = "the recommendation can be given only after 6 months!";
        String actualMessage = dataValidationException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCreateShouldReturnRecommendationId() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(anyLong());
        recommendationDto.setReceiverId(anyLong());
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto[]{}));
        Long expectedId = 10L;
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId()))
                .thenReturn(Optional.empty());
        when(recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent())).thenReturn(expectedId);

        Long resultId = recommendationService.create(recommendationDto);

        verify(recommendationRepository).create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        assertEquals(expectedId, resultId);
    }

    @Test
    public void testUpdateShouldReturnUpdatedRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("new content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto[]{}));
        Recommendation expectedRecommendation = recommendationMapper.toEntity(recommendationDto);
        when(recommendationRepository
                .update(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId(),
                        recommendationDto.getContent())).thenReturn(expectedRecommendation);
        Recommendation result = recommendationService.update(recommendationDto);
        verify(recommendationRepository).update(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        assertEquals(expectedRecommendation, result);
    }

    @Test
    public void testDelete() {
        Long recommendationId = 1L;
        doNothing().when(recommendationRepository).deleteById(recommendationId);
        recommendationService.delete(recommendationId);
        verify(recommendationRepository, times(1)).deleteById(recommendationId);
    }

    @Test
    public void testGetAllUserRecommendations() {
        Long receiverId = 2L;
        Pageable pageable = PageRequest.of(0, 5);

        User receiver = new User();
        receiver.setId(receiverId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        recommendation1.setContent("recommendation 1");
        recommendation1.setReceiver(receiver);
        recommendation1.setCreatedAt(LocalDateTime.now());
        recommendation1.setUpdatedAt(LocalDateTime.now());

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        recommendation2.setContent("recommendation 2");
        recommendation2.setReceiver(receiver);
        recommendation2.setCreatedAt(LocalDateTime.now());
        recommendation2.setUpdatedAt(LocalDateTime.now());

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(recommendation1);
        recommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto(1L, 1L, 2L, "recommendation 1", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, 4L, 2L, "recommendation 2", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        recommendationDtoList.add(recommendationDto1);
        recommendationDtoList.add(recommendationDto2);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(recommendationRepository.findAllByReceiverId(receiverId, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        Page<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId, pageable.getPageNumber(), pageable.getPageSize());

        verify(recommendationRepository).findAllByReceiverId(receiverId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage, result);
    }
    @Test
    public void testGetAllUserRecommendations() {
        Long authorId = 2L;
        Pageable pageable = PageRequest.of(0, 5);

        User author = new User();
        author.setId(authorId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        recommendation1.setContent("recommendation 1");
        recommendation1.setReceiver(author);
        recommendation1.setCreatedAt(LocalDateTime.now());
        recommendation1.setUpdatedAt(LocalDateTime.now());

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        recommendation2.setContent("recommendation 2");
        recommendation2.setReceiver(author);
        recommendation2.setCreatedAt(LocalDateTime.now());
        recommendation2.setUpdatedAt(LocalDateTime.now());

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(recommendation1);
        recommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto(1L, 1L, 2L, "recommendation 1", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, 4L, 2L, "recommendation 2", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        recommendationDtoList.add(recommendationDto1);
        recommendationDtoList.add(recommendationDto2);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        Page<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId, pageable);

        verify(userRepository).findById(authorId);
        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage, result);
    }

}