package school.faang.user_service.service.mentorship;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    private MentorshipRequestDto mentorshipRequestDto;

    @Mock
    private MentorshipRequest mentorshipRequest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilters;


    private User receiver;

    private User requester;


    private RequestFilterDto requestFilterDto;


    @BeforeEach
    public void init() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequester(1L);
        mentorshipRequestDto.setDescription("Description");
        mentorshipRequestDto.setRequester(88L);
        mentorshipRequestDto.setReceiver(77L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setDescriptionFilter("Description filter");
        mentorshipRequest = new MentorshipRequest();
        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("Ivan");
        requester = new User();
        requester.setId(1L);
        requester.setUsername("John");
    }

    @Test
    public void testReturnAllMentorshipRequests() {
        Mockito.when(mentorshipRequestMapper.toDtoList(List.of(mentorshipRequest)))
                .thenReturn(List.of(mentorshipRequestDto));
        Mockito.when(mentorshipRequestRepository.findAll())
                .thenReturn(List.of(mentorshipRequest));
        List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(requestFilterDto);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .findAll();
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDtoList(List.of(mentorshipRequest));
        Assertions.assertNotNull(requests);
        Assertions.assertEquals(requests.size(), 1);
    }

    @Test
    public void testReturnNothingWhenRequest() {
        Mockito.when(mentorshipRequestMapper.toDtoList(Collections.emptyList()))
                .thenReturn(Collections.emptyList());
        Mockito.when(mentorshipRequestRepository.findAll())
                .thenReturn(Collections.emptyList());
        List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(requestFilterDto);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .findAll();
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDtoList(Collections.emptyList());
        Assertions.assertNotNull(requests);
        Assertions.assertEquals(requests.size(), 0);
    }

    @Test
    public void whenRequestForMembershipThenNoDataInDB() {
        try {
            mentorshipRequestService.acceptRequest(1L);
        } catch (EntityNotFoundException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("There is no mentorship request with this id");
        }
        try {
            mentorshipRequestService.rejectRequest(1L, new RejectionDto(StringUtils.EMPTY));
        } catch (EntityNotFoundException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("There is no mentorship request with this id");
        }
    }

    @Test
    public void whenRequestForMembershipThenCreated() {
        Mockito.when(mentorshipRequestMapper.toEntity(mentorshipRequestDto)).thenReturn(mentorshipRequest);
        Mockito.when(userService.getUserById(mentorshipRequestDto.getReceiver()))
                .thenReturn(receiver);
        Mockito.when(userService.getUserById(mentorshipRequestDto.getRequester()))
                .thenReturn(requester);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .save(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDTO(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toEntity(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestValidator, times(1))
                .validateUserData(any(), any());
    }
}