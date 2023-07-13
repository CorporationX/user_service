package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.MentorshipRequestService;

@Component
public class FilterRequestStatusValidator {

    public RequestStatus validateStatus(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() == null ?
                null :
                RequestStatus.valueOf(requestFilterDto.getStatus());
    }

    public User validateRequester(RequestFilterDto requestFilterDto,
                                  MentorshipRequestService mentorshipRequestService) {
        return requestFilterDto.getRequesterId() == null ?
                null :
                mentorshipRequestService.findUserById(requestFilterDto.getRequesterId());
    }

    public User validateReceiver(RequestFilterDto requestFilterDto,
                                  MentorshipRequestService mentorshipRequestService) {
        return requestFilterDto.getReceiverId() == null ?
                null :
                mentorshipRequestService.findUserById(requestFilterDto.getReceiverId());
    }
}
