package school.faang.user_service.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestStatus {

    PENDING(0),
    ACCEPTED(1),
    REJECTED(2);

    private final int value;

}