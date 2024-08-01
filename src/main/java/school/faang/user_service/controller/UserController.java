package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.UserService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/students")
    public List <UserDto> saveUsers(@RequestParam("students") MultipartFile file) throws IOException {

        InputStream inputStream = file.getInputStream();
        //byte[] fileContent = file.getBytes();

        //fileInputStream.readAllBytes();

        //FileInputStream fileInputStream = new FileInputStream(Objects.requireNonNull(file.getOriginalFilename()));
//        FileOutputStream fileOutputStream = new FileOutputStream("copy_photo.jpg");
//
//        System.out.println("File name: " + file.getOriginalFilename());
//
//        String fileName = file.getOriginalFilename();
//
//        bufferedReader.close();

        List <UserDto> userDtos = userService.saveUsers(inputStream);
        inputStream.close();
        return userDtos;
    }

//    @PostMapping("/students")
//    public void saveUsers(@RequestParam("students") MultipartFile file) throws IOException {
//
//        System.out.println("File name: " + file.getOriginalFilename());
//
//        String fileName = file.getOriginalFilename();
//
//        InputStream inputStream = file.getInputStream();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            stringBuilder.append(line).append("\n");
//        }
//        String students = stringBuilder.toString();
//        bufferedReader.close();
//
//        userService.saveUsers(fileName, students);
//
//        //return inputStream;
//
//        //inputStream.close();
//    }
}
