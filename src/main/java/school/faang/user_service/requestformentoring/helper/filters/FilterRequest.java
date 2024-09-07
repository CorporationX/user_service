package school.faang.user_service.requestformentoring.helper.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Component
public interface FilterRequest {

    List<MentorshipRequest> filterMenReq(RequestFilterDto filterDto, List<MentorshipRequest> menReq);
}
