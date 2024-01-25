package school.faang.user_service.mapper.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestMapperTest {
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper;

    private MentorshipRequest mentorshipRequest;

    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    void setUp() {
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
}