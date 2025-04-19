package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusFilterTest {

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @InjectMocks
    private StatusFilter statusFilter;

    @BeforeEach
    void setUp() {
        statusFilter = new StatusFilter(recommendationRequestMapper);
    }

    @Test
    void shouldReturnTrueWhenStatusIsNotNull() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(new RequestStatusDto("PENDING"));

        assertTrue(statusFilter.isApplicable(filter));
    }

    @Test
    void shouldReturnFalseWhenStatusIsNull() {
        RequestFilterDto filter = new RequestFilterDto();

        assertFalse(statusFilter.isApplicable(filter));
    }

    @Test
    void shouldFilterRequestsWithMatchingStatus() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(new RequestStatusDto("PENDING"));

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setStatus(RequestStatus.PENDING);

        RecommendationRequest request2 = new RecommendationRequest();
        request2.setStatus(RequestStatus.REJECTED);

        List<RecommendationRequest> requests = List.of(request1, request2);
        when(recommendationRequestMapper.mapStatusToEntity(filter.getStatus()))
                .thenReturn(RequestStatus.PENDING);

        Stream<RecommendationRequest> filteredStream = statusFilter.apply(requests.stream(), filter);
        List<RecommendationRequest> filteredList = filteredStream.toList();

        assertEquals(1, filteredList.size());
        assertEquals(RequestStatus.PENDING, filteredList.get(0).getStatus());
    }

    @Test
    void shouldFilterRequestsWhenAllMatch() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(new RequestStatusDto("PENDING"));

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setStatus(RequestStatus.PENDING);

        RecommendationRequest request2 = new RecommendationRequest();
        request2.setStatus(RequestStatus.PENDING);

        List<RecommendationRequest> requests = List.of(request1, request2);
        when(recommendationRequestMapper.mapStatusToEntity(filter.getStatus()))
                .thenReturn(RequestStatus.PENDING);

        Stream<RecommendationRequest> filteredStream = statusFilter.apply(requests.stream(), filter);
        List<RecommendationRequest> filteredList = filteredStream.toList();

        assertEquals(2, filteredList.size());
        assertEquals(RequestStatus.PENDING, filteredList.get(0).getStatus());
        assertEquals(RequestStatus.PENDING, filteredList.get(1).getStatus());
    }

    @Test
    void shouldReturnEmptyListWhenNoMatches() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(new RequestStatusDto("PENDING"));

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setStatus(RequestStatus.REJECTED);

        RecommendationRequest request2 = new RecommendationRequest();
        request2.setStatus(RequestStatus.REJECTED);

        List<RecommendationRequest> requests = List.of(request1, request2);
        when(recommendationRequestMapper.mapStatusToEntity(filter.getStatus()))
                .thenReturn(RequestStatus.PENDING);

        Stream<RecommendationRequest> filteredStream = statusFilter.apply(requests.stream(), filter);
        List<RecommendationRequest> filteredList = filteredStream.toList();

        assertEquals(0, filteredList.size());
    }
}