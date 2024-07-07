package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.Long.*;
import static java.lang.String.format;
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
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setup() {
        recommendationRequestRepository = mock(RecommendationRequestRepository.class);
        userRepository = mock(UserRepository.class);
        skillRequestRepository = mock(SkillRequestRepository.class);
        recommendationRequestService = new RecommendationRequestService(
                recommendationRequestRepository,
                userRepository,
                skillRequestRepository);
    }

    private List<SkillRequestDto> createSkillRequestsDto() {
        SkillDto skill1 = new SkillDto(1L,"Java");
        SkillDto skill2 = new SkillDto(2L,"Spring Boot");

        SkillRequestDto skillRequest1 = new SkillRequestDto(1L, new RecommendationRequest(), skill1);
        SkillRequestDto skillRequest2 = new SkillRequestDto(2L, new RecommendationRequest(), skill2);

        return Arrays.asList(skillRequest1, skillRequest2);
    }
    private RecommendationRequest createRecommendationRequest(RecommendationRequestDto recommendationRequestDto){
        return RecommendationRequestMapper.INSTANCE.dtoToEntity(recommendationRequestDto);
    }

    private RecommendationRequestDto createRecommendationRequestDto() {
        List<SkillRequestDto> skills = createSkillRequestsDto();

        return RecommendationRequestDto.builder()
                .id(1L)
                .message("Please provide a recommendation.")
                .status(RequestStatus.PENDING)
                .skills(skills)
                .requesterId(1001L)
                .receiverId(1002L)
                .createdAt(LocalDateTime.of(2020, Month.JANUARY, 18, 0, 0))
                .updatedAt(LocalDateTime.of(2021, Month.JANUARY, 18, 0, 0))
                .build();
    }

    private RequestFilterDto createRequestFilterDto(){
        return RequestFilterDto.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requesterId(1001L)
                .receiverId(1002L)
                .recommendation(null)
                .skills(createSkillRequestsDto())
                .build();
    }

    @DisplayName("Unit test for create method - positive scenario.")
    @Test
    void givenRecommendationRequestWhenCreateThenReturnRecommendationRequest() {
        var recommendationRequestDto = createRecommendationRequestDto();
        var recommendationRequest = createRecommendationRequest(recommendationRequestDto);
        var skillReq = SkillRequestMapper.INSTANCE.dtoToEntity(recommendationRequestDto.getSkills().get(0));

        given(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .willReturn(recommendationRequest);
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(new User()));
        given(skillRequestRepository.findById(anyLong()))
                .willReturn(Optional.of(skillReq));
        given(skillRequestRepository.create(anyLong(), anyLong()))
                .willReturn(skillReq);

        var actualResult = recommendationRequestService.create(recommendationRequestDto);

        assertThat(recommendationRequestDto.getId()).isEqualTo(recommendationRequest.getId());
        assertThat(recommendationRequestDto.getMessage()).isEqualTo(recommendationRequest.getMessage());
        assertThat(recommendationRequestDto.getStatus()).isEqualTo(recommendationRequest.getStatus());

        assertThat(recommendationRequestDto.getSkills())
                .hasSize(recommendationRequest.getSkills().size())
                .allSatisfy(skillRequestDto -> {
                    SkillRequest skillRequest = recommendationRequest.getSkills().stream()
                            .filter(req -> req.getId() == skillRequestDto.getId())
                            .findFirst()
                            .orElse(null);
                    assertThat(skillRequest).isNotNull();
                    assertThat(skillRequestDto.getSkillDto().getId()).isEqualTo(skillRequest.getSkill().getId());
                    assertThat(skillRequestDto.getSkillDto().getTitle()).isEqualTo(skillRequest.getSkill().getTitle());
                });

        assertThat(recommendationRequestDto.getRequesterId()).isEqualTo(recommendationRequest.getRequester().getId());
        assertThat(recommendationRequestDto.getReceiverId()).isEqualTo(recommendationRequest.getReceiver().getId());

        assertThat(recommendationRequestDto.getCreatedAt()).isEqualTo(recommendationRequest.getCreatedAt());
        assertThat(recommendationRequestDto.getUpdatedAt()).isEqualTo(recommendationRequest.getUpdatedAt());
    }

    @DisplayName("Unit test for create method with requester that not exists in database - negative scenario.")
    @Test
    void givenInvalidRequesterAndOrReceiverIdWhenCreateThenThrowEntityNotFoundException() {
        var recommendationRequestDto = createRecommendationRequestDto();
        var invalidRequesterId = recommendationRequestDto.getRequesterId();

        given(userRepository.findById(invalidRequesterId)).willReturn(Optional.empty());

        assertThatThrownBy(() ->  recommendationRequestService.create(recommendationRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id: " + invalidRequesterId + " not found.");
    }


    @DisplayName("Unit test for create method for recommendation request more than once every six months. - negative scenario.")
    @Test
    void givenInvalidRecommendationRequestWhenCreateThenThrowIllegalStateException() {
        var recommendationRequestDto = createRecommendationRequestDto();
        recommendationRequestDto.setUpdatedAt(LocalDateTime.now());

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(new User()));

        assertThatThrownBy(() ->  recommendationRequestService.create(recommendationRequestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Recommendation requests can only be sent once every 6 months.");
    }

    @DisplayName("Unit test for create method for recommendation request with not existed skill - negative scenario.")
    @Test
    void givenNotExistedSkillWhenCreateThenThrowEntityNotFoundException() {
        var recommendationRequestDto = createRecommendationRequestDto();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(new User()));

        assertThatThrownBy(() ->recommendationRequestService.create(recommendationRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Some skills do not exist in the database." );
    }

    @DisplayName("Unit test for getRequests method - positive scenario.")
    @Test
    void givenRequestFilterWhenGetRequestsThenReturnFilteredRequests() {
        var requestFilterDto = createRequestFilterDto();
        var recommendationRequestDto = createRecommendationRequestDto();
        var recommendationRequest = createRecommendationRequest(recommendationRequestDto);
        var recommendationRequestDtoList = Arrays.asList(recommendationRequestDto);

        given(recommendationRequestService.getRequests(requestFilterDto))
                .willReturn(List.of(recommendationRequestDto));
        given(recommendationRequestRepository.findAll())
                .willReturn(List.of(recommendationRequest));

        var actualResult = recommendationRequestService.getRequests(requestFilterDto);

        assertThat(actualResult).hasSize(1)
                .allSatisfy(actReqRequestDto -> {
                    RecommendationRequestDto expReqRequestDto = recommendationRequestDtoList.stream()
                            .filter(req -> req.getId() == actReqRequestDto.getId())
                            .findFirst()
                            .orElse(null);
                    assertThat(expReqRequestDto).isNotNull();
                    assertThat(actReqRequestDto.getMessage()).isEqualTo(expReqRequestDto.getMessage());
                    assertThat(actReqRequestDto.getStatus()).isEqualTo(expReqRequestDto.getStatus());
                    assertThat(actReqRequestDto.getReceiverId()).isEqualTo(expReqRequestDto.getReceiverId());
                    assertThat(actReqRequestDto.getRequesterId()).isEqualTo(expReqRequestDto.getRequesterId());
                    assertThat(actReqRequestDto.getCreatedAt()).isEqualTo(expReqRequestDto.getCreatedAt());
                    assertThat(actReqRequestDto.getUpdatedAt()).isEqualTo(expReqRequestDto.getUpdatedAt());
                });

    }

    @DisplayName("Unit test for getRequest method - positive scenario.")
    @Test
    void givenRequestIdWhenGetRequestThenReturnRequest(){
        var recommendationRequestDto = createRecommendationRequestDto();
        var recommendationRequest = createRecommendationRequest(recommendationRequestDto);
        var id = recommendationRequestDto.getId();

        given(recommendationRequestRepository.findById(id))
                .willReturn(Optional.of(recommendationRequest));

        var actualResult = recommendationRequestService.getRequest(id);

        assertThat(actualResult.getId()).isEqualTo(recommendationRequest.getId());
        assertThat(actualResult.getMessage()).isEqualTo(recommendationRequest.getMessage());
        assertThat(actualResult.getStatus()).isEqualTo(recommendationRequest.getStatus());

        assertThat(actualResult.getSkills())
                .hasSize(recommendationRequest.getSkills().size())
                .allSatisfy(skillRequestDto -> {
                    SkillRequest skillRequest = recommendationRequest.getSkills().stream()
                            .filter(req -> req.getId() == skillRequestDto.getId())
                            .findFirst()
                            .orElse(null);
                    assertThat(skillRequest).isNotNull();
                    assertThat(skillRequestDto.getSkillDto().getId()).isEqualTo(skillRequest.getSkill().getId());
                    assertThat(skillRequestDto.getSkillDto().getTitle()).isEqualTo(skillRequest.getSkill().getTitle());
                });

        assertThat(actualResult.getRequesterId()).isEqualTo(recommendationRequest.getRequester().getId());
        assertThat(actualResult.getReceiverId()).isEqualTo(recommendationRequest.getReceiver().getId());

        assertThat(actualResult.getCreatedAt()).isEqualTo(recommendationRequest.getCreatedAt());
        assertThat(actualResult.getUpdatedAt()).isEqualTo(recommendationRequest.getUpdatedAt());
    }

    @DisplayName("Unit test for getRequest method - negative scenario.")
    @Test
    void givenInvalidRequestIdWhenGetRequestThenThrowEntityNotFoundException(){
        var invalidId = MIN_VALUE;

        given(recommendationRequestRepository.findById(invalidId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->recommendationRequestService.getRequest(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("RecommendationRequest with id: " + invalidId + " not found.");
    }

    @DisplayName("Unit test for getRequest method - positive scenario.")
    @Test
    void givenRequestFilterWhenGetRequestThenReturnRequest(){
        var requestFilterDto = createRequestFilterDto();
        var recommendationRequestDto = createRecommendationRequestDto();
        var recommendationRequest = createRecommendationRequest(recommendationRequestDto);
        var id = requestFilterDto.getId();

        given(recommendationRequestRepository.findById(id))
                .willReturn(Optional.of(recommendationRequest));

        var actualResult = recommendationRequestService.getRequest(requestFilterDto);

        assertThat(actualResult.getId()).isEqualTo(recommendationRequest.getId());
        assertThat(actualResult.getMessage()).isEqualTo(recommendationRequest.getMessage());
        assertThat(actualResult.getStatus()).isEqualTo(recommendationRequest.getStatus());

        assertThat(actualResult.getSkills())
                .hasSize(recommendationRequest.getSkills().size())
                .allSatisfy(skillRequestDto -> {
                    SkillRequest skillRequest = recommendationRequest.getSkills().stream()
                            .filter(req -> req.getId() == skillRequestDto.getId())
                            .findFirst()
                            .orElse(null);
                    assertThat(skillRequest).isNotNull();
                    assertThat(skillRequestDto.getSkillDto().getId()).isEqualTo(skillRequest.getSkill().getId());
                    assertThat(skillRequestDto.getSkillDto().getTitle()).isEqualTo(skillRequest.getSkill().getTitle());
                });

        assertThat(actualResult.getRequesterId()).isEqualTo(recommendationRequest.getRequester().getId());
        assertThat(actualResult.getReceiverId()).isEqualTo(recommendationRequest.getReceiver().getId());

        assertThat(actualResult.getCreatedAt()).isEqualTo(recommendationRequest.getCreatedAt());
        assertThat(actualResult.getUpdatedAt()).isEqualTo(recommendationRequest.getUpdatedAt());
    }



    @DisplayName("Unit test for getRequest method - negative scenario.")
    @Test
    void givenInvalidIdInRequestFilterWhenGetRequestThenThrowEntityNotFoundException(){
        var requestFilterDto = createRequestFilterDto();
        requestFilterDto.setId(MIN_VALUE);
        var invalidId = requestFilterDto.getId();


        given(recommendationRequestRepository.findById(invalidId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationRequestService.getRequest(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("RecommendationRequest with id: " + invalidId + " not found.");
    }

    @DisplayName("Unit test for rejectRequest method - positive scenario.")
    @Test
    void givenRejectionWhenRejectRequestThenReturnRequest(){
        var rejection = new RejectionDto("Rejection reason.");
        var recommendationRequest = RecommendationRequestMapper.INSTANCE.dtoToEntity(createRecommendationRequestDto());
        var id = recommendationRequest.getId();
        var recommendationRequestSaved = RecommendationRequestMapper.INSTANCE
                .dtoToEntity(createRecommendationRequestDto());
        recommendationRequestSaved.setRejectionReason(rejection.getRejectionReason());
        recommendationRequestSaved.setStatus(RequestStatus.REJECTED);

        given(recommendationRequestRepository.findById(id))
                .willReturn(Optional.of(recommendationRequest));

        given(recommendationRequestRepository.save(recommendationRequest))
                .willReturn(recommendationRequestSaved);

        var actualResult = recommendationRequestService.rejectRequest(id, rejection);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(RecommendationRequestMapper.INSTANCE.entityToDto(recommendationRequestSaved));
        assertThat(actualResult.getStatus()).isEqualTo(recommendationRequestSaved.getStatus());
    }

    @DisplayName("Unit test for rejectRequest method - negative scenario.")
    @Test
    void givenInvalidRecommendationRequestWhenRejectRequestThenThrowIllegalStateException(){
        var rejection = new RejectionDto("Rejection reason.");
        var recommendationRequest = RecommendationRequestMapper.INSTANCE.dtoToEntity(createRecommendationRequestDto());
        var id = recommendationRequest.getId();
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);

        given(recommendationRequestRepository.findById(id))
                .willReturn(Optional.of(recommendationRequest));

        assertThatThrownBy(() -> recommendationRequestService.rejectRequest(id, rejection))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only requests with PENDING status can be rejected.");
    }
}