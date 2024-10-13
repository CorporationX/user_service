package school.faang.user_service.dto.user;

import com.json.student.Address;

public record ContactInfoDto(String email, String phone, Address address) {
}