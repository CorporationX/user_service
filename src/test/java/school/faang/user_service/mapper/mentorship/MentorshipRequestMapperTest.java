package school.faang.user_service.mapper.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestMapperTest {
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;
    private MentorshipRequest request;
    private MentorshipRequestDto dto;

    @BeforeEach
    void setUp() {
        request = MentorshipRequest.builder()
                .id(1L)
                .description("description")
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .rejectionReason("rejection reason")
                .status(RequestStatus.PENDING)
                .build();
        dto = MentorshipRequestDto.builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .receiverId(2L)
                .rejectionReason("rejection reason")
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    void toDto_shouldMatchAllFields() {
        MentorshipRequestDto actualDto = mentorshipRequestMapper.toDto(request);
        assertAll(() -> {
            assertEquals(dto.getId(), actualDto.getId());
            assertEquals(dto.getDescription(), actualDto.getDescription());
            assertEquals(dto.getRequesterId(), actualDto.getRequesterId());
            assertEquals(dto.getReceiverId(), actualDto.getReceiverId());
            assertEquals(dto.getRejectionReason(), actualDto.getRejectionReason());
            assertEquals(dto.getStatus(), actualDto.getStatus());
        });
    }

    @Test
    void toEntity_shouldMatchAllFields() {
        MentorshipRequest actualRequest = mentorshipRequestMapper.toEntity(dto);
        assertAll(() -> {
            assertEquals(request.getId(), actualRequest.getId());
            assertEquals(request.getDescription(), actualRequest.getDescription());
            assertEquals(request.getRequester(), actualRequest.getRequester());
            assertEquals(request.getReceiver(), actualRequest.getReceiver());
            assertEquals(request.getRejectionReason(), actualRequest.getRejectionReason());
            assertEquals(request.getStatus(), actualRequest.getStatus());
        });
    }
}