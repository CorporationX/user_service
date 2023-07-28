package school.faang.user_service.filter;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Builder
public class MentorshipRequestFilter {

    private List<MentorshipRequestDto> requestDtoList;
    private RequestFilterDto filter;

    public List<MentorshipRequestDto> requestFiltering() {
        List<MentorshipRequestDto> resultList = requestDtoList;

        if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
            resultList = filterByDescription(resultList);
        }
        if (filter.getReceiver() != null) {
            resultList = filterByReceiver(resultList);
        }
        if (filter.getRequester() != null) {
            resultList = filterByRequester(resultList);
        }
        if (filter.getUpdatedAt() != null) {
            resultList = filterByUpdatedAt(resultList);
        }
        if (filter.getRequestStatus() != null) {
            resultList = filterByRequestStatus(resultList);
        }

        return resultList;
    }

    private List<MentorshipRequestDto> filterByDescription(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> requestDto.getDescription().contains(filter.getDescription()))
                .toList();
    }

    private List<MentorshipRequestDto> filterByRequester(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> requestDto.getRequester().equals(filter.getRequester()))
                .toList();
    }

    private List<MentorshipRequestDto> filterByReceiver(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> requestDto.getReceiver().equals(filter.getReceiver()))
                .toList();
    }

    private List<MentorshipRequestDto> filterByUpdatedAt(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> requestDto.getUpdatedAt().truncatedTo(ChronoUnit.HOURS)
                        .equals(filter.getUpdatedAt().truncatedTo(ChronoUnit.HOURS)))
                .toList();
    }

    private List<MentorshipRequestDto> filterByRequestStatus(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> requestDto.getStatus() == filter.getRequestStatus())
                .toList();
    }
}
