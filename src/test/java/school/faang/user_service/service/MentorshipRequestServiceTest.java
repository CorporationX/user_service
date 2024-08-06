package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.dto.mentorship.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectRequestDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = new MentorshipRequestMapperImpl();

    private AcceptMentorshipRequestDto acceptMentorshipRequestDto;
    private RejectRequestDto rejectRequestDto;
    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequest mentorshipRequest;
    private Mentorship mentorship;
    private List<MentorshipRequest> resultQuery;
    private MentorshipRequestFilterDto filterDto;
    @Value("${variables.interval}")
    private long interval;

    @BeforeEach
    public void setUp() {
        filterDto = new MentorshipRequestFilterDto();
        filterDto.setDescription("Java");
        MentorshipRequest firstRequest = MentorshipRequest
                .builder()
                .id(1L).requester(new User()).receiver(new User()).description("Java")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
        MentorshipRequest secondRequest = MentorshipRequest
                .builder()
                .id(2L).requester(new User()).receiver(new User()).description("Python")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
        resultQuery = List.of(firstRequest, secondRequest);

        mentorship = Mentorship.builder().id(1L).mentee(new User()).mentor(new User()).build();

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setDescription("Description");
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(new User());

        acceptMentorshipRequestDto = new AcceptMentorshipRequestDto();
        acceptMentorshipRequestDto.setId(1L);
        acceptMentorshipRequestDto.setRequesterId(1L);
        acceptMentorshipRequestDto.setReceiverId(2L);

        rejectRequestDto = new RejectRequestDto();
        rejectRequestDto.setId(1L);
        rejectRequestDto.setRejectReason("No reason just wish!!!");

        mentorshipRequestDto = MentorshipRequestDto.builder()
                .description("Request")
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testAcceptRequest() {

        when(mentorshipRequestRepository.findById(acceptMentorshipRequestDto.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        when(mentorshipRepository.getLastMentorship(acceptMentorshipRequestDto.getReceiverId(),
                acceptMentorshipRequestDto.getRequesterId()))
                .thenReturn(Optional.empty());

        when(mentorshipRepository
                .create(acceptMentorshipRequestDto.getReceiverId(),
                        acceptMentorshipRequestDto.getRequesterId())).thenReturn(mentorship);
        MentorshipRequestDto result = mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);

        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
    }

    @Test
    @DisplayName("test accept request with req not found")
    public void testAcceptRequestWithRequestNotFound() {
        when(mentorshipRequestRepository.findById(acceptMentorshipRequestDto.getId()))
                .thenThrow(new EntityNotFoundException("Mentorship request not found!"));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);
        });
        assertEquals("Mentorship request not found!", exception.getMessage());
    }

    @Test
    @DisplayName("test accept request with already accepted request")
    public void testAcceptRequestWithAlreadyAcceptedRequest() {
        when(mentorshipRequestRepository.findById(acceptMentorshipRequestDto.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRepository.getLastMentorship(acceptMentorshipRequestDto.getReceiverId(),
                acceptMentorshipRequestDto.getRequesterId()))
                .thenReturn(Optional.of(mentorship));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);
        });
        assertEquals("You have already accepted request!!", exception.getMessage());
    }

    @Test
    public void testRejectRequest() {
        when(mentorshipRequestRepository.findById(rejectRequestDto.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(rejectRequestDto);

        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals(rejectRequestDto.getRejectReason(), mentorshipRequest.getRejectionReason());
    }

    @Test
    public void testRejectRequestWithNotFoundError() {
        when(mentorshipRequestRepository.findById(rejectRequestDto.getId()))
                .thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            mentorshipRequestService.rejectRequest(rejectRequestDto);
        });
        assertEquals("Mentorship request not found!",exception.getMessage());
        verify(mentorshipRequestRepository,times(0)).save(mentorshipRequest);
    }

    @Test
    public void testGetAllMentorshipRequestsWithDescriptionFilter() {
        when(mentorshipRequestRepository.findAll()).thenReturn(resultQuery);
        when(mentorshipRequestMapper.toDtoList(getFilteredRequestEntities(resultQuery))).thenReturn(getResultList());
        List<MentorshipRequestDto> result = mentorshipRequestService.getAllMentorshipRequests(filterDto);
        assertNotNull(result);
        assertNotEquals(result.size(), resultQuery.size());
    }

    @Test
    public void testRequestMentorship() {
        when(mentorshipRequestRepository
                .findFirstByRequesterIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getCreatedAt().minusMonths(interval)
                )).thenReturn(Optional.empty());
        when(mentorshipRequestRepository
                .findLatestRequest(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId()))
                .thenReturn(Optional.empty());


        when(mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription()))
                .thenReturn(mentorshipRequest);

        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);
        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        assertEquals(mentorshipRequestDto, result);

    }

    private List<MentorshipRequest> getFilteredRequestEntities(List<MentorshipRequest> list) {
        return list.stream().filter(el -> el.getDescription().contains("Java")).toList();
    }

    private List<MentorshipRequestDto> getResultList() {
        MentorshipRequestDto first = MentorshipRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .description("Java")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return List.of(first);
    }
}