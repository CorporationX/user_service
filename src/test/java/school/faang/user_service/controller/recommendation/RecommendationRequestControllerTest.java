package school.faang.user_service.controller.recommendation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import javax.xml.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    private RejectionDto rejectionDto;
    private RequestFilterDto filterDto;

    private Validator validator;
    private long id;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) factory.getValidator();

        rejectionDto = new RejectionDto("some reason");
        filterDto = RequestFilterDto.builder().build();
        id = 1L;
    }

    @Test
    void testRequestRecommendationWrongArgument() {
        assertThrows(DataValidationException.class, () -> recommendationRequestController.requestRecommendation(null));
        verify(recommendationRequestService, never()).create(null);
    }

    @Test
    void testRequestRecommendationIsOk() {
        RecommendationRequestDto recommendationRequestDto = RecommendationRequestDto.builder().build();
        when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto responseDto = recommendationRequestController
                .requestRecommendation(recommendationRequestDto);

        verify(recommendationRequestService, times(1)).create(recommendationRequestDto);

        assertEquals(recommendationRequestDto, responseDto);
    }

    @Test
    void testGetRecommendationRequestsWithEmptyFilter() {
        assertThrows(DataValidationException.class,
                () -> recommendationRequestController.getRecommendationRequests(null));
    }

    @Test
    void testGetRecommendationRequestsOk() {
        recommendationRequestController.getRecommendationRequests(filterDto);

        verify(recommendationRequestService, times(1)).getRequests(filterDto);
    }

    @Test
    void testGetRecommendationRequestByIdOk() {
        recommendationRequestController.getRecommendationRequest(id);

        verify(recommendationRequestService, times(1)).getRequest(Mockito.anyLong());
    }

    @Test
    void testRejectRequestWithNullDto() {

        /*assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.rejectRequest(id, null));*/
        Set<ConstraintViolation<Reje>> violations = validator.validate(dto);

        recommendationRequestController.rejectRequest(id, null);
    }

    @Test
    void testRejectRequestWithEmptyDto() {
        rejectionDto.setReason("");
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.rejectRequest(id, rejectionDto));
    }

    @Test
    void testRejectRequestDtoOk() {
        recommendationRequestController.rejectRequest(id, rejectionDto);
        verify(recommendationRequestService, times(1)).rejectRequest(id, rejectionDto);
    }
}
