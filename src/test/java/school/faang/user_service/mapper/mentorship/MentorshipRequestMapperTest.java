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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestMapperTest {
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper;

    private MentorshipRequest mentorshipRequest;

    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        User requester = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        mentorshipRequest = new MentorshipRequest(1L, "Description", requester, receiver, RequestStatus.ACCEPTED, "Reason", now, now.plusMonths(3));
        mentorshipRequestDto = MentorshipRequestDto.builder()
                .id(1L)
                .receiver(2L)
                .requester(1L)
                .description("Description")
                .createdAt(now)
                .updatedAt(now.plusMonths(3))
                .build();
    }

    @Test
    void testToDto() {
        MentorshipRequestDto toDto = mentorshipRequestMapper.toDTO(mentorshipRequest);
        assertEquals(mentorshipRequestDto, toDto);
    }

    @Test
    void testToEntity() {
        MentorshipRequest toEntity = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        assertEquals(mentorshipRequest, toEntity);
    }
}
