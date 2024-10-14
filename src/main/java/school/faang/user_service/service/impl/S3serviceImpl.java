package school.faang.user_service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.model.entity.UserProfilePic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3serviceImpl implements S3service {

    private static final int LARGE_SIZE = 1080;
    private static final int SMALL_SIZE = 170;
    private static final String JPG = "jpg";

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public UserProfilePic uploadFile(MultipartFile file, long userId) {
        String fileId = UUID.randomUUID().toString();
        String fileName = fileId + "." + JPG;
        String largeKey = "large/userId-" + userId + "/" + fileName;
        String smallKey = "small/userId-" + userId + "/" + fileName;

        try {
            put(file, largeKey, LARGE_SIZE);
            put(file, smallKey, SMALL_SIZE);
            return new UserProfilePic(largeKey, smallKey);
        } catch (IOException e) {
            throw new RuntimeException("Error when uploading file", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object object = s3Client.getObject(bucketName, key);
        return object.getObjectContent();
    }

    private void put(MultipartFile file, String key, int maxSize) throws IOException {
        Pair<InputStream, Long> pair = resizeImage(file.getInputStream(), maxSize);
        InputStream inputStream = pair.getFirst();
        Long size = pair.getSecond();

        ObjectMetadata smallMetadata = new ObjectMetadata();
        smallMetadata.setContentLength(size);
        smallMetadata.setContentType(file.getContentType());

        s3Client.putObject(bucketName, key, inputStream, smallMetadata);
    }

    private Pair<InputStream, Long> resizeImage(InputStream inputStream, int maxSize) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(inputStream)
                .size(maxSize, maxSize)
                .outputFormat(JPG)
                .toOutputStream(outputStream);

        byte[] resizedImageBytes = outputStream.toByteArray();
        InputStream resizedImageStream = new ByteArrayInputStream(resizedImageBytes);
        long sizeInBytes = resizedImageBytes.length;

        return Pair.of(resizedImageStream, sizeInBytes);
    }
}
