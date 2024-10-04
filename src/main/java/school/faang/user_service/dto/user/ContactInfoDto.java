package school.faang.user_service.dto.user;

import com.json.student.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfoDto {
        private String email;
        private String phone;
        private Address address;
}