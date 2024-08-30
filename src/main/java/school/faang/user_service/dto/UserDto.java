package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для предоставления мета информации о пользователе.
 */
@Builder
@Data
@AllArgsConstructor
public class UserDto {

  private Long id;
  private String username;
  private String email;
  private String phone;
  private String aboutMe;
  private String city;
  private boolean active;
  private String premium;

}