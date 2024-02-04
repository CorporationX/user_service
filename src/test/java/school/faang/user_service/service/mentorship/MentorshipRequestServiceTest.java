package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.DataNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;


import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;


import school.faang.user_service.dto.mentorship.RejectionDto;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestDto mentorshipRequestDto;

    @InjectMocks
    private MentorshipRequest mentorshipRequest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void init() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequester(1L);
        mentorshipRequestDto.setDescription("Description");
        mentorshipRequestDto.setRequester(88L);
        mentorshipRequestDto.setReceiver(77L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("Ivan");
        requester = new User();
        requester.setId(1L);
        requester.setUsername("John");
        user = new User();
        user.setId(1L);
        user.setUsername("John");
        Mockito.when(mentorshipRequestMapper.toEntity(mentorshipRequestDto))
                .thenReturn(mentorshipRequest);
        Mockito.when(mentorshipRequestMapper.toDTO(mentorshipRequest))
                .thenReturn(mentorshipRequestDto);
    }

    @Test
    public void whenRequestForMembershipThenNoDataInDB() {
        try {
            mentorshipRequestService.acceptRequest(1L);
        } catch (DataNotFoundException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("There is no mentorship request with this id");
        }
        try {
            mentorshipRequestService.rejectRequest(1L, new RejectionDto(StringUtils.EMPTY));
        } catch (DataNotFoundException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("There is no mentorship request with this id");
        }
    }

    @Test
    public void whenRequestForMembershipThenSuccess() {
        Mockito.when(mentorshipRequestRepository.findById(1L))
                .thenReturn(Optional.of(mentorshipRequest));
        Mockito.when(mentorshipRequest.getRequester()).thenReturn(requester);
        Mockito.when(mentorshipRequest.getReceiver()).thenReturn(receiver);
        requester.setMentors(new ArrayList<>());
        mentorshipRequestService.acceptRequest(1L);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .save(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDTO(mentorshipRequest);
        mentorshipRequestService.rejectRequest(1L, new RejectionDto(StringUtils.EMPTY));
        Mockito.verify(mentorshipRequestRepository, times(2))
                .save(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(2))
                .toDTO(mentorshipRequest);
    }

    @Test
    public void whenRequestForMembershipThenCreated() {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .save(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDTO(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toEntity(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestValidator, times(1))
                .validateUserData(any(), any());
        Assertions.assertEquals(mentorshipRequestService.requestMentorship(mentorshipRequestDto)
                , mentorshipRequestDto);
    }
}