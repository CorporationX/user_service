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
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = {MockitoExtension.class})
public class RecommendationServiceTest {

    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testCreateWithTimeValidateException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        doThrow(DataValidationException.class).when(recommendationValidator).recommendationTimeValidation(recommendationDto);
        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateWithSkillValidateException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        doThrow(DataValidationException.class).when(recommendationValidator).skillEmptyValidation(recommendationDto);
        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testUpdateWithValidateException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        doThrow(DataValidationException.class).when(recommendationValidator).skillEmptyValidation(recommendationDto);
        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendationDto));
    }

    @Test
    public void testDeleteById() {
        Long idToDelete = 1L;
        recommendationService.delete(idToDelete);
        verify(recommendationRepository, times(1)).deleteById(idToDelete);
    }

    @Test
    public void testCreateSuccess() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto[]{}));
        Long expectedId = 10L;
        doNothing().when(recommendationValidator).recommendationTimeValidation(recommendationDto);
        doNothing().when(recommendationValidator).skillEmptyValidation(recommendationDto);

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
    public void testUpdateSuccess() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("new content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto[]{}));

        doNothing().when(recommendationValidator).skillEmptyValidation(recommendationDto);
        doNothing().when(skillOfferRepository).deleteAllByRecommendationId(recommendationDto.getId());

        recommendationService.update(recommendationDto);

        verify(recommendationRepository, times(1)).update(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
    }

    @Test
    public void testGetAllUserRecommendations() {
        long receiverId = 2L;
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
    public void testGetAllGivenRecommendations() {
        long authorId = 2L;
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

        Page<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId, pageable.getPageNumber(), pageable.getPageSize());

        verify(userRepository).findById(authorId);
        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage, result);
    }
}
