package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;

public record ResourceDto(
        Long id,
        @NotNull
        @Min(0)
        BigInteger size,
        String fileId,
        String smallFileId
) {}
