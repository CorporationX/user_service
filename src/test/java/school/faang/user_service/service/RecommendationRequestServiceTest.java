package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filer.IdPatternFilter;
import school.faang.user_service.service.filer.RequestFilter;
import school.faang.user_service.service.filer.StatusPatternFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
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
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RequestFilter requestFilterMock;
    @Mock
    private RequestFilter idPatternFilter;
    @Mock
    private StatusPatternFilter statusPatternFilter;
    @Mock
    private List<RequestFilter> requestFilter;
    @Mock
    private RecommendationRequest recommendationRequest;


    @BeforeEach
    void init() {
        idPatternFilter = Mockito.mock(RequestFilter.class);
    }

    @Test
    @DisplayName("testCreateWithFindByIdRequesterAndReceiver")
    public void testCreateWithFindByIdRequesterAndReceiver() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    @DisplayName("testCreateBelieveTheRequestToSendNotMoreThanSixMonths")
    public void testCreateBelieveTheRequestToSendNotMoreThanSixMonths() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(1L);

        when(userRepository.findById(requestDto.getRequesterId()))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(requestDto.getRecieverId()))
                .thenReturn(Optional.of(new User()));

        checkLastRequest(requestDto, LocalDateTime.now());

        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    @DisplayName("testCreateCheckTheExistenceOfSkillsInTheDatabase")
    public void testCreateCheckTheExistenceOfSkillsInTheDatabase() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(1L);
        requestDto.setSkillsId(Arrays.asList(1L, 2L, 3L));

        when(userRepository.findById(requestDto.getRequesterId()))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(requestDto.getRecieverId()))
                .thenReturn(Optional.of(new User()));

        checkLastRequest(requestDto, LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
        when(skillRepository.findAllById(requestDto.getSkillsId()))
                .thenReturn(Arrays.asList(new Skill(), new Skill()));

        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    @DisplayName("testCreateSave")
    public void testCreateSave() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(1L);
        requestDto.setSkillsId(Arrays.asList(1L, 2L, 3L));

        when(userRepository.findById(requestDto.getRequesterId()))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(requestDto.getRecieverId()))
                .thenReturn(Optional.of(new User()));

        checkLastRequest(requestDto, LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
        when(skillRepository.findAllById(requestDto.getSkillsId()))
                .thenReturn(Arrays.asList(
                        new Skill(1L, null, null, null, null, null, null, null),
                        new Skill(2L, null, null, null, null, null, null, null),
                        new Skill(3L, null, null, null, null, null, null, null)));

        RecommendationRequest request = new RecommendationRequest();
        request.setRequester(new User());
        request.setReceiver(new User());
        request.setSkills(List.of(
                new SkillRequest(),
                new SkillRequest(),
                new SkillRequest()));

        when(recommendationRequestMapper.toEntity(requestDto))
                .thenReturn(request);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(request);

        recommendationRequestService.create(requestDto);
        // проверить DTO и Entity (по полям)
        verify(recommendationRequestMapper, times(1))
                .toEntity(requestDto);
        verify(recommendationRequestRepository, times(1))
                .save(request);
    }

    private void checkLastRequest(RecommendationRequestDto requestDto, LocalDateTime localDateTime) {
        RecommendationRequest request = new RecommendationRequest();
        request.setUpdatedAt(localDateTime);

        when(recommendationRequestRepository
                .findLatestPendingRequest(requestDto.getRequesterId(), requestDto.getRecieverId()))
                .thenReturn(Optional.of(request));
    }

    @Test
    @DisplayName("testGetRequestsFindAll")
    public void testGetRequestsFindAll() {
        when(statusPatternFilter.isApplication(any())).thenReturn(true);
        when(statusPatternFilter.apply(any(), any())).thenReturn(Stream.of(recommendationRequest));
        when(idPatternFilter.isApplication(any())).thenReturn(true);
        when(idPatternFilter.apply(any(), any())).thenReturn(Stream.of(new RecommendationRequest()));
        when(requestFilter.stream()).thenReturn(Stream.of(statusPatternFilter, idPatternFilter));
        List<RecommendationRequest> resultList = new ArrayList<>();
        RecommendationRequest t1 = new RecommendationRequest();
        t1.setId(1L);
        t1.setStatus(RequestStatus.ACCEPTED);
        resultList.add(t1);

        RequestFilterDto r = new RequestFilterDto(1L, null);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(new RecommendationRequest()));
        when(recommendationRequestMapper.toDto(any())).thenReturn(new RecommendationRequestDto());
        recommendationRequestService.getRequests(r);

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
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setId(1);
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1);
        long id = 1;
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(request));
        when(recommendationRequestMapper.toDto(request))
                .thenReturn(requestDto);
        // Проверка, что все поля не пустые
        recommendationRequestService.getRequest(id);
        verify(recommendationRequestMapper, times(1))
                .toDto(request);
    }

    @Test
    @DisplayName("testRejectRequestFindById")
    public void testRejectRequestFindById() {
        long id = 1;
        RejectionDto rejectionDto = new RejectionDto();
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    @DisplayName("testRejectRequestGetStatus")
    public void testRejectRequestGetStatus() {
        long id = 1;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Отказано");
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.ACCEPTED);

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(request));

        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    @DisplayName("testRejectRequestSave")
    public void testRejectRequestSave() {
        long id = 1;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Отказано");
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.PENDING);
        RecommendationRequestDto requestDto = new RecommendationRequestDto();

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(request));

        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(request);
        when(recommendationRequestMapper.toDto(request))
                .thenReturn(requestDto);
        recommendationRequestService.rejectRequest(id, rejectionDto);

        verify(recommendationRequestRepository, times(1))
                .save(request);
        verify(recommendationRequestMapper, times(1))
                .toDto(request);
    }
}