package school.faang.user_service.parser;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileParser<T> {
    List<T> parse(MultipartFile file);
}
