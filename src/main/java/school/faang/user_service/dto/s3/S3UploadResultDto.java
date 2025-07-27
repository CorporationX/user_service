package school.faang.user_service.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3UploadResultDto {
    String key;
    String url;
}
