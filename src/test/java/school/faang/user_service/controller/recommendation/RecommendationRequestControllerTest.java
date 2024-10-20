package school.faang.user_service.controller.recommendation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.dto.recommendation.RejectionDto;
import school.faang.user_service.model.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
        validator = factory.getValidator();

        rejectionDto = new RejectionDto("some reason");
        filterDto = RequestFilterDto.builder().build();
        id = 1L;
    }

    @Test
    void testRequestRecommendation_WithEmptyParamDto() throws NoSuchMethodException {
        // Arrange
        RecommendationRequestDto recommendationRequestDto = RecommendationRequestDto.builder().build();

        // Act
        Set<ConstraintViolation<RecommendationRequestController>> violations = validator
                .forExecutables()
                .validateParameters(
                        recommendationRequestController,
                        recommendationRequestController.getClass().getMethod("requestRecommendation", RecommendationRequestDto.class),
                        new Object[]{recommendationRequestDto}
                );

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testRequestRecommendation_WithNullParam() throws NoSuchMethodException {
        // Arrange
        RecommendationRequestDto recommendationRequestDto = null;

        // Act
        Set<ConstraintViolation<RecommendationRequestController>> violations = validator
                .forExecutables()
                .validateParameters(
                        recommendationRequestController,
                        recommendationRequestController.getClass().getMethod("requestRecommendation", RecommendationRequestDto.class),
                        new Object[]{recommendationRequestDto}
                );

        // Assert
        assertThat(violations).isNotEmpty();
    }


    @Test
    void testRequestRecommendation_WithValidParam() {
        RecommendationRequestDto recommendationRequestDto = RecommendationRequestDto.builder().build();
        when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto responseDto = recommendationRequestController
                .requestRecommendation(recommendationRequestDto);

        verify(recommendationRequestService, times(1)).create(recommendationRequestDto);

        assertEquals(recommendationRequestDto, responseDto);
    }

    @Test
    void testGetRecommendationRequests_WithNullParam() throws NoSuchMethodException {
        // Arrange
        RequestFilterDto requestFilterDto = null;

        // Act
        Set<ConstraintViolation<RecommendationRequestController>> violations = validator
                .forExecutables()
                .validateParameters(
                        recommendationRequestController,
                        recommendationRequestController.getClass().getMethod("getRecommendationRequests", RequestFilterDto.class),
                        new Object[]{requestFilterDto}
                );

        // Assert
        assertThat(violations).isNotEmpty();
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
    void testRejectRequestWithNullDto() throws NoSuchMethodException {
        // Arrange
        long validId = 1L;
        RejectionDto invalidRejectionDto = null;

        // Act
        Set<ConstraintViolation<RecommendationRequestController>> violations = validator
                .forExecutables()
                .validateParameters(
                        recommendationRequestController,
                        recommendationRequestController.getClass().getMethod("rejectRequest", long.class, RejectionDto.class),
                        new Object[]{validId, invalidRejectionDto}
                );

        // Assert
        assertThat(violations).isNotEmpty();
        ConstraintViolation<RecommendationRequestController> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("must not be null");
    }

    @Test
    void testRejectRequestWithEmptyDto() throws NoSuchMethodException {
        // Arrange
        long validId = 1L;
        RejectionDto invalidRejectionDto = RejectionDto.builder().build();

        // Act
        Set<ConstraintViolation<RecommendationRequestController>> violations = validator
                .forExecutables()
                .validateParameters(
                        recommendationRequestController,
                        recommendationRequestController.getClass().getMethod("rejectRequest", long.class, RejectionDto.class),
                        new Object[]{validId, invalidRejectionDto}
                );

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testRejectRequestDtoOk() {
        recommendationRequestController.rejectRequest(id, rejectionDto);
        verify(recommendationRequestService, times(1)).rejectRequest(id, rejectionDto);
    }
}
