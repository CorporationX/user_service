package school.faang.user_service.dto.mentorship;

import school.faang.user_service.entity.RequestStatus;

public class MentorshipRequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}