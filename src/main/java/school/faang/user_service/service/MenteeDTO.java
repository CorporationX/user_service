package school.faang.user_service.service;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class MenteeDTO {
    private long id;
    private String name;

    public MenteeDTO() {
        this.id = 0;
        this.name = name;

    }
}