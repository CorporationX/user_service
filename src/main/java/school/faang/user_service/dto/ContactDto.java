package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.ContactType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    private long id;

    @NotNull
    private Long userId;

    @NotNull
    private String contact;

    @NotNull
    private ContactType type;
}
