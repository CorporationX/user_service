package school.faang.user_service.mapper.mentorship_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MentorshipRequestMapperTest {
    private MentorshipRequestMapper mentorshipRequestMapper;

    @BeforeEach
    void setUp() {
        mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    }

    @Test
    void toDto() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(user1);
        mentorshipRequest.setReceiver(user2);
        mentorshipRequest.setDescription("description");
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason("RejectionReason");
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusDays(1));
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());
        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestMapper.toDto(mentorshipRequest);

        assertEquals(mentorshipRequestDto.getRequesterId(), mentorshipRequest.getRequester().getId());
        assertEquals(mentorshipRequestDto.getReceiverId(), mentorshipRequest.getReceiver().getId());
        assertThat(mentorshipRequest)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(User.class)
                .isEqualTo(mentorshipRequestDto);
    }
}