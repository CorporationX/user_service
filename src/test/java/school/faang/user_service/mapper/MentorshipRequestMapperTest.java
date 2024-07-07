package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class MentorshipRequestMapperTest {

    private final MentorshipRequestMapper mapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Test
    void testToEntity() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setRequestStatus(RequestStatus.PENDING);

        MentorshipRequest entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getRequester()).isNotNull();
        assertThat(entity.getRequester().getId()).isEqualTo(1L);
        assertThat(entity.getReceiver()).isNotNull();
        assertThat(entity.getReceiver().getId()).isEqualTo(2L);
        assertThat(entity.getStatus()).isEqualTo(RequestStatus.PENDING);
    }

    @Test
    void testToDto() {
        MentorshipRequest entity = new MentorshipRequest();
        User requester = new User();
        requester.setId(1L);
        entity.setRequester(requester);
        User receiver = new User();
        receiver.setId(2L);
        entity.setReceiver(receiver);
        entity.setStatus(RequestStatus.PENDING);

        MentorshipRequestDto dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getRequesterId()).isEqualTo(1L);
        assertThat(dto.getReceiverId()).isEqualTo(2L);
        assertThat(dto.getRequestStatus()).isEqualTo(RequestStatus.PENDING);
    }
}