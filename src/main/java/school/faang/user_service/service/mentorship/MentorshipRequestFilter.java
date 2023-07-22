package school.faang.user_service.service.mentorship;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;

import java.util.List;

@Data
@Builder
public class MentorshipRequestFilter {

    private List<MentorshipRequestDto> requestDtoList;
    private RequestFilterDto filter;

    public List<MentorshipRequestDto> requestFiltering() {
        List<MentorshipRequestDto> resultList = requestDtoList;

        if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
            resultList = byDescription(resultList);
        }
        if (filter.getReceiver() != null) {
            resultList = byReceiver(resultList);
        }
        if (filter.getRequester() != null) {
            resultList = byRequester(resultList);
        }
        if (filter.getUpdatedAt() != null) {
            resultList = byUpdatedAt(resultList);
        }
        if (filter.getRequestStatus() != null) {
            resultList = byRequestStatus(resultList);
        }

        return resultList;
    }

    private List<MentorshipRequestDto> byDescription(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> {
                    if (requestDto.getDescription() != null) {
                        return requestDto.getDescription().equals(filter.getDescription());
                    }
                    return false;
                })
                .toList();
    }

    private List<MentorshipRequestDto> byRequester(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> {
                    if (requestDto.getRequester() != null) {
                        return  requestDto.getRequester().equals(filter.getRequester());
                    }
                    return false;
                })
                .toList();
    }

    private List<MentorshipRequestDto> byReceiver(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> {
                    if (requestDto.getReceiver() != null) {
                        return requestDto.getReceiver().equals(filter.getReceiver());
                    }
                    return false;
                })
                .toList();
    }

    private List<MentorshipRequestDto> byUpdatedAt(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> {
                    if (requestDto.getUpdatedAt() != null) {
                        return requestDto.getUpdatedAt().equals(filter.getUpdatedAt());
                    }
                    return false;
                })
                .toList();
    }

    private List<MentorshipRequestDto> byRequestStatus(List<MentorshipRequestDto> list) {
        return list.stream()
                .filter(requestDto -> requestDto.getStatus() == filter.getRequestStatus())
                .toList();
    }
}
