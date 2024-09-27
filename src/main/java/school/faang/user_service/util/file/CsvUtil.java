package school.faang.user_service.util.file;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CsvUtil {

    public <T> List<T> parseCsvMultipartFile(MultipartFile multipartFile, Class<T> clazz) {
        log.info("Start parseCsvMultipartFile() - {}", multipartFile.getOriginalFilename());

        List<CompletableFuture<T>> completableFuture = new ArrayList<>();
        int linesParsed = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.trim().isEmpty()) {
                log.error("Exception while parsing - {}. Empty header line", multipartFile.getOriginalFilename());
                throw new ParseException("Empty header line");
            }
            headerLine = headerLine.substring(3);
            //Жесткий костыль, я не понимаю откуда в начале строки появились символы ï»¿

            String finalHeaderLine = headerLine;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                linesParsed++;
                String finalLine = line;
                completableFuture.add(CompletableFuture.supplyAsync(() ->
                        parseCsvLineToPojo(finalHeaderLine.concat("\n" + finalLine), clazz)));
                /*
                finalHeaderLine.concat("\n" + finalLine) - еще костыль)
                никак не получается решить проблему парсинга csv, если передаем просто данные то вываливается ошибка
                много ошибок разных. Проблема в том что данные ожидаются вместе с хедером.
                Я не нашел как отдельно сделать схему для хедера и просто уже ее передавать например.
                 */
            }

        } catch (IOException e) {
            log.error("Exception while parsing - {}. {}", multipartFile.getOriginalFilename(), e.getMessage());
            throw new ParseException("Error reading multipart file " + multipartFile.getName());
        }

        List<T> result = completableFuture.stream()
                .map(CompletableFuture::join)
                .toList();

        if (linesParsed == result.size()) {
            log.info("Finish parseCsvMultipartFile() - {}", multipartFile.getOriginalFilename());
            return result;
        } else {
            log.error("Expected objects: {}, but parsed - {}", linesParsed, result.size());
            throw new ParseException("Discrepancy between expected parsed lines and obtained lines");
        }
    }

    private <T> T parseCsvLineToPojo(String line, Class<T> clazz) {
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            return csvMapper.readerFor(clazz).with(schema).readValue(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> parseCsvToPojo(MultipartFile multipartFile, @Valid Class<T> clazz) {
        try {
            return new CsvMapper()
                    .readerWithSchemaFor(clazz)
                    .with(CsvSchema.emptySchema().withHeader())
                    .<T>readValues(multipartFile.getBytes())
                    .readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}