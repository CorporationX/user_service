package school.faang.user_service.model.dto;

import com.json.student.Address;

public record ContactInfoDto(String email, String phone, Address address) {
}