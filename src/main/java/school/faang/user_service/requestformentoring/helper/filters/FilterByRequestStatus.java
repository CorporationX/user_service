package school.faang.user_service.requestformentoring.helper.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Component
public class FilterByRequestStatus implements FilterRequest {


    @Override
    public List<MentorshipRequest> filterMenReq(RequestFilterDto filterDto, List<MentorshipRequest> menReq) {
        return menReq.stream().filter(mentorshipRequest -> mentorshipRequest.getStatus()
                .equals(filterDto.getStatus())).toList();
    }
}
