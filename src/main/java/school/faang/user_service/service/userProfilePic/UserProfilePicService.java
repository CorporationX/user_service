package school.faang.user_service.service.userProfilePic;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfile.UserProfileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.userProfilePic.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfilePicService {

    //    private final UserContext userContext;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final UserProfilePicMapper userProfilePicMapper;

    public UserProfileDto addImageInProfile(Long userId, MultipartFile multipartFile) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователя по id: {} нет в баззе данных", userId);
            return new EntityNotFoundException("Пользователя по такому id нет в базе данных");
        });

        String folder = user.getId() + user.getUsername();
        UserProfilePic userProfilePic = s3Service.uploadProfile(multipartFile, folder);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return userProfilePicMapper.toDto(user);
    }
    // вариант с 2 фото и списком
//    public List<byte[]> getImageFromProfile(Long userId) throws IOException {
//        User user = userRepository.findById(userId).orElseThrow(() -> {
//            log.error("Пользователя по id: {} нет в баззе данных", userId);
//            return new EntityNotFoundException("Пользователя по такому id нет в базе данных");
//        });
//
//        List<byte[]> listArrayByte = new ArrayList<>();
//        listArrayByte.add(s3Service.downloadingByteImage(user.getUserProfilePic().getFileId()).readAllBytes());
//        listArrayByte.add(s3Service.downloadingByteImage(user.getUserProfilePic().getSmallFileId()).readAllBytes());
//
//        return listArrayByte;
//    }

    // вариант с 1 фото
    public InputStream getImageFromProfile(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователя по id: {} нет в баззе данных", userId);
            return new EntityNotFoundException("Пользователя по такому id нет в базе данных");
        });
        return s3Service.downloadingByteImage(user.getUserProfilePic().getFileId());
    }
    public UserProfileDto deleteImageFromProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователя по id: {} нет в баззе данных", userId);
            return new EntityNotFoundException("Пользователя по такому id нет в базе данных");
        });

        s3Service.deleteImage(user.getUserProfilePic());
        user.setUserProfilePic(null);
        userRepository.save(user);

        return userProfilePicMapper.toDto(user);
    }
}
