package school.faang.user_service.mapper.mentorshipRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.service.mentorshipRequest.MentorshipRequestService;

public class MentorshipRequestMapperTest {

    @Mock
    FilterRequestStatusValidator filterRequestStatusValidator;

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

    @Test
    void testMappingRequestFilterDto_UsersIdsAreNull() {
        RequestFilterDto requestFilterDto =
                new RequestFilterDto("description", null, null, "PENDING");

        mentorshipRequestMapper.toEntity(requestFilterDto, mentorshipRequestService, filterRequestStatusValidator);

        Mockito.verify(mentorshipRequestService, Mockito.times(0))
                .findUserById(Mockito.anyLong());
    }
}
