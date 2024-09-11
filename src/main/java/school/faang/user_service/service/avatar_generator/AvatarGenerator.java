package school.faang.user_service.service.avatar_generator;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.AWSS3Config;
import school.faang.user_service.config.DiceBearAvatarConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarGenerator {

    private final DiceBearAvatarConfig avatarConfig;
    private final AWSS3Config awsConfig;
    private static final String API_URL = "https://api.dicebear.com/9.x";

    public String generateAvatarLinkStatic() {
        String seed = String.valueOf(DiceBearAvatarSeeds.values()[new Random().nextInt(DiceBearAvatarSeeds.values().length)]);
        return API_URL + "/" + avatarConfig.getStyle() + "/" + avatarConfig.getFormat() + "?seed=" + seed;
    }

    public String GenerateAvatarLinkS3() {
        String staticLink = generateAvatarLinkStatic();
        try {
            URL avatarUrl = new URL(staticLink);
            URLConnection urlConnection = avatarUrl.openConnection();

            try (InputStream inputStream = urlConnection.getInputStream()) {
                ObjectMetadata metadata = new ObjectMetadata();
                String pathname = UUID.randomUUID().toString() + "." + avatarConfig.getFormat();

                // Create the request to upload the file to S3
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        awsConfig.getBucket(), pathname, inputStream, metadata
                ); // Make the object publicly accessible

                // Upload the file to S3
                awsConfig.s3client().putObject(putObjectRequest);

                // Generate the URL
                return awsConfig.s3client().getUrl(awsConfig.getBucket(), pathname).toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
