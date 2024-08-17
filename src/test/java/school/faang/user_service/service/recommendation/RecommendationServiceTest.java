package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.messaging.publisher.recommendationReceived.RecommendationPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.skillOffer.SkillOfferService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private RecommendationPublisher recommendationPublisher;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillOfferService skillOfferService;
    @InjectMocks
    private RecommendationService recommendationService;
    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private List<SkillOfferDto> skillOfferDtos;
    private List<SkillOffer> skillOffers;
    private long recommendationId = 1L;

    @BeforeEach
    void init() {
        skillOfferDtos = new ArrayList<>(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .skillId(2L)
                        .build(),
                SkillOfferDto.builder()
                        .id(2L)
                        .skillId(3L)
                        .build()));
        skillOffers = new ArrayList<>(List.of(
                SkillOffer.builder()
                        .id(1L)
                        .skill(Skill.builder()
                                .id(2L)
                                .build())
                        .build(),
                SkillOffer.builder()
                        .id(2L)
                        .skill(Skill.builder()
                                .id(3L)
                                .build())
                        .build()));
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(2L)
                .receiverId(3L)
                .skillOffers(skillOfferDtos)
                .build();
        recommendation = Recommendation.builder()
                .id(1L)
                .author(User.builder().id(2L).build())
                .receiver(User.builder().id(3L).build())
                .skillOffers(skillOffers)
                .build();
    }

    @Test
    void testCreateToEntity() {
        doNothing().when(recommendationValidator)
                .checkNotRecommendBeforeSixMonths(anyLong(), anyLong());
        doNothing().when(recommendationValidator)
                .validateSkillOffers(recommendationDto);
        when(recommendationMapper.toEntity(any(RecommendationDto.class)))
                .thenThrow(new RuntimeException("ошибка"));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                recommendationService.create(recommendationDto));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testCreateSave() {
        Mockito.doNothing().when(recommendationValidator)
                .checkNotRecommendBeforeSixMonths(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doNothing().when(recommendationValidator)
                .validateSkillOffers(recommendationDto);
        Mockito.when(recommendationMapper.toEntity(Mockito.any(RecommendationDto.class)))
                .thenReturn(recommendation);
        Mockito.when(recommendationRepository.save(Mockito.any(Recommendation.class)))
                .thenThrow(new RuntimeException("ошибка"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                recommendationService.create(recommendationDto));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testCreateSaveSkillOffers() {
        Mockito.doNothing().when(recommendationValidator)
                .checkNotRecommendBeforeSixMonths(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doNothing().when(recommendationValidator)
                .validateSkillOffers(recommendationDto);
        Mockito.when(recommendationMapper.toEntity(Mockito.any(RecommendationDto.class)))
                .thenReturn(recommendation);
        Mockito.when(recommendationRepository.save(Mockito.any(Recommendation.class)))
                .thenReturn(recommendation);
        Mockito.when(skillOfferService.saveSkillOffers(Mockito.anyList(), Mockito.anyLong()))
                .thenThrow(new RuntimeException("ошибка"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                recommendationService.create(recommendationDto));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testCreateToDto() {
        Mockito.doNothing().when(recommendationValidator)
                .checkNotRecommendBeforeSixMonths(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doNothing().when(recommendationValidator)
                .validateSkillOffers(recommendationDto);
        Mockito.when(recommendationMapper.toEntity(Mockito.any(RecommendationDto.class)))
                .thenReturn(recommendation);
        Mockito.when(recommendationRepository.save(Mockito.any(Recommendation.class)))
                .thenReturn(recommendation);
        Mockito.when(skillOfferService.saveSkillOffers(Mockito.anyList(), Mockito.anyLong()))
                .thenReturn(List.of(new SkillOffer()));
        Mockito.doNothing().when(recommendationPublisher).toEventAndPublish(recommendation);
        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(recommendationDto);

        recommendationService.create(recommendationDto);
    }

    @Test
    void testUpdateSkillOfferService() {
        when(recommendationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(recommendation));
        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(recommendationDto);
        doNothing().when(recommendationValidator)
                .checkNotRecommendBeforeSixMonths(anyLong(), anyLong());
        doNothing().when(recommendationValidator)
                .validateSkillOffers(recommendationDto);
        when(skillOfferService.newSkillOffersToUpdate(anyList(), anyList()))
                .thenReturn(skillOfferDtos);
        doNothing().when(skillOfferService)
                .updateSkillGuarantee(anyList(), anyLong(), anyLong());
        when(skillOfferService.saveSkillOffers(anyList(), anyLong()))
                .thenThrow(new RuntimeException("ошибка"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                recommendationService.update(recommendationId, recommendationDto));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testUpdateSave() {
        when(recommendationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(recommendation));
        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(recommendationDto);
        doNothing().when(recommendationValidator)
                .checkNotRecommendBeforeSixMonths(anyLong(), anyLong());
        doNothing().when(recommendationValidator)
                .validateSkillOffers(recommendationDto);
        when(skillOfferService.newSkillOffersToUpdate(anyList(), anyList()))
                .thenReturn(skillOfferDtos);
        doNothing().when(skillOfferService)
                .updateSkillGuarantee(anyList(), anyLong(), anyLong());
        when(skillOfferService.saveSkillOffers(anyList(), anyLong()))
                .thenReturn(skillOffers);
        when(recommendationMapper.toEntity(any(RecommendationDto.class)))
                .thenReturn(recommendation);
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenThrow(new RuntimeException("ошибка"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                recommendationService.update(recommendationId, recommendationDto));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testUpdateToDto() {
        when(recommendationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(recommendation));
        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(recommendationDto);
        doNothing().when(recommendationValidator)
                .checkNotRecommendBeforeSixMonths(anyLong(), anyLong());
        doNothing().when(recommendationValidator)
                .validateSkillOffers(recommendationDto);
        when(skillOfferService.newSkillOffersToUpdate(anyList(), anyList()))
                .thenReturn(skillOfferDtos);
        doNothing().when(skillOfferService)
                .updateSkillGuarantee(anyList(), anyLong(), anyLong());
        when(skillOfferService.saveSkillOffers(anyList(), anyLong()))
                .thenReturn(skillOffers);
        when(recommendationMapper.toEntity(any(RecommendationDto.class)))
                .thenReturn(recommendation);
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(recommendation);

        recommendationService.update(recommendationId, recommendationDto);

        verify(recommendationRepository).findById(1L);
        verify(recommendationMapper, times(2)).toDto(recommendation); // Проверяем один вызов
        verify(recommendationValidator).checkNotRecommendBeforeSixMonths(anyLong(), anyLong());
        verify(recommendationValidator).validateSkillOffers(any(RecommendationDto.class));
        verify(skillOfferService).newSkillOffersToUpdate(anyList(), anyList());
        verify(skillOfferService).updateSkillGuarantee(anyList(), anyLong(), anyLong());
        verify(skillOfferService).saveSkillOffers(anyList(), anyLong());
        verify(recommendationMapper).toEntity(any(RecommendationDto.class));
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testDeleteException(){
        doThrow(new RuntimeException("ошибка")).when(recommendationRepository)
                .deleteById(anyLong());
        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationService.delete(recommendationId));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testDeleteValid(){
        doNothing().when(recommendationRepository).deleteById(anyLong());

        recommendationService.delete(recommendationId);

        verify(recommendationRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void testGetAllUserRecommendationsException(){
        when(userService.getUserById(recommendationId))
                .thenReturn(User.builder().recommendationsReceived(List.of(recommendation)).build());
        when(recommendationMapper.toListDto(anyList()))
                .thenThrow(new RuntimeException("ошибка"));
        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationService.getAllUserRecommendations(recommendationId));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testGetAllUserRecommendationsValid(){
        when(userService.getUserById(recommendationId))
                .thenReturn(User.builder().recommendationsReceived(List.of(recommendation)).build());
        when(recommendationMapper.toListDto(anyList()))
                .thenReturn(List.of(recommendationDto));

        recommendationService.getAllUserRecommendations(recommendationId);

        verify(recommendationMapper, times(1))
                .toListDto(anyList());
    }

    @Test
    void testGetAllGivenRecommendationsException(){
        when(userService.getUserById(recommendationId))
                .thenReturn(User.builder().recommendationsGiven(List.of(recommendation)).build());
        when(recommendationMapper.toListDto(anyList()))
                .thenThrow(new RuntimeException("ошибка"));
        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationService.getAllGivenRecommendations(recommendationId));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testGetAllGivenRecommendationsValid(){
        when(userService.getUserById(recommendationId))
                .thenReturn(User.builder().recommendationsGiven(List.of(recommendation)).build());
        when(recommendationMapper.toListDto(anyList()))
                .thenReturn(List.of(recommendationDto));

        recommendationService.getAllGivenRecommendations(recommendationId);

        verify(recommendationMapper, times(1))
                .toListDto(anyList());
    }
}