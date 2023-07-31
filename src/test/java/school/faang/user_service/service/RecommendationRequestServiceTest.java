package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    RecommendationRequestRepository repository;
    @Spy
    RecommendationRequestMapperImpl mapper;
    @InjectMocks
    RecommendationRequestService service;

    @ParameterizedTest
    @MethodSource("getId")
    @DisplayName("Get recommendation request by id")
    void getRecommendationRequestById(long id) {
        RecommendationRequest request = new RecommendationRequest();
        request.setId(id);
        when(repository.findById(id))
                .thenReturn(Optional.of(request));
        RecommendationRequestDto requestDto = service.getRequest(id);
        assertEquals(id, requestDto.getId());
    }

    @ParameterizedTest
    @MethodSource("getId")
    @DisplayName("Recommendation request not found")
    void getRecommendationRequestByIdNotFound(long id) {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.getRequest(id));
        assertEquals("Recommendation request not found", exception.getMessage());
    }

    private static Stream<Arguments> getId() {
        return Stream.of(
                Arguments.of(2L),
                Arguments.of(19L),
                Arguments.of(11231L),
                Arguments.of(1L),
                Arguments.of(123L),
                Arguments.of(1547L)
        );
    }

}