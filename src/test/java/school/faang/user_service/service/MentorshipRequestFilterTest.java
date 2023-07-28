package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.MentorshipRequestFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestFilterTest {

    @InjectMocks
    private MentorshipRequestFilter requestFilter;
    private List<MentorshipRequestDto> requestDtoList;
    private RequestFilterDto filter;
    private UserDto requesterDto;
    private UserDto receiverDto;
    private MentorshipRequestDto dto1;
    private MentorshipRequestDto dto2;
    private MentorshipRequestDto dto3;
    private MentorshipRequestDto dto4;
    private MentorshipRequestDto dto5;

    @BeforeEach
    void initData() {
        filter = RequestFilterDto.builder().build();
        requesterDto = UserDto.builder()
                .id(1L)
                .build();
        receiverDto = UserDto.builder()
                .id(2L)
                .build();

        dto1 = MentorshipRequestDto.builder()
                .description("description")
                .requester(requesterDto)
                .receiver(receiverDto)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(RequestStatus.REJECTED)
                .build();
        dto2 = MentorshipRequestDto.builder()
                .description("another description")
                .requester(requesterDto)
                .receiver(receiverDto)
                .status(RequestStatus.ACCEPTED)
                .build();
        dto3 = MentorshipRequestDto.builder()
                .description("description")
                .requester(requesterDto)
                .receiver(requesterDto)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(RequestStatus.PENDING)
                .build();
        dto4 = MentorshipRequestDto.builder()
                .description("description")
                .requester(requesterDto)
                .receiver(receiverDto)
                .updatedAt(LocalDateTime.now().minusMonths(2).truncatedTo(ChronoUnit.SECONDS))
                .status(RequestStatus.ACCEPTED)
                .build();
        dto5 = MentorshipRequestDto.builder()
                .description("   ")
                .requester(receiverDto)
                .receiver(receiverDto)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(RequestStatus.ACCEPTED)
                .build();

        requestDtoList = List.of(dto1, dto2, dto3, dto4, dto5);
        requestFilter = MentorshipRequestFilter.builder()
                .filter(filter)
                .requestDtoList(requestDtoList)
                .build();
    }

    @Test
    void testRequestFilteringWithEmptyFilter() {
        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = requestDtoList;

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByDescription() {
        filter.setDescription("description");

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = List.of(dto1, dto3, dto4);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByReceiver() {
        filter.setReceiver(receiverDto);

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = List.of(dto1, dto2, dto4, dto5);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByRequester() {
        filter.setRequester(requesterDto);

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = List.of(dto1, dto2, dto3, dto4);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByUpdatedAt() {
        filter.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = List.of(dto1, dto3, dto5);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByStatus() {
        filter.setRequestStatus(RequestStatus.ACCEPTED);

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = List.of(dto2, dto4, dto5);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringWithMixFilters() {
        filter.setDescription("description");
        filter.setReceiver(receiverDto);
        filter.setRequester(requesterDto);

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = List.of(dto1, dto4);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringWithAllFilters() {
        filter.setDescription("description");
        filter.setRequester(requesterDto);
        filter.setReceiver(receiverDto);
        filter.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        filter.setRequestStatus(RequestStatus.REJECTED);

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = List.of(dto1);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringWithEmptyList() {
        filter.setUpdatedAt(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS));

        List<MentorshipRequestDto> actualList = requestFilter.requestFiltering();
        List<MentorshipRequestDto> expectedList = new ArrayList<>();

        assertEquals(expectedList, actualList);
    }
}