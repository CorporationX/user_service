package school.faang.user_service.service.profile_picture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.ProfilePictureServiceConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.s3.S3Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePictureService {

    private final S3Client s3Client;
    private final ProfilePictureServiceConfig config;

    public String generatePictureUrl(User user) {
        String encodedUsername = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        return config.getDiceBearBaseUrlWithStyle() + encodedUsername;
    }

    public byte[] downloadPicture(String pictureURL) {
        byte[] pictureBytes = new byte[0];

        try {
            URL url = new URL(pictureURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try (InputStream in = connection.getInputStream()) {
                pictureBytes = in.readAllBytes();
            }
        } catch (MalformedURLException e) {
            log.error("Invalid picture URL: {}", pictureURL);
        } catch (IOException e) {
            log.error("Failed to download picture from URL: {}", pictureURL);
        }

        return pictureBytes;
    }

    public byte[] resizePicture(byte[] pictureData) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(pictureData));
            BufferedImage resizedImage = new BufferedImage(config.getWidth(), config.getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, config.getWidth(), config.getHeight(), null);
            g.dispose();

            ImageIO.write(resizedImage, "JPEG", baos);
        } catch (IOException e) {
            log.error("Invalid request. Failed to resize picture.");
        }

        return baos.toByteArray();
    }

    public void setProfilePicture(User user) {
        String pictureURL = generatePictureUrl(user);

        byte[] picture = downloadPicture(pictureURL);
        byte[] resizedPicture = resizePicture(picture);

        s3Client.uploadProfilePicture(user, picture, config.getOriginalExtension());
        s3Client.uploadProfilePicture(user, resizedPicture, config.getResizedExtension());

        UserProfilePic profilePic = new UserProfilePic();

        profilePic.setFileId(s3Client.getURLById(user.getId(), config.getOriginalExtension()));
        profilePic.setSmallFileId(s3Client.getURLById(user.getId(), config.getResizedExtension()));

        user.setUserProfilePic(profilePic);
    }
}
