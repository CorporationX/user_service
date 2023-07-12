package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.mapper.MentorshipRequestMapper;

public class MentorshipRequestMapperTest {

    @Mock
    MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMappingMentorshipDto() {
        MentorshipRequestDto mentorshipRequestDto =
                new MentorshipRequestDto("description", 1L, 2L);

        mentorshipRequestMapper.toEntity(mentorshipRequestDto, mentorshipRequestService);

        Mockito.verify(mentorshipRequestService, Mockito.times(2))
                .findUserById(Mockito.anyLong());
    }
}
