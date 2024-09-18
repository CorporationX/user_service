package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filters.RequestFilter;
import school.faang.user_service.service.filters.RequestFilterByDescription;
import school.faang.user_service.service.filters.RequestFilterByReceiver;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService service;

    @Mock
    private MentorshipRequestRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private List<RequestFilter> requestFilters;

    @Spy
    private MentorshipRequestMapperImpl mapper;


    @Test
    @DisplayName("Create mentorshipRequest: check if description is blank")
    public void testMentorshipRequestDescriptionIsBlank() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription(" ");
        dto.setStatus("PENDING");
        MentorshipRequest mentorshipRequest = mapper.toEntity(dto);

        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(mentorshipRequest));
    }

    @Test
    @DisplayName("Create mentorshipRequest: check if receiver and requester are identical")
    public void testMentorshipRequestRequesterReceiverIdAreIdentical() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test description");
        dto.setStatus("PENDING");
        dto.setUserRequesterId(1L);
        dto.setUserReceiverId(1L);
        MentorshipRequest mentorshipRequest = mapper.toEntity(dto);

        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(mentorshipRequest));
    }

    @Test
    @DisplayName("Create mentorshipRequest: check if the requester exist")
    public void testMentorshipRequestExistRequester() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test description");
        dto.setStatus("PENDING");
        dto.setUserRequesterId(1L);
        dto.setUserReceiverId(2L);
        MentorshipRequest mentorshipRequest = mapper.toEntity(dto);

        Mockito.when(userRepository.existsById(dto.getUserRequesterId()))
                .thenReturn(false);


        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(mentorshipRequest));
    }

    @Test
    @DisplayName("Create mentorshipRequest: check if the receiver exist")
    public void testMentorshipRequestExistReceiver() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test description");
        dto.setStatus("PENDING");
        dto.setUserRequesterId(1L);
        dto.setUserReceiverId(2L);
        MentorshipRequest mentorshipRequest = mapper.toEntity(dto);

        Mockito.when(userRepository.existsById(dto.getUserRequesterId()))
                .thenReturn(true);
        Mockito.when(userRepository.existsById(dto.getUserReceiverId()))
                .thenReturn(false);

        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(mentorshipRequest));
    }

    @Test
    @DisplayName("Create mentorshipRequest: check if latest request less then 3 month")
    public void testMentorshipRequestFindLatestRequest() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test description");
        dto.setUserRequesterId(1L);
        dto.setUserReceiverId(2L);
        dto.setStatus("PENDING");
        dto.setCreatedAt(LocalDateTime.of(2024, Month.OCTOBER, 9, 15, 30));

        MentorshipRequest request = mapper.toEntity(dto);

        Mockito.when(userRepository.existsById(dto.getUserRequesterId()))
                .thenReturn(true);
        Mockito.when(userRepository.existsById(dto.getUserReceiverId()))
                .thenReturn(true);

        Mockito.when(repository.findLatestRequest(dto.getUserRequesterId(), dto.getUserReceiverId()))
                .thenReturn(Optional.of(request));

        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(request));
    }

    @Test
    @DisplayName("Create mentorshipRequest")
    public void testRepositoryCreate() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test description");
        dto.setUserRequesterId(1L);
        dto.setUserReceiverId(2L);

        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(new MentorshipRequest()));

        repository.create(dto.getUserRequesterId(), dto.getUserReceiverId(), "test");

        Mockito.verify(repository, Mockito.times(1))
                .create(1L, 2L, "test");
    }

    @Test
    @DisplayName("Filters mentorshipRequest")
    public void testGetRequestsByFilter() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setDescription("test description");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setDescription("test description");
        Mockito.when(repository.findAll()).thenReturn(List.of(mentorshipRequest));

        Mockito.when(requestFilters.stream()).thenReturn(Stream.of(
                new RequestFilterByDescription(),
                new RequestFilterByReceiver()
        ));

        List<MentorshipRequest> requestsByFilter = service.getRequests(filterDto);

        assertThat(mentorshipRequest)
                .usingRecursiveAssertion()
                .isEqualTo(requestsByFilter.get(0));
    }

    @Test
    @DisplayName("Accept: check if mentorshipRequest id is null")
    public void testAcceptRequestCheckIdIsNull() {
        Assert.assertThrows(RuntimeException.class, () -> service.acceptRequest(null));
    }

    @Test
    @DisplayName("Accept: check if mentorshipRequest exist")
    public void testAcceptRequestExist() {
        Long requestId = 1L;
        Mockito.when(repository.findById(requestId)).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> service.acceptRequest(requestId));
    }

    @Test
    @DisplayName("Accept")
    public void testAcceptRequest() {
        Long requestId = 1L;

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        Mockito.when(repository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        service.acceptRequest(requestId);

        Mockito.verify(repository, Mockito.times(1))
                .save(mentorshipRequest);

        assertNotNull(mentorshipRequest);
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
    }

    @Test
    @DisplayName("Reject: check if mentorshipRequest id is null")
    public void testRejectRequestCheckIdIsNull() {
        long id = 1;
        String reason = "Test reason";

        Assert.assertThrows(RuntimeException.class, () -> service.rejectRequest(id, reason));
    }

    @Test
    @DisplayName("Reject: check if mentorshipRequest exist")
    public void testRejectRequestExist() {
        long id = 1;
        String reason = "test reason";

        Mockito.when(repository.findById(id)).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> service.rejectRequest(id, reason));
    }

    @Test
    @DisplayName("Reject")
    public void testRejectRequest() {
        long id = 1;

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1);
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason("test reason");
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(mentorshipRequest));

        service.rejectRequest(id, "test reason");

        Mockito.verify(repository, Mockito.times(1))
                .save(mentorshipRequest);

        assertNotNull(mentorshipRequest);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
    }
}
