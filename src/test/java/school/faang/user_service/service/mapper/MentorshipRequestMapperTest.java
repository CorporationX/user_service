package school.faang.user_service.service.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestMapperTest {
    private static MentorshipRequest initialMentorshipRequest;
    private static MentorshipRequest expectedMentorshipRequest;
    private static MentorshipRequestDto initialMentorshipRequestDto;
    private static MentorshipRequestDto expectedMentorshipRequestDto;
    @Spy
    private MentorshipRequestMapperImpl mapper;

    @BeforeAll
    public static void init() {
        var requesterId = 1L;
        var receiverId = 2L;
        var createdAtOfInitialMentorshipRequest = LocalDateTime.of(2024, 1, 1, 12, 0);
        var updatedAtOfInitialMentorshipRequest = LocalDateTime.of(2024, 1, 1, 15, 0);
        var requesterOfInitialMentorshipRequest = User.builder()
                .id(requesterId)
                .username("NameOfRequesterOfInitialMentorshipRequest")
                .build();
        var receiverOfInitialMentorshipRequest = User.builder()
                .id(receiverId)
                .username("NameOfReceiverOfInitialMentorshipRequest")
                .build();

        initialMentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .description("description of initialMentorshipRequest")
                .requester(requesterOfInitialMentorshipRequest)
                .receiver(receiverOfInitialMentorshipRequest)
                .status(RequestStatus.PENDING)
                .rejectionReason("rejection reason of initialMentorshipRequest")
                .createdAt(createdAtOfInitialMentorshipRequest)
                .updatedAt(updatedAtOfInitialMentorshipRequest)
                .build();
        expectedMentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .description("description of initialMentorshipRequest")
                .requester(null)
                .receiver(null)
                .status(RequestStatus.PENDING)
                .rejectionReason("rejection reason of initialMentorshipRequest")
                .createdAt(createdAtOfInitialMentorshipRequest)
                .updatedAt(updatedAtOfInitialMentorshipRequest)
                .build();
        initialMentorshipRequestDto = MentorshipRequestDto.builder()
                .id(1L)
                .description("description of initialMentorshipRequest")
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(RequestStatus.PENDING.toString())
                .rejectionReason("rejection reason of initialMentorshipRequest")
                .createdAt(createdAtOfInitialMentorshipRequest)
                .updatedAt(updatedAtOfInitialMentorshipRequest)
                .build();
        expectedMentorshipRequestDto = MentorshipRequestDto.builder()
                .id(1L)
                .description("description of initialMentorshipRequest")
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(RequestStatus.PENDING.toString())
                .rejectionReason("rejection reason of initialMentorshipRequest")
                .createdAt(createdAtOfInitialMentorshipRequest)
                .updatedAt(updatedAtOfInitialMentorshipRequest)
                .build();
    }

    @Test
    public void testMappingOfMentorshipRequestEntityToMentorshipRequestDto() {
        var actual = mapper.toDto(initialMentorshipRequest);

        assertEquals(expectedMentorshipRequestDto, actual);
    }

    @Test
    public void testMappingOfMentorshipRequestDtoToMentorshipRequestEntity() {
        var actual = mapper.toEntity(initialMentorshipRequestDto);

        assertEquals(expectedMentorshipRequest, actual);
    }
}
