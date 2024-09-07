package school.faang.user_service.requestformentoring.helper.filters;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

public class FilterByRecipientRequest implements FilterRequest {

    @Override
    public List<MentorshipRequest> filterMenReq(RequestFilterDto filterDto, List<MentorshipRequest> menReq) {
        return menReq.stream().filter(mentorshipRequest -> mentorshipRequest.getRequester().getId()
                .equals(filterDto.getRequesterId())).toList();
    }
}
