package school.faang.user_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.dto_mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;


public class TestMentorshipRequestService {
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    private AutoCloseable autoCloseable;
    private MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();

    @Captor
    private ArgumentCaptor<MentorshipRequest> argumentCaptor;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
       }

    @Test
    public void testRequestMentorshipGetDescriptionNotNull() {
        prepareData(null);
    }

    @Test
    public void testRequestMentorshipWithExistingDescription() {
        prepareData("  ");
    }

    private void prepareData(String string) {
        mentorshipRequestDto.setDescription(string);

        assertThrows(NoSuchElementException.class,
                () -> mentorshipRequestService.mentorshipRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testRequestMentorshipFindByIdRequester() {
        when(userRepository.findById(mentorshipRequestDto.getRequesterId())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () ->  mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testGivenNonExistReceiverWhenFindByIdThenThrowException() {
        mentorshipRequestDto.setDescription("Description");

        when(userRepository.findById(mentorshipRequestDto.getRequesterId())).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(mentorshipRequestDto.getReceiverId())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () ->  mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCheckingForIdenticalIdsUsers() {
        User user1 = new User();
        User user2 = new User();
        user1.setId(1l);
        user2.setId(1l);
        MentorshipRequest requestEntity = mock(MentorshipRequest.class);

        when(requestEntity.getRequester()).thenReturn(user1);
        when(requestEntity.getReceiver()).thenReturn(user2);

        assertThrows(NoSuchElementException.class, () ->
                        mentorshipRequestService.checkingForIdenticalIdsUsers(requestEntity));

    }

    @Test
    public void testCheckingForIdenticalIdsUsersDoesNotThrowException() {
        User user1 = new User();
        User user2 = new User();
        user1.setId(1l);
        user2.setId(2l);
        MentorshipRequest requestEntity = mock(MentorshipRequest.class);

        when(requestEntity.getRequester()).thenReturn(user1);
        when(requestEntity.getReceiver()).thenReturn(user2);

        assertDoesNotThrow(() ->
                        mentorshipRequestService.checkingForIdenticalIdsUsers(requestEntity));
    }

    @Test
    public void testSpamCheckNoRequestWithinThreeMonths() {
        mentorshipRequestDto.setRequesterId(1L);

        MentorshipRequest oldRequest = new MentorshipRequest();
        oldRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.singletonList(oldRequest));
        assertDoesNotThrow(() -> mentorshipRequestService.spamCheck(mentorshipRequestDto));
    }

    @Test
    public void testSpamCheckRequestWithinThreeMonths() {
        mentorshipRequestDto.setRequesterId(1L);

        MentorshipRequest recentRequest = new MentorshipRequest();
        recentRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.singletonList(recentRequest));
        assertThrows(RuntimeException.class, () -> mentorshipRequestService.spamCheck(mentorshipRequestDto));
    }

    @Test
    public void testSpamCheckNoRequestsFound() {
        mentorshipRequestDto.setRequesterId(1L);

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> mentorshipRequestService.spamCheck(mentorshipRequestDto));
    }

    @Test
    public void testGetRequestsWithNullFilter() {
        List<MentorshipRequest> requests = new ArrayList<>();
        MentorshipRequest firstRequest = new MentorshipRequest();
        MentorshipRequest secondRequest = new MentorshipRequest();
        firstRequest.setId(1l);
        secondRequest.setId(2l);
        requests.add(firstRequest);
        requests.add(secondRequest);

        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        List<MentorshipRequest> result = mentorshipRequestService.getRequests(null);
        assertEquals(requests, result);
    }

    @Test
    public void testGetRequestsWithFilter() {
        List<MentorshipRequest> requests = new ArrayList<>();
        MentorshipRequest firstRequest = new MentorshipRequest();
        MentorshipRequest secondRequest = new MentorshipRequest();
        firstRequest.setId(1l);
        firstRequest.setDescription("First description");
        secondRequest.setId(2l);
        secondRequest.setDescription("Second description");
        requests.add(firstRequest);
        requests.add(secondRequest);

        RequestFilterDto filter = new RequestFilterDto();
        filter.setDescription("First");

        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        List<MentorshipRequest> result = mentorshipRequestService.getRequests(filter);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getDescription().contains("First"));
    }

//    @Test
//    public void testGetRequests_WithNoMatchingFilter() {
//        // Arrange
//        List<MentorshipRequest> requests = new ArrayList<>();
//        requests.add(new MentorshipRequest("Request 1"));
//        requests.add(new MentorshipRequest("Request 2"));
//        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
//
//        RequestFilterDto filter = new RequestFilterDto();
//        filter.setDescription("Not Found");
//
//        // Act
//        List<MentorshipRequest> result = mentorshipService.getRequests(filter);
//
//        // Assert
//        assertTrue(result.isEmpty());
//    }


    @AfterEach
    void tearDone() {
        try {
            autoCloseable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
