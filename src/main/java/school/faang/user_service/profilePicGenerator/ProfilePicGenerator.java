package school.faang.user_service.profilePicGenerator;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import school.faang.user_service.client.AmazonS3Client;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ProfilePicGenerator {

    public static String generateProfilePic(String nameFirstLetter, String surnameFirstLetter, Long id){
        String seed = nameFirstLetter + surnameFirstLetter;
        String profilePicName = seed + id;
        String avatarUrl = "https://api.dicebear.com/6.x/initials/svg?seed=" + seed
                + "&radius=20&backgroundType=gradientLinear";
        System.out.println(avatarUrl);
        String pic = getPic(avatarUrl);
        MultipartFile profilePicFile = convertToMultipartFile(pic, profilePicName);

        uploadToMinioBucket(profilePicFile);

        return avatarUrl;
    }

    private static String getPic(String picUrl){
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
        return new ByteArrayMultipartFile(bytes, "pic", fileName, "image/svg+xml");
    }

    private static void uploadToMinioBucket(MultipartFile file){
        String bucketName = "corpbucket";
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());

        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(file.getBytes());

        PutObjectResult objectResult = AmazonS3Client.getClient().putObject(
                "myBucket", file.getOriginalFilename(), file.getInputStream(), data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}