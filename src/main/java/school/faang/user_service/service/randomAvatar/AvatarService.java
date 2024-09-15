package school.faang.user_service.service.randomAvatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.AvatarConfigProperties;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.randomAvatar.AvatarGenerationException;
import school.faang.user_service.exception.s3.FileDownloadException;
import school.faang.user_service.service.s3Service.S3Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarConfigProperties avatarProperties;
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    @Transactional
    public void setAvatar(UserDto userDto, byte[] avatar) {
        File photo;

        if (avatar == null) {
            photo = getRandomPhoto();
        } else  {
            photo = convertBytesToFile(avatar);
        }

        File smallPhoto = prepareSmallAvatar(photo);

        String randomPhotoId = uploadFile(photo);
        String smallRandomPhotoId = uploadFile(smallPhoto);

        userDto.setUserProfilePic(UserProfilePic.builder()
                .fileId(randomPhotoId)
                .smallFileId(smallRandomPhotoId)
                .build());
    }

    public String uploadFile(File file) {
        return s3Service.uploadFile(file);
    }

    public byte[] get(String avatarId) {
        InputStream file = s3Service.getFile(avatarId);

        try {
            return file.readAllBytes();
        } catch (IOException e) {
            throw new FileDownloadException(e.getMessage());
        }
    }

    public File getRandomPhoto() {
        List<String> avatars = avatarProperties.getAvatars();
        String avatarType = avatars.get(new Random().nextInt(avatars.size()));
        String uri = String.format("%s/%s/%s", avatarProperties.getUrl(), avatarType.replaceAll(" ", "-").toLowerCase(), avatarProperties.getType());

        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null, byte[].class);

        File file = new File(String.format("%s.%s", avatarProperties.getAvatarName(), avatarProperties.getType()));

        return writeResponseToFile(file, response);
    }

    public File writeResponseToFile(File file, ResponseEntity<byte[]> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
                fileOutputStream.write(response.getBody());
            } catch (IOException e) {
                log.warn(e.getMessage());
                throw new AvatarGenerationException(e.getMessage());
            }
        } else {
            throw new AvatarGenerationException("Avatar generated unsuccessfully");
        }

        return file;
    }

    public File prepareSmallAvatar(File file) {
        File smallAvatar = new File(String.format("%s.%s", avatarProperties.getSmallAvatarName(), avatarProperties.getType()));
        try {
            BufferedImage originalImage = ImageIO.read(file);

            BufferedImage resizedImage = new BufferedImage(
                    avatarProperties.getSmallFileWidth(),
                    avatarProperties.getSmallFileHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = resizedImage.createGraphics();

            g2d.drawImage(originalImage.getScaledInstance(
                    avatarProperties.getSmallFileWidth(),
                    avatarProperties.getSmallFileHeight(),
                    Image.SCALE_SMOOTH),
                    0,
                    0,
                    null);
            g2d.dispose();

            ImageIO.write(resizedImage, avatarProperties.getType(), smallAvatar);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        return smallAvatar;
    }

    public File convertBytesToFile(byte[] bytes) {
        File file = new File(String.format("%s.%s", avatarProperties.getAvatarName(), avatarProperties.getType()));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return file;
    }

    public byte[] convertMultipartFileToBytes(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
