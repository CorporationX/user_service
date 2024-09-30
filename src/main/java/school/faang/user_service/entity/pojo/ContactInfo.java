
package school.faang.user_service.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ContactInfo {
    private String email;
    private String phone;
    private Address address;
}
