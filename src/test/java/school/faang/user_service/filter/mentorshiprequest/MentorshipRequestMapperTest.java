package school.faang.user_service.filter.mentorshiprequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorshiprequest.MentorshipRequestMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MentorshipRequestMapperTest {

    private final MentorshipRequestMapper menReqMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long MENTORSHIP_REQUEST_ID = 1L;
    private static final String DESCRIPTION = "Test";
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2024, 8, 22, 22, 20, 20);

    @Test
    @DisplayName("Успех маппинга MentorshipRequest в MentorshipRequestDto")
    void whenToDtoThenSuccess() {
        User requester = new User();
        requester.setId(REQUESTER_ID);
        User receiver = new User();
        receiver.setId(RECEIVER_ID);

        MentorshipRequest menReqEntity = new MentorshipRequest();

        menReqEntity.setId(MENTORSHIP_REQUEST_ID);
        menReqEntity.setDescription(DESCRIPTION);
        menReqEntity.setRequester(requester);
        menReqEntity.setReceiver(receiver);
        menReqEntity.setStatus(RequestStatus.ACCEPTED);
        menReqEntity.setCreatedAt(LOCAL_DATE_TIME);

        MentorshipRequestDto menReqDto = menReqMapper.toDto(menReqEntity);

        assertEquals(MENTORSHIP_REQUEST_ID, menReqDto.getId());
        assertEquals(DESCRIPTION, menReqDto.getDescription());
        assertEquals(REQUESTER_ID, menReqDto.getRequesterId());
        assertEquals(RECEIVER_ID, menReqDto.getReceiverId());
        assertEquals(RequestStatus.ACCEPTED, menReqDto.getStatus());
        assertEquals(LOCAL_DATE_TIME.toLocalDate(), menReqDto.getCreatedAt());
    }
}