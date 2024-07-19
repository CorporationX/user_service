package school.faang.user_service.validation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exaception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class RecommendationValidationTest {

    @InjectMocks
    private RecommendationValidation recommendationValidation;

    private RecommendationDto recommendationDto;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;
    private List<SkillOfferDto> invalidSkillOffers;

    RecommendationValidationTest() {
    }

    @Test
    void testRecommendationIsNotEmpty() {
        RecommendationDto emptyContent = new RecommendationDto(1L,1L,1L, "", null,null);
        assertThrows(DataValidationException.class,
                () -> recommendationValidation.recommendationIsNotEmpty(emptyContent));
    }

    @Test
    void testRecommendationIsNotBlank(){
        RecommendationDto blankRecommendation = new RecommendationDto(1L, 1L,1L,"    ", null, null);
        assertThrows(DataValidationException.class,
                () -> recommendationValidation.recommendationIsNotEmpty(blankRecommendation));
    }

    @Test
    void testRecommendationIsNotEmptyValid(){
        RecommendationDto recommendationValid = new RecommendationDto(1L,1L, 1L,"content", null,null);
        recommendationValidation.recommendationIsNotEmpty(recommendationValid);
    }
    @Test
    void validationHalfOfYear_NotSixMonths() {
        RecommendationDto notSixMonths =  new RecommendationDto(1L,1L, 1L,"content", null, LocalDateTime.now());
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusMonths(5));

        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(anyLong(),anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(DataValidationException.class,
                () -> recommendationValidation.validationHalfOfYear(notSixMonths));
    }

    @Test
    void validationHalfOfYear_SixMonthsValid(){
        RecommendationDto moreSixMonths =  new RecommendationDto(1L,1L, 1L,"content", null, LocalDateTime.now());
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusMonths(7));

        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(anyLong(),anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        recommendationValidation.validationHalfOfYear(moreSixMonths);
    }

    @Test
    void skillValidSkillFound() {
        List<SkillOfferDto> validSkillOffers = Collections.singletonList(new SkillOfferDto(1L, 1L, 1L));

        Mockito.when(skillOfferRepository.existsById(anyLong())).thenReturn(true);
        recommendationValidation.skillValid(validSkillOffers);
    }

    @Test
    void skillValidSkillNotFound(){

        invalidSkillOffers = Collections.singletonList(new SkillOfferDto(2L, 2L, 2L));

        Mockito.when(skillOfferRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> recommendationValidation.skillValid(invalidSkillOffers));
    }
}