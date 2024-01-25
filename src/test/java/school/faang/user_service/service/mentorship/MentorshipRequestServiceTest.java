package school.faang.user_service.service.mentorship;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.mentorship.DataNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequest mentorshipRequest;

    @Test
    public void whenRequestForMembershipThenNoDataInDB() {
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
        mentorshipRequestService.rejectRequest(1L, new RejectionDto(StringUtils.EMPTY));
        Mockito.verify(mentorshipRequestRepository, times(1))
                .save(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDTO(mentorshipRequest);
    }
}