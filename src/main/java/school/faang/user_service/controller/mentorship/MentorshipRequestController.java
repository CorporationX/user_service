package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto createMentorshipRequestDto(CreateMentorshipRequestDto createMentorshipRequestDto) {
        validateNotNull(createMentorshipRequestDto.description(), "description");
        validateString(createMentorshipRequestDto.description(), "description");
        validateNotNull(createMentorshipRequestDto.mentorId(), "mentor id");

        return mentorshipRequestService.create(createMentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        validateFilterNotNull(filter, "filter");
        return mentorshipRequestService.getByFilters(filter);
    }

    public void accept(long requestId) {
        validateNotNull(requestId, "requestId");
        mentorshipRequestService.accept(requestId);
    }

    public void reject(long requestId, RejectionDto rejectionDto) {
        validateNotNull(requestId, "requestId");
        validateNotNull(rejectionDto, "rejectionDto");
        validateString(rejectionDto.reason(), "reason");
        mentorshipRequestService.reject(requestId, rejectionDto);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateFilterNotNull(MentorshipRequestFilterDto filter, String paramName) {
        if (filter.requesterId() == null || filter.receiverId() == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}