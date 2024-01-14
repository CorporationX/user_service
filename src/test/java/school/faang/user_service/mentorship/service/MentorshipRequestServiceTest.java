package school.faang.user_service.mentorship.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.mentorship.filter.RequestFilterDto;
import school.faang.user_service.mentorship.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilters;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestDto mentorshipRequestDto;

    @InjectMocks
    private MentorshipRequest mentorshipRequest;

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
        Mockito.when(mentorshipRequestMapper.toDtoList(List.of(mentorshipRequest)))
                .thenReturn(List.of(mentorshipRequestDto));
        Mockito.when(mentorshipRequestRepository.findAll())
                .thenReturn(List.of(mentorshipRequest));
    }

    @Test
    public void testReturnAllMentorshipRequests() {
        List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(requestFilterDto);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .findAll();
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDtoList(List.of(mentorshipRequest));
        Assertions.assertNotNull(requests);
        Assertions.assertEquals(requests.size(), 1);
    }
}