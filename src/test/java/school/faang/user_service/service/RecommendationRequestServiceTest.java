package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filer.RequestFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private RequestFilterDto requestFilterDto;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RequestFilter idPatternFilter;
    @Mock
    private RequestFilter statusPatternFilter;
    @Mock
    private List<RequestFilter> requestFilter;
    RecommendationRequestDto requestDto;
    RejectionDto rejectionDto;
    RecommendationRequest recommendationRequest;


    @BeforeEach
    void init() {
        requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(1L);
        requestDto.setSkillsId(Arrays.asList(1L, 2L, 3L));

        rejectionDto = new RejectionDto();
        rejectionDto.setReason("Отказано");

        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1);
        recommendationRequest.setRequester(new User());
        recommendationRequest.setReceiver(new User());
        recommendationRequest.setSkills(List.of(
                new SkillRequest(),
                new SkillRequest(),
                new SkillRequest()));

        requestFilterDto = new RequestFilterDto();

        idPatternFilter = Mockito.mock(RequestFilter.class);
    }

    @Test
    @DisplayName("testCreateWithFindByIdRequesterAndReceiver")
    public void testCreateWithFindByIdRequesterAndReceiver() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    @DisplayName("testCreateBelieveTheRequestToSendNotMoreThanSixMonths")
    public void testCreateBelieveTheRequestToSendNotMoreThanSixMonths() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        checkLastRequest(requestDto, LocalDateTime.now());

        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    @DisplayName("testCreateCheckTheExistenceOfSkillsInTheDatabase")
    public void testCreateCheckTheExistenceOfSkillsInTheDatabase() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        checkLastRequest(requestDto, LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
        when(skillRequestRepository.findAllById(requestDto.getSkillsId()))
                .thenReturn(Arrays.asList(new SkillRequest(), new SkillRequest()));

        assertThrows(NullPointerException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    @DisplayName("testCreateSave")
    public void testCreateSave() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        checkLastRequest(requestDto, LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
        when(skillRequestRepository.findAllById(requestDto.getSkillsId()))
                .thenReturn(Arrays.asList(
                        new SkillRequest(1L, null, null),
                        new SkillRequest(2L, null, null),
                        new SkillRequest(3L, null, null)));

        when(recommendationRequestMapper.toEntity(requestDto))
                .thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequest);

        recommendationRequestService.create(requestDto);

        verify(recommendationRequestMapper, times(1))
                .toEntity(requestDto);
        verify(recommendationRequestRepository, times(1))
                .save(recommendationRequest);
    }

    private void checkLastRequest(RecommendationRequestDto requestDto, LocalDateTime localDateTime) {
        recommendationRequest.setUpdatedAt(localDateTime);

        when(recommendationRequestRepository
                .findLatestPendingRequest(requestDto.getRequesterId(), requestDto.getRecieverId()))
                .thenReturn(Optional.of(recommendationRequest));
    }

    @Test
    @DisplayName("testGetRequestsFindAll")
    public void testGetRequestsFindAll() {
        when(statusPatternFilter.isApplication(any())).thenReturn(true);
        when(statusPatternFilter.apply(any(), any())).thenReturn(Stream.of(recommendationRequest));
        when(idPatternFilter.isApplication(any())).thenReturn(true);
        when(idPatternFilter.apply(any(), any())).thenReturn(Stream.of(recommendationRequest));
        when(requestFilter.stream()).thenReturn(Stream.of(statusPatternFilter, idPatternFilter));
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(new RecommendationRequest()));
        when(recommendationRequestMapper.toDto(any())).thenReturn(new RecommendationRequestDto());
        recommendationRequestService.getRequests(requestFilterDto);

        verify(recommendationRequestMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("testGetRequestFundRequest")
    public void testGetRequestFundRequest() {
        long id = 1;
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> recommendationRequestService.getRequest(id));
    }

    @Test
    @DisplayName("testGetRequestToDto")
    public void testGetRequestToDto() {
        long id = 1;
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toDto(recommendationRequest))
                .thenReturn(requestDto);
        recommendationRequestService.getRequest(id);
        verify(recommendationRequestMapper, times(1))
                .toDto(recommendationRequest);
    }

    @Test
    @DisplayName("testRejectRequestFindById")
    public void testRejectRequestFindById() {
        long id = 1;
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    @DisplayName("testRejectRequestGetStatus")
    public void testRejectRequestGetStatus() {
        long id = 1;
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    @DisplayName("testRejectRequestSave")
    public void testRejectRequestSave() {
        long id = 1;
        recommendationRequest.setStatus(RequestStatus.PENDING);

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(recommendationRequest));

        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest))
                .thenReturn(requestDto);
        recommendationRequestService.rejectRequest(id, rejectionDto);

        verify(recommendationRequestRepository, times(1))
                .save(recommendationRequest);
        verify(recommendationRequestMapper, times(1))
                .toDto(recommendationRequest);
    }
}