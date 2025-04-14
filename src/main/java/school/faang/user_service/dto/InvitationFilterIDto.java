package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

public record InvitationFilterIDto(String inviterNamePattern,
                                   String invitedNamePattern,
                                   Long inviterId,
                                   Long invitedId,
                                   RequestStatus status) { }
