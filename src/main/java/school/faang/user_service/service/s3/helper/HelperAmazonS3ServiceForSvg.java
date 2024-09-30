package school.faang.user_service.service.s3.helper;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.HelperAmazonS3Service;

import java.util.UUID;

@Slf4j
@Component
public class HelperAmazonS3ServiceForSvg implements HelperAmazonS3Service {

    @Override
    public ObjectMetadata getMetadata(byte[] picture) {
        log.info("Getting metadata");

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(picture.length);
        objectMetadata.setContentType("image/svg");
        objectMetadata.setContentEncoding("utf-8");

        log.info("Getting metadata success");
        return objectMetadata;
    }

    @Override
    public String getKey(byte[] picture, String name, String... keyArgs) {
        log.info("Creating a key");
        StringBuilder key = new StringBuilder(name).append("/");

        for (String arg : keyArgs) {
            key.append(arg).append(":");
        }

        UUID uuid = UUID.nameUUIDFromBytes(picture);
        log.info("The key was created successfully");
        return key.append(uuid)
                .append("-image.svg")
                .toString();
    }
}
