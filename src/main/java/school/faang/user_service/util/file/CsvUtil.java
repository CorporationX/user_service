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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CsvUtil {

    private static final CsvMapper csvMapper = new CsvMapper();

    public <T> List<T> parseCsvMultipartFile(MultipartFile multipartFile, Class<T> clazz) {
        log.info("Start parseCsvMultipartFile() - {}", multipartFile.getOriginalFilename());

        List<CompletableFuture<T>> completableFuture = new ArrayList<>();
        int linesParsed = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            CsvSchema csvSchema = generateCsvSchema(headerLine);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                linesParsed++;
                String finalLine = line;
                completableFuture.add(CompletableFuture.supplyAsync(() ->
                        parseCsvLineToPojo(headerLine.concat("\n" + finalLine), clazz, csvSchema)));
/*
finalHeaderLine.concat("\n" + finalLine) - костыль)
никак не получается решить проблему парсинга csv, если передаем просто данные то вываливается ошибка
Проблема в том что данные ожидаются вместе с хедером.
Я не нашел как отдельно сделать схему для хедера и просто уже ее передавать например.
(Вроде уже даже и схему передаю, все равно не хочет)
com.fasterxml.jackson.databind.exc.MismatchedInputException: No content to map due to end-of-input
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

    private CsvSchema generateCsvSchema(String headerLine) {
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();

        for (String header : headerLine.split(",")) {
            schemaBuilder.addColumn(header.trim());
        }

        return schemaBuilder.build().withHeader();
    }

    private <T> T parseCsvLineToPojo(String line, Class<T> clazz, CsvSchema csvSchema) {
        try {
            return csvMapper.readerFor(clazz).with(csvSchema).readValue(line);
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
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
            throw new ParseException(e.getMessage());
        }
    }
}