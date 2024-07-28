package school.faang.user_service.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.mentorship.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectRequestDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
public class MentorshipRequestServiceTest {
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper;

    private AcceptMentorshipRequestDto acceptMentorshipRequestDto;
    private RejectRequestDto rejectRequestDto;
    private MentorshipRequestDto mentorshipRequestDto;
    private Long userId;
    private MentorshipRequest mentorshipRequest;
    private Mentorship mentorship;
    private List<MentorshipRequest> resultQuery;
    private List<MentorshipRequestDto> resultList;
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
        resultList = resultQuery.stream().map(mentorshipRequestMapper::toDto).toList();

        mentorship = new Mentorship();
        mentorship.setId(1L);
        mentorship.setMentee(new User());
        mentorship.setMentor(new User());

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setDescription("Description");
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(new User());

        acceptMentorshipRequestDto = new AcceptMentorshipRequestDto();
        acceptMentorshipRequestDto.setId(1L);
        acceptMentorshipRequestDto.setRequesterId(1L);
        acceptMentorshipRequestDto.setReceiverId(2L);
        userId = 1L;

        rejectRequestDto = new RejectRequestDto();
        rejectRequestDto.setId(1L);
        rejectRequestDto.setRejectReason("No reason just wish!!!");

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setDescription("Request");
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        mentorshipRequestDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testAcceptRequest() {
        when(mentorshipRequestRepository.findById(acceptMentorshipRequestDto.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRepository.getLastMentorship(acceptMentorshipRequestDto.getReceiverId(),
                acceptMentorshipRequestDto.getRequesterId()))
                .thenReturn(Optional.empty());
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);

        mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
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
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);

        mentorshipRequestService.rejectRequest(rejectRequestDto);

        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals(rejectRequestDto.getRejectReason(), mentorshipRequest.getRejectionReason());
    }
    @Test
    public void testGetAllMentorshipRequestsWithDescriptionFilter() {
        when(mentorshipRequestRepository.findAll()).thenReturn(resultQuery);
        when(mentorshipRequestMapper.toDtoList(getFilteredRequestEntities(resultQuery))).thenReturn(getResultList());
        List<MentorshipRequestDto> result = mentorshipRequestService.getAllMentorshipRequests(filterDto);
        assertNotNull(result);
        assertNotEquals(result.size(), resultQuery.size());
    }




    public void testRequestMentorship() {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(false);
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


        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository).create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
        assertEquals(mentorshipRequestDto,result);

    }
    private List<MentorshipRequest> getFilteredRequestEntities(List<MentorshipRequest> list) {
        return list.stream().filter(el->el.getDescription().contains("Java")).toList();
    }
    private List<MentorshipRequestDto> getResultList(){
        MentorshipRequestDto first = new MentorshipRequestDto();
        first.setId(1L);
        first.setRequesterId(1L);
        first.setReceiverId(2L);
        first.setDescription("Java");
        first.setCreatedAt(LocalDateTime.now());
        first.setUpdatedAt(LocalDateTime.now());

        return List.of(first);
    }
}
