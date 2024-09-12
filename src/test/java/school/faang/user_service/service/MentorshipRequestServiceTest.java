package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.AcceptationDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filters.RequestFilter;
import school.faang.user_service.service.filters.RequestFilterByDescription;
import school.faang.user_service.service.filters.RequestFilterByReceiver;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
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
    public void testMentorshipRequestDescriptionIsBlank() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription(" ");
        dto.setStatus("PENDING");
        MentorshipRequest mentorshipRequest = mapper.toEntity(dto);

        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(mentorshipRequest));
    }

    @Test
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
    public void testMentorshipRequestFindLatestRequest() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test description");
        dto.setUserRequesterId(2L);
        dto.setUserReceiverId(3L);
        dto.setStatus("PENDING");
        dto.setCreatedAt(LocalDateTime.of(2024, Month.OCTOBER, 9, 15, 30));
        Mockito.when(userRepository.existsById(dto.getUserRequesterId()))
                .thenReturn(true);
        Mockito.when(userRepository.existsById(dto.getUserReceiverId()))
                .thenReturn(true);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setCreatedAt(LocalDateTime.of(2024, Month.SEPTEMBER, 9, 15, 30));

        Mockito.when(repository.findLatestRequest(dto.getUserRequesterId(), dto.getUserReceiverId()))
                .thenReturn(Optional.of(request));

        Assert.assertThrows(RuntimeException.class, () -> service.requestMentorship(request));
    }

    @Test
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
    public void testGetRequestsByFilter() {
//        RequestFilterDto filterDto = new RequestFilterDto();
//        filterDto.setDescription("test description");
//
//        MentorshipRequest mentorshipRequest = new MentorshipRequest();
//        mentorshipRequest.setDescription("test description");
//        //List<RequestFilter> filters = List.of(new RequestFilterByDescription());
//
//        Mockito.when(repository.findAll()).thenReturn(List.of(mentorshipRequest));
//
//        Mockito.when(requestFilters.stream()).thenReturn(Stream.of(
//                new RequestFilterByDescription(),
//                new RequestFilterByReceiver()
//        ));
//
//        List<RequestFilterDto> requestsByFilter = service.getRequests(filterDto);
//
//        Assert.assertEquals(requestsByFilter.get(0).getDescription(), "test description");
    }

    @Test
    public void testAcceptRequestCheckIdIsNull() {
        AcceptationDto acceptDto = new AcceptationDto();

        Assert.assertThrows(RuntimeException.class, () -> service.acceptRequest(acceptDto));
    }

    @Test
    public void testAcceptRequestExist() {
        AcceptationDto acceptDto = new AcceptationDto();
        acceptDto.setRequestId(1L);

        Mockito.when(repository.findById(acceptDto.getRequestId())).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> service.acceptRequest(acceptDto));
    }

    @Test
    public void testAcceptRequest() {
        AcceptationDto acceptDto = new AcceptationDto();
        acceptDto.setRequestId(1L);
        acceptDto.setStatus(RequestStatus.ACCEPTED.toString());

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        Mockito.when(repository.findById(acceptDto.getRequestId())).thenReturn(Optional.of(mentorshipRequest));

        Assertions.assertEquals(mapper.toAcceptDto(mentorshipRequest), acceptDto);

        repository.create(1, 2, "test");

        Mockito.verify(repository, Mockito.times(1))
                .create(1L, 2L, "test");
    }

    @Test
    public void testRejectRequestCheckIdIsNull() {
        RejectionDto rejectionDto = new RejectionDto();

        Assert.assertThrows(RuntimeException.class, () -> service.rejectRequest(rejectionDto));
    }

    @Test
    public void testRejectRequestExist() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRequestId(1L);

        Mockito.when(repository.findById(rejectionDto.getRequestId())).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> service.rejectRequest(rejectionDto));
    }

    @Test
    public void testRejectRequest() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRequestId(1L);
        rejectionDto.setStatus(RequestStatus.REJECTED.toString());
        rejectionDto.setReason("I don't like you!");

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1);
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason("I don't like you!");
        Mockito.when(repository.findById(rejectionDto.getRequestId())).thenReturn(Optional.of(mentorshipRequest));

        Assertions.assertEquals(mapper.toRejectDto(mentorshipRequest), rejectionDto);

        repository.create(1, 2, "test");

        Mockito.verify(repository, Mockito.times(1))
                .create(1L, 2L, "test");
    }
}
