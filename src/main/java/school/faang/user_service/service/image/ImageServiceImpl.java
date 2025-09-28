package school.faang.user_service.service.image;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.MinioConfig;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.FileUploadException;
import school.faang.user_service.exception.FileSizeLimitExceededException;
import school.faang.user_service.exception.ImageProcessingException;
import school.faang.user_service.exception.InvalidFileFormatException;
import school.faang.user_service.exception.StorageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_IMAGE_SIZE = 512;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    @Override
    public String uploadTeamAvatar(MultipartFile file, long teamId) {
        validateFile(file);

        BufferedImage originalImage = readImageFile(file);
        BufferedImage resized = processImage(originalImage);
        byte[] bytes = convertImageToBytes(resized);

        String objectKey = "team-" + teamId + "-avatar-" + UUID.randomUUID() + ".jpg";
        uploadToStorage(bytes, objectKey, teamId);

        return objectKey;
    }

    @Override
    public void deleteTeamAvatar(String avatarKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(avatarKey)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to delete avatar from MinIO: {}", avatarKey, e);
            throw new StorageException("Failed to delete avatar from storage", e);
        }
    }

    @Override
    public byte[] getTeamAvatar(String avatarKey) {
        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(avatarKey)
                        .build()
        )) {
            return response.readAllBytes();
        } catch (Exception e) {
            log.error("Failed to retrieve avatar from MinIO: {}", avatarKey, e);
            throw new EntityNotFoundException("Avatar not found: " + avatarKey);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeLimitExceededException(
                    String.format("File size %d bytes exceeds maximum allowed size of %d bytes",
                            file.getSize(), MAX_FILE_SIZE)
            );
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidFileFormatException(
                    String.format("Unsupported file type: %s. Allowed types: %s",
                            file.getContentType(), ALLOWED_CONTENT_TYPES)
            );
        }
    }

    private BufferedImage readImageFile(MultipartFile file) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new FileUploadException("Failed to read image file", e);
        }

        if (originalImage == null) {
            throw new InvalidFileFormatException("Invalid or corrupted image file - unable to parse as image");
        }

        return originalImage;
    }

    private BufferedImage processImage(BufferedImage originalImage) {
        try {
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            if (width <= 0 || height <= 0) {
                throw new InvalidFileFormatException("Invalid image dimensions: " + width + "x" + height);
            }

            double scale = Math.min(1.0, (double) MAX_IMAGE_SIZE / Math.max(width, height));
            int newWidth = (int) (width * scale);
            int newHeight = (int) (height * scale);

            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            resized.getGraphics().drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            return resized;
        } catch (Exception e) {
            if (e instanceof InvalidFileFormatException) {
                throw e;
            }
            throw new ImageProcessingException("Failed to process image during resizing", e);
        }
    }

    private byte[] convertImageToBytes(BufferedImage image) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean written = ImageIO.write(image, "jpg", os);
            if (!written) {
                throw new ImageProcessingException("Failed to write image in JPEG format");
            }
            return os.toByteArray();
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to convert image to bytes", e);
        }
    }

    private void uploadToStorage(byte[] bytes, String objectKey, long teamId) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectKey)
                            .stream(is, bytes.length, -1)
                            .contentType("image/jpeg")
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to upload avatar to MinIO for team {}", teamId, e);
            throw new StorageException("Failed to upload avatar to storage", e);
        }
    }
}