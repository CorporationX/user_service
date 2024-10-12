package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarFetchException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.minio.MinioService;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

@RequiredArgsConstructor
@Slf4j
@Service
public class AvatarService {
    private static final int LARGE_AVATAR_SIZE = 200;
    private static final int SMALL_AVATAR_SIZE = 50;
    private static final int LARGE_AVATAR_DIMENSION_CONSTRAINT = 1080;
    private static final int SMALL_AVATAR_DIMENSION_CONSTRAINT = 170;
    private static final String AVATAR_CONTENT_TYPE = "image/png";

    private final RestTemplate restTemplate;
    private final MinioService minioService;
    private final UserRepository userRepository;

    public UserProfilePic generateAndSaveAvatar(AvatarStyle style) {
        log.info("Generating avatar for style: {}", style.getStyleName());

        String largeAvatarFileName = uploadAvatar(style, LARGE_AVATAR_SIZE, AVATAR_CONTENT_TYPE);
        String smallAvatarFileName = uploadAvatar(style, SMALL_AVATAR_SIZE, AVATAR_CONTENT_TYPE);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(largeAvatarFileName);
        userProfilePic.setSmallFileId(smallAvatarFileName);

        return userProfilePic;
    }

    private String uploadAvatar(AvatarStyle style, int size, String contentType) {
        byte[] avatarData = getRandomAvatar(style, "png", size);
        String avatarFileName = UUID.randomUUID() + ".png";

        log.info("Saving avatar with name: {}", avatarFileName);
        minioService.uploadFile(avatarFileName, avatarData, contentType);
        return avatarFileName;
    }

    public byte[] getRandomAvatar(AvatarStyle style, String format, Integer size) {
        String url = String.format("https://api.dicebear.com/9.x/%s/%s", style.getStyleName(), format);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);

        if (size != null) {
            uriBuilder.queryParam("size", size);
        }

        ResponseEntity<byte[]> response = restTemplate.getForEntity(uriBuilder.toUriString(), byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Avatar successfully fetched from DiceBear API for style {}", style.getStyleName());
            return response.getBody();
        }
        log.error("Failed to fetch avatar from DiceBear API for style {}", style.getStyleName());
        throw new AvatarFetchException("Failed to fetch avatar from DiceBear API for style " + style.getStyleName());
    }

    private int[] getNewDimensions(int originalWidth, int originalHeight, double constraint) {
        int newWidth;
        int newHeight;

        if (originalWidth >= originalHeight) {
            newWidth = (int) constraint;
            newHeight = (int) (originalHeight * (constraint / originalWidth));
        } else {
            newHeight = (int) constraint;
            newWidth = (int) (originalWidth * (constraint / originalHeight));
        }

        return new int[]{newWidth, newHeight};
    }



    private byte[] resizeImage(int size, MultipartFile file) throws IOException {
        BufferedImage originalImage = null;
        originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

        BufferedImage outputImage;
        if (originalImage.getWidth() > size || originalImage.getHeight() > size){
            int[] newDimensions = getNewDimensions(originalImage.getWidth(), originalImage.getHeight(), size);
            Image resultingImage = originalImage.getScaledInstance(newDimensions[0], newDimensions[1], Image.SCALE_DEFAULT);
            outputImage = new BufferedImage(newDimensions[0], newDimensions[1], BufferedImage.TYPE_INT_RGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        } else {
            outputImage = originalImage;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    String shrinkAndUploadImage(Long userId, MultipartFile avatarFile, int constraint){
        byte[] avatarData = null;
        try {
            avatarData = resizeImage(constraint, avatarFile);
        } catch (IOException e) {
            log.error("Error while resizing file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        log.info(avatarFile.getContentType());
        String avatarFileName = UUID.randomUUID() + ".png";
        log.info("Saving avatar with name: {}", avatarFileName);
        minioService.uploadFile(avatarFileName, avatarData, AVATAR_CONTENT_TYPE);
        return avatarFileName;
    }

    @Transactional
    public UserProfilePic uploadUserAvatar(Long userId, MultipartFile avatarFile) {
        log.info("Uploading avatar for {}", userId);
        if (avatarFile.getSize() > 5 * 1024 * 1024){
            log.error("Cannot upload file bigger than 5Mb");
            throw new IllegalArgumentException("Cannot upload file bigger than 5Mb");
        }
        String largeAvatarFileName =  shrinkAndUploadImage(userId, avatarFile, LARGE_AVATAR_DIMENSION_CONSTRAINT);
        String smallAvatarFileName =  shrinkAndUploadImage(userId, avatarFile, SMALL_AVATAR_DIMENSION_CONSTRAINT);

        userRepository.changeProfilePic(userId,largeAvatarFileName,smallAvatarFileName);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(largeAvatarFileName);
        userProfilePic.setSmallFileId(smallAvatarFileName);

        return userProfilePic;
    }

    @Transactional
    public byte[] getUserAvatar(Long userId) {
        log.info("Downloading avatar for {}", userId);
        String userProfilePicId = userRepository.findProfilePic(userId);
        return minioService.downloadFile(userProfilePicId);
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        log.info("Deleting avatar pictures for {}", userId);
        String userProfilePicId =  userRepository.findProfilePic(userId);
        String userProfilePicIdSmall = userRepository.findSmallProfilePic(userId);
        if (userProfilePicId != null){
            minioService.deleteFile(userProfilePicId);
        }
        if (userProfilePicId != null){
            minioService.deleteFile(userProfilePicIdSmall);
        }
        userRepository.deleteProfilePic(userId);
    }
}