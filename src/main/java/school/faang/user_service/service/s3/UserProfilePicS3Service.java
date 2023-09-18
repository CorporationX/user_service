package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.util.ImageHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@Slf4j
public class UserProfilePicS3Service extends AbstractS3Service<UserProfilePic> {
    private final ImageHandler imageHandler;
    private final String bucketName;
    private final int maxSizePx;
    private final int smallMaxSizePx;

    public UserProfilePicS3Service(AmazonS3 amazonS3,
                                   ImageHandler imageHandler,
                                   @Value("${services.s3.bucketName}") String bucketName,
                                   @Value("${users.profile_picture.big.max_size}") int maxSizePx,
                                   @Value("${users.profile_picture.small.max_size}") int smallMaxSizePx) {
        super(amazonS3);
        this.bucketName = bucketName;
        this.imageHandler = imageHandler;
        this.maxSizePx = maxSizePx;
        this.smallMaxSizePx = smallMaxSizePx;
    }

    @Override
    public UserProfilePic upload(MultipartFile file, String folder) {
        String contentType = file.getContentType();

        byte[] bigPicture = imageHandler.resizePic(file, maxSizePx);
        String bigPictureKey = getKey(folder, maxSizePx, file.getOriginalFilename());
        ObjectMetadata bigPictureMetadata = getMetadata(contentType, bigPicture.length);
        uploadFile(bucketName, bigPictureKey, new ByteArrayInputStream(bigPicture), bigPictureMetadata);

        byte[] smallPicture = imageHandler.resizePic(file, smallMaxSizePx);
        String smallPictureKey = getKey(folder, smallMaxSizePx, file.getOriginalFilename());
        ObjectMetadata smallPictureMetadata = getMetadata(contentType, smallPicture.length);
        uploadFile(bucketName, smallPictureKey, new ByteArrayInputStream(smallPicture), smallPictureMetadata);

        return UserProfilePic.builder()
                .fileId(bigPictureKey)
                .smallFileId(smallPictureKey)
                .build();
    }

    @Override
    public InputStream download(String key) {
        return downloadFile(bucketName, key);
    }

    @Override
    public void delete(String key) {
        deleteFile(bucketName, key);
    }

    private String getKey(String folder, long size, String fileName) {
        return String.format("%s/%d_%d_%s",folder, size, System.currentTimeMillis(), fileName);
    }
}
