package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MentorshipRequestMapperTest {

    private MentorshipRequestMapper mapper = new MentorshipRequestMapperImpl();

    @Test
    void toDto_WhenEntityProvided_ShouldCorrectlyMapToDto() {
        MentorshipRequest entity = new MentorshipRequest();
        User requester = new User(); requester.setId(1L);
        User receiver = new User(); receiver.setId(2L);
        entity.setRequester(requester);
        entity.setReceiver(receiver);
        entity.setDescription("Description");

        MentorshipRequestDto dto = mapper.toDto(entity);

        assertEquals(entity.getRequester().getId(), dto.getRequesterId());
        assertEquals(entity.getReceiver().getId(), dto.getReceiverId());
        assertEquals(entity.getDescription(), dto.getDescription());
    }

    @Test
    void toEntity_WhenDtoProvided_ShouldCorrectlyMapToEntity() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "Description");

        MentorshipRequest entity = mapper.toEntity(dto);

        assertEquals(dto.getRequesterId(), entity.getRequester().getId());
        assertEquals(dto.getReceiverId(), entity.getReceiver().getId());
        assertEquals(dto.getDescription(), entity.getDescription());
    }
}