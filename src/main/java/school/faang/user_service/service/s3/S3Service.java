package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service{
    @Value("${services.s3.bucket-name}")
    private String bucketName;

}
