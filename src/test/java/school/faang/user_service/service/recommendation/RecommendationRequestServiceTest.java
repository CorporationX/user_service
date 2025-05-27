//package school.faang.user_service.service.recommendation;
//
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.RecommendationRejectDto;
//import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
//import school.faang.user_service.dto.recommendation.RejectionDto;
//import school.faang.user_service.dto.recommendation.RequestFilterDto;
//import school.faang.user_service.entity.RequestStatus;
//import school.faang.user_service.entity.Skill;
//import school.faang.user_service.entity.User;
//import school.faang.user_service.entity.recommendation.RecommendationRequest;
//import school.faang.user_service.entity.recommendation.SkillRequest;
//import school.faang.user_service.exception.DataValidationException;
//import school.faang.user_service.mapper.recommandation.RecommendationRequestMapperImpl;
//import school.faang.user_service.mapper.recommandation.RecommendationRequestRejectionMapperImpl;
//import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
//import school.faang.user_service.service.skil.SkillRequestService;
//import school.faang.user_service.service.skil.SkillService;
//import school.faang.user_service.service.user.UserService;
//
//import java.time.LocalDateTime;
//import java.time.Month;
//import java.util.*;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class RecommendationRequestServiceTest {
//    @InjectMocks
//    private RecommendationRequestService requestService;
//    @Mock
//    private RecommendationRequestRepository requestRepository;
//    @Mock
//    private UserService userService;
//    @Mock
//    private SkillService skillService;
//    @Mock
//    private SkillRequestService skillRequestService;
//    @Mock
//    private RecommendationRequestMapperImpl requestMapper;
//    @Mock
//    private RecommendationRequestRejectionMapperImpl rejectionMapper;
//
//    @Test
//    @DisplayName("testCreateWithUserExistence")
//    public void testCreateWithUserExistence() {
//        RecommendationRequestDto requestDto = getRequestDto();
//        when(userService.isUserExistByID(requestDto.getRequesterId())).thenReturn(false);
//
//        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
//                () -> requestService.create(requestDto));
//
//        assertTrue(dataValidationException.getMessage().contains("User not found in database"));
//    }
//
//    @Test
//    @DisplayName("testCreateWithRequestPeriodShort")
//    public void testCreateWithRequestPeriodShort() {
//        RecommendationRequestDto requestDto = getRequestDto();
//        RecommendationRequest existingRequest = new RecommendationRequest();
//        existingRequest.setCreatedAt(LocalDateTime.now().minusDays(60));
//
//        when(userService.isUserExistByID(requestDto.getRequesterId())).thenReturn(true);
//        when(userService.isUserExistByID(requestDto.getReceiverId())).thenReturn(true);
//        when(requestRepository.findLatestPendingRequest(1L, 2L))
//                .thenReturn(Optional.of(existingRequest));
//
//        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
//                () -> requestService.create(requestDto));
//
//        assertTrue(dataValidationException.getMessage().contains("Request period is too short"));
//    }
//
//    @Test
//    @DisplayName("testCreateWithSkillsExistence")
//    public void testCreateWithSkillsExistence() {
//        RecommendationRequest requestSaved = getRequestSaved();
//        RecommendationRequestDto requestDto = getRequestDto();
//        requestDto.getSkillsIds().add(5L);
//        List<Long> existSkillIds = List.of(1L, 2L);
//
//        when(userService.isUserExistByID(requestDto.getRequesterId())).thenReturn(true);
//        when(userService.isUserExistByID(requestDto.getReceiverId())).thenReturn(true);
//        when(requestRepository.findLatestPendingRequest(requestDto.getRequesterId(), requestDto.getReceiverId()))
//                .thenReturn(Optional.of(requestSaved));
//        when(skillService.findExistingSkills(requestDto.getSkillsIds())).thenReturn(existSkillIds);
//
//        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
//                () -> requestService.create(requestDto));
//
//        assertTrue(dataValidationException.getMessage().contains("Skills not found in database"));
//    }
//
//    @Test
//    @DisplayName("testIsCreateRequest")
//    public void testIsCreateRequest() {
//        RecommendationRequestDto requestDto = getRequestDto();
//        RecommendationRequest requestSaved = getRequestSaved();
//        RecommendationRequest requestEntity = getRequestEntity();
//        RecommendationRequestDto responseDto = getRequestDto();
//        responseDto.setId(1L);
//        List<Long> existSkillIds = List.of(1L);
//        Skill skill = Skill.builder().id(1L).build();
//        SkillRequest skillRequest = new SkillRequest(requestSaved, skill);
//        requestSaved.getSkills().add(skillRequest);
//
//        when(userService.isUserExistByID(anyLong())).thenReturn(true);
//        when(userService.isUserExistByID(anyLong())).thenReturn(true);
//        when(requestRepository.findLatestPendingRequest(anyLong(), anyLong())).thenReturn(Optional.of(requestSaved));
//        when(skillService.findExistingSkills(anyList())).thenReturn(existSkillIds);
//        when(requestMapper.toEntity(any())).thenReturn(requestEntity);
//        when(userService.getUserById(anyLong())).thenReturn(requestEntity.getRequester());
//        when(userService.getUserById(anyLong())).thenReturn(requestEntity.getReceiver());
//        when(requestRepository.save(any())).thenReturn(requestSaved);
//        when(skillService.findAllByIDs(anyList())).thenReturn(List.of(skill));
//        when(skillRequestService.create(any(), any())).thenReturn(skillRequest);
//        when(requestMapper.toDto(any())).thenReturn(responseDto);
//
//        RecommendationRequestDto result = requestService.create(requestDto);
//
//        verify(userService).getUserById(requestDto.getRequesterId());
//        verify(userService).getUserById(requestDto.getReceiverId());
//        verify(requestRepository).save(requestEntity);
//        verify(skillService).findAllByIDs(requestDto.getSkillsIds());
//        verify(skillRequestService).create(requestSaved, skill);
//
//        assertEquals(result, responseDto);
//        assertEquals(RequestStatus.PENDING, result.getStatus());
//        assertEquals(1, result.getSkillsIds().size());
//    }
//
//    @Test()
//    @DisplayName("testGetRequestsWithFilerSuccess")
//    public void testGetRequestsWithFilerSuccess() {
//        Filter<RequestFilterDto, RecommendationRequest> mockFilter = mock(Filter.class);
//        List<Filter<RequestFilterDto, RecommendationRequest>> filters = List.of(mockFilter);
//
//        requestService = new RecommendationRequestService(requestRepository, requestMapper, rejectionMapper, filters,
//                userService, skillRequestService, skillService);
//
//        RequestFilterDto filterDto = RequestFilterDto.builder().status(RequestStatus.PENDING).build();
//        List<RecommendationRequest> requests = List.of(
//                RecommendationRequest.builder().status(RequestStatus.PENDING).build(),
//                RecommendationRequest.builder().status(RequestStatus.REJECTED).build()
//        );
//        RecommendationRequestDto requestDto = RecommendationRequestDto.builder().status(RequestStatus.PENDING).build();
//        Stream<RecommendationRequest> requestStream = Stream.of(requests.get(0));
//
//        when(requestRepository.findAll()).thenReturn(requests);
//        when(filters.get(0).isApplicable(any())).thenReturn(true);
//        when(filters.get(0).apply(any(), any())).thenReturn(requestStream);
//        when(requestMapper.toDto(requests.get(0))).thenReturn(requestDto);
//
//        List<RecommendationRequestDto> result = requestService.getRequests(filterDto);
//        assertEquals(result.get(0).getStatus(), RequestStatus.PENDING);
//    }
//
//    @Test
//    @DisplayName("testGetRequestById")
//    public void testGetRequestById() {
//        RecommendationRequest requestSaved = getRequestSaved();
//        RecommendationRequestDto requestDto = getRequestDto();
//
//        when(requestRepository.findById(1L)).thenReturn(Optional.ofNullable(requestSaved));
//        when(requestMapper.toDto(requestSaved)).thenReturn(requestDto);
//
//        assertEquals(requestDto, requestService.getRequest(1L));
//    }
//
//    @Test
//    @DisplayName("testRejectRequestWithStatusPending")
//    public void testRejectRequestWithStatusNotPending() {
//        RecommendationRequest requestSaved = getRequestSaved();
//        requestSaved.setStatus(RequestStatus.REJECTED);
//        RejectionDto rejectionDto = RejectionDto.builder().reason("reason").status(RequestStatus.REJECTED).build();
//
//        when(requestRepository.findById(1L)).thenReturn(Optional.ofNullable(requestSaved));
//
//        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
//                () -> requestService.rejectRequest(1L, rejectionDto));
//
//        assertTrue(dataValidationException.getMessage().contains("Request cannot be rejected"));
//    }
//
//    @Test
//    @DisplayName("testRejectRequestSuccess")
//    public void testRejectRequestSuccess() {
//        RecommendationRequest requestSaved = getRequestSaved();
//        RecommendationRejectDto rejectionDto = new RecommendationRejectDto("reason");
//
//        when(requestRepository.findById(1L)).thenReturn(Optional.ofNullable(requestSaved));
//        when(requestRepository.save(requestSaved)).thenReturn(requestSaved);
//        when(rejectionMapper.toDto(requestSaved)).thenReturn(rejectionDto);
//
//        RejectionDto result = requestService.rejectRequest(1L, rejectionDto);
//
//        assertEquals(result, rejectionDto);
//    }
//
//    private RecommendationRequestDto getRequestDto() {
//        return RecommendationRequestDto.builder()
//                .message("message")
//                .status(RequestStatus.PENDING)
//                .requesterId(1L)
//                .receiverId(2L)
//                .skillsIds(new ArrayList<>(List.of(1L)))
//                .build();
//    }
//
//    private RecommendationRequest getRequestSaved() {
//        return RecommendationRequest.builder()
//                .id(1L)
//                .message("message")
//                .status(RequestStatus.PENDING)
//                .requester(new User())
//                .receiver(new User())
//                .skills(new ArrayList<>())
//                .createdAt(LocalDateTime.of(2024, Month.FEBRUARY, 1, 0, 0, 0))
//                .build();
//    }
//
//    private RecommendationRequest getRequestEntity() {
//        return RecommendationRequest.builder()
//                .requester(new User())
//                .receiver(new User())
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
//}