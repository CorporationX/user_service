package school.faang.user_service.profilePicGenerator;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import school.faang.user_service.client.MinIOClient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class ProfilePicGenerator {

    public static String generateProfilePic(String nameFirstLetter, String surnameFirstLetter, Long id){
        String bucketName = "corpbucket";
        String seed = nameFirstLetter + surnameFirstLetter;
        String profilePicName = seed + id;
        String generatedPicUrl = "https://api.dicebear.com/6.x/initials/svg?seed=" + seed
                + "&radius=20&backgroundType=gradientLinear";
        String pic = getPicFromUrl(generatedPicUrl);

        MultipartFile profilePicFile = convertToMultipartFile(pic, profilePicName);

        uploadToMinioBucket(profilePicFile, bucketName);

       String picUrl = getUrl(profilePicFile, bucketName);
       System.out.println(picUrl);

        return generatedPicUrl;
    }

    private static String getPicFromUrl(String picUrl){
        StringBuilder picBuilder = new StringBuilder();
        try (InputStream inputStream = new URL(picUrl).openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)){
            String line;
            while ((line = reader.readLine()) != null){
                picBuilder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return picBuilder.toString();
    }

    private static MultipartFile convertToMultipartFile(String pic, String fileName){
        byte[] bytes = pic.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayMultipartFile(bytes, fileName + ".svg", fileName, "image/svg+xml");
    }

    private static void uploadToMinioBucket(MultipartFile file, String bucketName){
        MinioClient minioClient = MinIOClient.getClient();
        try {
            //ByteBuffer byteBuffer = ByteBuffer.wrap(file.getBytes());

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object("profilePics/" + file.getName())
                    .contentType(file.getContentType())
                            .stream(file.getInputStream(),-1, 10485760)
                    .build());

        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException
                | XmlParserException e) {
            e.printStackTrace();
        }
    }

    private static String getUrl(MultipartFile profilePicFile, String bucketName){
        MinioClient minioClient = MinIOClient.getClient();
        String url = null;
        try {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object("profilePics/" + profilePicFile.getName())
                            .build());
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException
                | XmlParserException e) {
            e.printStackTrace();
        }

        return url;
    }
}