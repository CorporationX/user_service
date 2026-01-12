package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {
    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;
    @Mock
    private MentorshipRequestService mentorshipRequestService;
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    @Test
    void testToMentorshipRequestDtoMentorshipRequestNull() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.toMentorshipRequestDto(null));
    }

    @Test
    void testToMentorshipRequestDtoRequesterNull() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(null);
        mentorshipRequest.setReceiver(new User());
        mentorshipRequest.setDescription("description");

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.toMentorshipRequestDto(mentorshipRequest));
    }

    @Test
    void testToMentorshipRequestDtoReceiverNull() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(null);
        mentorshipRequest.setDescription("description");

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.toMentorshipRequestDto(mentorshipRequest));
    }

    @Test
    void testToMentorshipRequestDtoDescriptionNull() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(new User());
        mentorshipRequest.setDescription(null);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.toMentorshipRequestDto(mentorshipRequest));
    }

    @Test
    void testToMentorshipRequestDtoDescriptionIsBlank() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(new User());
        mentorshipRequest.setDescription(" ");

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.toMentorshipRequestDto(mentorshipRequest));
    }

    @Test
    void testToMentorshipRequestDto() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(new User());
        mentorshipRequest.setDescription("description");
        MentorshipRequestDto dto = createMentorshipRequestDto(1L, 2L, 3L);
        when(mentorshipRequestService.toMentorshipRequestDto(mentorshipRequest.getId())).thenReturn(dto);

        MentorshipRequestDto res = mentorshipRequestController.toMentorshipRequestDto(mentorshipRequest);


        assertEquals(mentorshipRequest.getId(), res.id());
    }

    @Test
    void testGetByFiltersRequesterIdNullAndReceiverIdNull() {
        MentorshipRequestFilterDto filter = new MentorshipRequestFilterDto(
                null, null, RequestStatus.REJECTED);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.getByFilters(filter));
    }

    @Test
    void testGetByFilters() {
        MentorshipRequest entity1 = new MentorshipRequest();
        entity1.setId(1L);
        entity1.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest entity2 = new MentorshipRequest();
        entity2.setId(2L);
        entity2.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequestFilterDto filter = new MentorshipRequestFilterDto(
                1L, 2L, RequestStatus.ACCEPTED);
        MentorshipRequestDto dto1 = createMentorshipRequestDto(1L, 2L, 3L);
        MentorshipRequestDto dto2 = createMentorshipRequestDto(2L, 4L, 5L);

        List<MentorshipRequestDto> expectedDtos = List.of(dto1, dto2);

        when(mentorshipRequestService.getByFilters(filter)).thenReturn(expectedDtos);
        when(mentorshipRequestMapper.toMentorshipRequest(dto1)).thenReturn(entity1);
        when(mentorshipRequestMapper.toMentorshipRequest(dto2)).thenReturn(entity2);

        List<MentorshipRequest> expectedEntities = List.of(entity1, entity2);
        List<MentorshipRequest> result = mentorshipRequestController.getByFilters(filter);

        assertEquals(expectedEntities, result);
    }

    @Test
    void testAccept() {
        long requestId = 1L;

        mentorshipRequestController.accept(requestId);

        verify(mentorshipRequestService, times(1)).accept(requestId);
    }

    @Test
    void testRejectionDtoNull() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto(null);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.reject(requestId, rejectionDto));
    }

    @Test
    void testRejectionDtoIsBlank() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto(" ");

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.reject(requestId, rejectionDto));
    }

    @Test
    void testReject() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto("h");

        mentorshipRequestController.reject(requestId, rejectionDto);

        verify(mentorshipRequestService, times(1)).reject(requestId, rejectionDto);
    }

    @NotNull
    private MentorshipRequestDto createMentorshipRequestDto(Long id, Long requesterId,
                                                            Long receiverId) {
        UserDto userDtoRequester = new UserDto(requesterId, "j", "j", "j", "j");
        UserDto userDtoReceiver = new UserDto(receiverId, "j", "j", "j", "j");

        return new MentorshipRequestDto(id, "description",
                userDtoRequester, userDtoReceiver, RequestStatus.ACCEPTED);
    }
}
