package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestMapperTest {

    private final MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    private final MentorshipRequest mentorshipRequest = new MentorshipRequest();

    private final MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
            .status(RequestStatus.PENDING)
            .build();

    @Test
    void testToEntity() {
        MentorshipRequest actualMentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        assertEquals(mentorshipRequest.getStatus(), actualMentorshipRequest.getStatus());
    }

    @Test
    void testToDto() {
        MentorshipRequestDto actualMentorshipRequestDto = mentorshipRequestMapper.toDto(mentorshipRequest);
        actualMentorshipRequestDto.setStatus(RequestStatus.PENDING);

        assertEquals(mentorshipRequestDto, actualMentorshipRequestDto);
    }

    @Test
    void toDtoList() {
        List<MentorshipRequestDto> dtos = List.of(mentorshipRequestDto);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        List<MentorshipRequestDto> actualDtos = mentorshipRequestMapper.toDtoList(List.of(mentorshipRequest));

        assertEquals(dtos, actualDtos);
    }
}
