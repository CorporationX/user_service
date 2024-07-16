package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.RecommendationFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.util.TestDataFactory;

import java.time.LocalDateTime;

import static java.lang.Long.*;
import static java.util.Collections.*;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RecommendationFilter recommendationFilter;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private void assertRecommendationRequest(RecommendationRequestDto recommendationRequestDto, RecommendationRequest recommendationRequest) {
        assertThat(recommendationRequestDto.getMessage()).isEqualTo(recommendationRequest.getMessage());
        assertThat(recommendationRequestDto.getStatus().toUpperCase()).isEqualTo(recommendationRequest.getStatus().name());

        assertThat(recommendationRequestDto.getRequesterId()).isEqualTo(recommendationRequest.getRequester().getId());
        assertThat(recommendationRequestDto.getReceiverId()).isEqualTo(recommendationRequest.getReceiver().getId());

        assertThat(recommendationRequestDto.getCreatedAt()).isEqualTo(recommendationRequest.getCreatedAt());
        assertThat(recommendationRequestDto.getUpdatedAt()).isEqualTo(recommendationRequest.getUpdatedAt());
    }

    @DisplayName("Unit test for create method - positive scenario.")
    @Test
    void givenRecommendationRequestWhenCreateThenReturnRecommendationRequest() {
        // given - precondition
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        var recommendationRequest = TestDataFactory.createRecommendationRequest();
        var skillReq = TestDataFactory.createSkillRequests().get(0);
        var user = new User();

        given(userRepository.findById(anyLong()))
                .willReturn(of(new User()));
        given(skillRequestRepository.findById(anyLong()))
                .willReturn(of(skillReq));
        given(recommendationRequestMapper.toEntity(any(RecommendationRequestDto.class)))
                .willReturn(recommendationRequest);
        given(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .willReturn(recommendationRequest);
        given(skillRequestRepository.create(anyLong(), anyLong()))
                .willReturn(skillReq);
        given(recommendationRequestMapper.toDto(any(RecommendationRequest.class)))
                .willReturn(recommendationRequestDto);

        // when - action
        var actualResult = recommendationRequestService.create(recommendationRequestDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertRecommendationRequest(actualResult, recommendationRequest);
    }

    @DisplayName("Unit test for create method with requester that not exists in database - negative scenario.")
    @Test
    void givenInvalidRequesterAndOrReceiverIdWhenCreateThenThrowEntityNotFoundException() {
        // given - precondition
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        var invalidRequesterId = recommendationRequestDto.getRequesterId();

        given(userRepository.findById(invalidRequesterId))
                .willReturn(empty());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() ->  recommendationRequestService.create(recommendationRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id: " + invalidRequesterId + " not found.");
    }

    @DisplayName("Unit test for create method for recommendation request more than once every six months. - negative scenario.")
    @Test
    void givenInvalidRecommendationRequestWhenCreateThenThrowIllegalStateException() {
        // given - precondition
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        recommendationRequestDto.setUpdatedAt(LocalDateTime.now());

        given(userRepository.findById(anyLong()))
                .willReturn(of(new User()));

        // when - action and
        // then - verify the output
        assertThatThrownBy(() ->  recommendationRequestService.create(recommendationRequestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Recommendation requests can only be sent once every 6 months.");
    }

    @DisplayName("Unit test for create method for recommendation request with not existed skill - negative scenario.")
    @Test
    void givenNotExistedSkillWhenCreateThenThrowEntityNotFoundException() {
        // given - precondition
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();

        given(userRepository.findById(anyLong()))
                .willReturn(of(new User()));
        given(skillRequestRepository.findById(anyLong()))
                .willReturn(empty());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() ->recommendationRequestService.create(recommendationRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Some skills do not exist in the database." );
    }

    @DisplayName("Unit test for getRequests method - positive scenario.")
    @Test
    void givenRequestFilterWhenGetRequestsThenReturnFilteredRequests() {
        // given - precondition
        var requestFilterDto = TestDataFactory.createRequestFilterDto();

        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        var recommendationRequest = TestDataFactory.createRecommendationRequest();
        var expectedResult = singletonList(recommendationRequestDto);

        given(recommendationRequestRepository.findAll())
                .willReturn(singletonList(recommendationRequest));
        given(recommendationFilter.matchesFilter(recommendationRequest, requestFilterDto))
                .willReturn(true);
        given(recommendationRequestMapper.toDto(recommendationRequest))
                .willReturn(recommendationRequestDto);

        // when - action
        var actualResult = recommendationRequestService.getRequests(requestFilterDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).hasSize(expectedResult.size());
        assertThat(actualResult).containsAll(expectedResult);
    }

    @DisplayName("Unit test for getRequest method - positive scenario.")
    @Test
    void givenRequestIdWhenGetRequestThenReturnRequest(){
        // given - precondition
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        var recommendationRequest = TestDataFactory.createRecommendationRequest();
        var id = recommendationRequestDto.getId();

        given(recommendationRequestRepository.findById(id))
                .willReturn(of(recommendationRequest));
        given(recommendationRequestMapper.toDto(recommendationRequest))
                .willReturn(recommendationRequestDto);

        // when - action
        var actualResult = recommendationRequestService.getRequest(id);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertRecommendationRequest(actualResult, recommendationRequest);
    }

    @DisplayName("Unit test for getRequest method - negative scenario.")
    @Test
    void givenInvalidRequestIdWhenGetRequestThenThrowEntityNotFoundException(){
        // given - precondition
        var invalidId = MIN_VALUE;

        given(recommendationRequestRepository.findById(invalidId))
                .willReturn(empty());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() ->recommendationRequestService.getRequest(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("RecommendationRequest with id: " + invalidId + " not found.");
    }

    @DisplayName("Unit test for getRequest method - positive scenario.")
    @Test
    void givenRequestFilterWhenGetRequestThenReturnRequest(){
        // given - precondition
        var requestFilterDto = TestDataFactory.createRequestFilterDto();
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        var recommendationRequest = TestDataFactory.createRecommendationRequest();
        var id = requestFilterDto.getId();

        given(recommendationRequestRepository.findById(id))
                .willReturn(of(recommendationRequest));
        given(recommendationRequestMapper.toDto(recommendationRequest))
                .willReturn(recommendationRequestDto);

        // when - action
        var actualResult = recommendationRequestService.getRequest(requestFilterDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertRecommendationRequest(actualResult, recommendationRequest);
    }

    @DisplayName("Unit test for getRequest method - negative scenario.")
    @Test
    void givenInvalidIdInRequestFilterWhenGetRequestThenThrowEntityNotFoundException(){
        // given - precondition
        var requestFilterDto = TestDataFactory.createRequestFilterDto();
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        var recommendationRequest = TestDataFactory.createRecommendationRequest();
        requestFilterDto.setId(MIN_VALUE);
        var invalidId = requestFilterDto.getId();

        given(recommendationRequestRepository.findById(invalidId))
                .willReturn(empty());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> recommendationRequestService.getRequest(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("RecommendationRequest with id: " + invalidId + " not found.");
    }

    @DisplayName("Unit test for rejectRequest method - positive scenario.")
    @Test
    void givenRejectionWhenRejectRequestThenReturnRequest(){
        // given - precondition
        var rejection = new RejectionDto("Rejection reason.");
        var recommendationRequest = TestDataFactory.createRecommendationRequest();
        var id = recommendationRequest.getId();

        var recommendationRequestSaved = TestDataFactory.createRecommendationRequest();
        recommendationRequestSaved.setRejectionReason(rejection.getRejectionReason());
        recommendationRequestSaved.setStatus(RequestStatus.REJECTED);

        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();
        recommendationRequestDto.setStatus("Rejected");

        given(recommendationRequestRepository.findById(id))
                .willReturn(of(recommendationRequest));
        given(recommendationRequestRepository.save(recommendationRequest))
                .willReturn(recommendationRequestSaved);
        given(recommendationRequestMapper.toDto(recommendationRequest))
                .willReturn(recommendationRequestDto);

        // when - action
        var actualResult = recommendationRequestService.rejectRequest(id, rejection);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getStatus().toUpperCase()).isEqualTo(recommendationRequestSaved.getStatus().name());

        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
    }

    @DisplayName("Unit test for rejectRequest method - negative scenario.")
    @Test
    void givenInvalidRecommendationRequestWhenRejectRequestThenThrowIllegalStateException(){
        // given - precondition
        var rejection = new RejectionDto("Rejection reason.");
        var recommendationRequest = TestDataFactory.createRecommendationRequest();
        var id = recommendationRequest.getId();
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);

        given(recommendationRequestRepository.findById(id))
                .willReturn(of(recommendationRequest));

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> recommendationRequestService.rejectRequest(id, rejection))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only requests with PENDING status can be rejected.");

        verify(recommendationRequestRepository, times(1)).findById(id);
        verifyNoMoreInteractions(recommendationRequestRepository);
        verifyNoInteractions(recommendationRequestMapper);
    }
}