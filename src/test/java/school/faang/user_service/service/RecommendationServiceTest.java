package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mappers.RecommendationMapper;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillMapper skillMapper;


    @Test
    public void testCreate() {
        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(1L)
                .skill(1L)
                .recommendation(1L)
                .build();
        List<SkillOfferDto> skillOffers = Arrays.asList(skillOfferDto);
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("Test recommendation")
                .skillOffers(skillOffers)
                .build();
        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        Recommendation recommendationEntity = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Test recommendation")
                .skillOffers(new ArrayList<>())
                .build();
        SkillOffer skillOffer = SkillOffer.builder()
                .id(1L).build();

        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);
        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1L);
        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.of(recommendationEntity));
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);
        when(skillOfferRepository.findById(anyLong())).thenReturn(Optional.of(skillOffer));

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(recommendationValidator).validateRecommendationContent(recommendationDto);
        verify(recommendationValidator).validateRecommendationTerm(recommendationDto);
        verify(skillValidator).validateSkillOffersDto(recommendationDto);
        verify(recommendationRepository, times(1)).create(anyLong(), anyLong(), anyString());
        verify(recommendationRepository, times(1)).findById(anyLong());
        verify(skillOfferRepository, times(1)).create(anyLong(), anyLong());

        assertEquals(recommendationDto.getId(), result.getId());
        assertEquals(recommendationDto.getAuthorId(), result.getAuthorId());
        assertEquals(recommendationDto.getReceiverId(), result.getReceiverId());
        assertEquals(recommendationDto.getContent(), result.getContent());
        assertEquals(recommendationDto.getSkillOffers(), result.getSkillOffers());
    }

    @Test
    public void testUpdate() {
        SkillOfferDto skillOfferDtoFirst = SkillOfferDto.builder()
                .id(1L)
                .skill(1L)
                .recommendation(1L)
                .build();
        SkillOfferDto skillOfferDtoSecond = SkillOfferDto.builder()
                .id(2L)
                .skill(2L)
                .recommendation(2L)
                .build();

        List<SkillOfferDto> skillOffers = new ArrayList<>();
        skillOffers.add(skillOfferDtoFirst);
        skillOffers.add(skillOfferDtoSecond);

        RecommendationDto recommendationUpdate = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("Updated recommendation")
                .skillOffers(skillOffers)
                .build();

        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        SkillOffer skillOffer = SkillOffer.builder()
                .id(1L).build();
        List<SkillOffer> skillOfferList = new ArrayList<>();
        skillOfferList.add(skillOffer);

        Recommendation recommendationEntity = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Original recommendation")
                .skillOffers(skillOfferList)
                .build();

        when(recommendationRepository.update(recommendationUpdate.getAuthorId(), recommendationUpdate.getReceiverId(), recommendationUpdate.getContent())).thenReturn(recommendationEntity);
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);
        when(skillOfferRepository.findById(anyLong())).thenReturn(Optional.of(skillOffer));
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationUpdate);

        RecommendationDto result = recommendationService.update(recommendationUpdate);

        verify(recommendationValidator).validateRecommendationContent(recommendationUpdate);
        verify(recommendationValidator).validateRecommendationTerm(recommendationUpdate);
        verify(skillValidator).validateSkillOffersDto(recommendationUpdate);
        verify(recommendationRepository, times(1)).update(anyLong(), anyLong(), anyString());
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendationEntity.getId());
        verify(skillOfferRepository, times(1)).create(1, 1);

        assertEquals(recommendationUpdate.getId(), result.getId());
        assertEquals(recommendationUpdate.getAuthorId(), result.getAuthorId());
        assertEquals(recommendationUpdate.getReceiverId(), result.getReceiverId());
        assertEquals(recommendationUpdate.getContent(), result.getContent());
        assertEquals(recommendationUpdate.getSkillOffers(), result.getSkillOffers());
    }

    @Test
    public void testDeleteInvokesDeleteById() {
        recommendationService.delete(1);
        verify(recommendationRepository).deleteById(1L);
    }

    @Test
    public void testGetAllUserRecommendations() {
        // Test data
        long receiverId = 2L;
        User receiver = User.builder().id(2L).build();
        List<Recommendation> recommendationList = List.of(
                Recommendation.builder().id(1L).content("Recommendation 1").receiver(receiver).build(),
                Recommendation.builder().id(2L).content("Recommendation 2").receiver(receiver).build()
        );
        List<RecommendationDto> expectedRecommendationDtoList = List.of(
                RecommendationDto.builder().id(1L).content("Recommendation 1").receiverId(receiverId).build(),
                RecommendationDto.builder().id(2L).content("Recommendation 2").receiverId(receiverId).build()
        );

        // Mock the repository method to return the test data
        when(recommendationRepository.findAllByReceiverId(receiverId)).thenReturn(recommendationList);
        when(recommendationMapper.toRecommendationDtos(anyList())).thenReturn(expectedRecommendationDtoList);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId);

        verify(recommendationRepository).findAllByReceiverId(receiverId);

        assertEquals(expectedRecommendationDtoList, result);
    }
}








