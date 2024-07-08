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
    private List<RequestFilter> filters;
    @Mock
    private RequestFilter idPatternFilter;

    @Mock
    private StatusPatternFilter statusPatternFilter;


    @BeforeEach
    void init() {
//        RecommendationRequestRepository recommendationRequestRepositoryMock = Mockito.mock(RecommendationRequestRepository.class);
//        RecommendationRequestMapper recommendationRequestMapperMock = Mockito.mock(RecommendationRequestMapper.class);
//        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
//        SkillRepository skillRepositoryMock = Mockito.mock(SkillRepository.class);
//        SkillRequestRepository skillRequestRepositoryMock = Mockito.mock(SkillRequestRepository.class);
        idPatternFilter = Mockito.mock(RequestFilter.class);
        requestFilterMock = Mockito.mock(RequestFilter.class);
        filters = List.of(idPatternFilter, requestFilterMock);
//        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepositoryMock,
//                recommendationRequestMapperMock,
//                userRepositoryMock,
//                skillRepositoryMock,
//                skillRequestRepositoryMock,
//                filters);
//        when(filters.get(0).isApplication(new RequestFilterDto(1L, null))).thenReturn(true);
//        when(filters.get(0).apply(any(Stream.class), any(RequestFilterDto.class))).thenReturn(Stream)
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
//        List<RecommendationRequest> resultList = new ArrayList<>();
//        RecommendationRequest t1 = new RecommendationRequest();
////        RecommendationRequest t2 = new RecommendationRequest();
//        t1.setId(1L);
////        t2.setId(2L);
//        t1.setStatus(RequestStatus.ACCEPTED);
////        t2.setStatus(RequestStatus.ACCEPTED);
//        resultList.add(t1);
////        resultList.add(t2);
//
//        List<RecommendationRequest> otherList = new ArrayList<>();
//        RecommendationRequest r1 = new RecommendationRequest();
//        RecommendationRequest r2 = new RecommendationRequest();
//        RecommendationRequest r3 = new RecommendationRequest();
//        r1.setId(1L);
//        r2.setId(2L);
//        r3.setId(3L);
//        r1.setStatus(RequestStatus.ACCEPTED);
//        r2.setStatus(RequestStatus.ACCEPTED);
//        r3.setStatus(RequestStatus.PENDING);
//        otherList.add(r1);
//        otherList.add(r2);
//        otherList.add(r3);
////
//        List<RecommendationRequestDto> requestDtoList = new ArrayList<>();
//        RecommendationRequestDto p1 = new RecommendationRequestDto();
////        RecommendationRequestDto p2 = new RecommendationRequestDto();
//        p1.setId(1L);
////        p2.setId(2L);
//        p1.setStatus(RequestStatus.ACCEPTED);
////        p2.setStatus(RequestStatus.ACCEPTED);
//        requestDtoList.add(p1);
////        requestDtoList.add(p2);
////
////        requestFilterDto = new RequestFilterDto(null, RequestStatus.ACCEPTED);
////
////        when(recommendationRequestRepository.findAll()).thenReturn(otherList);
////        when(statusPatternFilter.isApplication(requestFilterDto)).thenReturn(true);
////        when(statusPatternFilter.apply(otherList.stream(), requestFilterDto)).thenReturn(resultList.stream());
////        when(recommendationRequestMapper.toDtos(resultList)).thenReturn(requestDtoList);
////        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(requestFilterDto);
////        assertNotNull(result);
//        RequestFilterDto r = new RequestFilterDto(1L, null);
//        when(recommendationRequestRepository.findAll()).thenReturn(otherList);
//        when(filters.get(0).isApplication(r)).thenReturn(true);
//        when(filters.get(0).apply(otherList.stream(), r)).thenReturn(resultList.stream());
//        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class))).thenReturn(any(RecommendationRequestDto.class));
////        when(recommendationRequestMapper.toDto(resultList)).thenReturn(requestDtoList);
//        recommendationRequestService.getRequests(r);
//
//        verify(requestFilterMock, times(1)).isApplication(any(RequestFilterDto.class));
//        verify(requestFilterMock, times(1)).apply(any(Stream.class), any(RequestFilterDto.class));
//        verify(recommendationRequestMapper, times(resultList.size())).toDto(new  RecommendationRequest());

        List<RecommendationRequest> allRequests = new ArrayList<>();
        RecommendationRequest r1 = new RecommendationRequest();
        RecommendationRequest r2 = new RecommendationRequest();
        RecommendationRequest r3 = new RecommendationRequest();
        r1.setId(1L);
        r2.setId(2L);
        r3.setId(3L);
        r1.setStatus(RequestStatus.ACCEPTED);
        r2.setStatus(RequestStatus.ACCEPTED);
        r3.setStatus(RequestStatus.PENDING);
        allRequests.add(r1);
        allRequests.add(r2);
        allRequests.add(r3);

        List<RecommendationRequest> filteredRequests = allRequests.stream()
                .filter(req -> req.getId() == 2L)
                .filter(req -> req.getStatus().equals(RequestStatus.ACCEPTED)).toList();

        List<RecommendationRequestDto> requestDtoList = filteredRequests.stream()
                .map(req -> {
                    RecommendationRequestDto dto = new RecommendationRequestDto();
                    dto.setId(req.getId());
                    dto.setStatus(req.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());

        when(recommendationRequestRepository.findAll()).thenReturn(allRequests);
        when(statusPatternFilter.isApplication(requestFilterDto)).thenReturn(true);
        when(statusPatternFilter.apply(any(), eq(requestFilterDto))).thenAnswer(invocation -> {
            Stream<RecommendationRequest> stream = invocation.getArgument(0);
            return stream.filter(req -> req.getStatus().equals(requestFilterDto.getStatusPattern()));
        });
        when(idPatternFilter.isApplication(requestFilterDto)).thenReturn(true);
        when(idPatternFilter.apply(any(), eq(requestFilterDto))).thenAnswer(invocation -> {
            Stream<RecommendationRequest> stream = invocation.getArgument(0);
            return stream.filter(req -> req.getId() ==(requestFilterDto.getIdPattern()));
        });
        when(recommendationRequestMapper.toDto(any())).thenAnswer(invocation -> {
            RecommendationRequest request = invocation.getArgument(0);
            RecommendationRequestDto dto = new RecommendationRequestDto();
            dto.setId(request.getId());
            dto.setStatus(request.getStatus());
            return dto;
        });

        List<RecommendationRequestDto> requestDtos = recommendationRequestService.getRequests(requestFilterDto);

        assertNotNull(requestDtos);
        assertEquals(1, requestDtos.size());
        assertEquals(requestDtoList, requestDtos);

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(statusPatternFilter, times(1)).isApplication(requestFilterDto);
        verify(statusPatternFilter, times(1)).apply(any(), eq(requestFilterDto));
        verify(idPatternFilter, times(1)).isApplication(requestFilterDto);
        verify(idPatternFilter, times(1)).apply(any(), eq(requestFilterDto));
        verify(recommendationRequestMapper, times(1)).toDto(any());

        // Verify that the correct list is passed to toDto
        ArgumentCaptor<RecommendationRequest> argumentCaptor = ArgumentCaptor.forClass(RecommendationRequest.class);
        verify(recommendationRequestMapper, times(1)).toDto(argumentCaptor.capture());
        List<RecommendationRequest> capturedList = argumentCaptor.getAllValues();
        assertEquals(1, capturedList.size());
        assertEquals(filteredRequests.get(0).getId(), capturedList.get(0).getId());

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