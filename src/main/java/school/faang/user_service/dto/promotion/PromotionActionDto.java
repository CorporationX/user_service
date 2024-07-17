package school.faang.user_service.dto.promotion;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Evgenii Malkov
 */
@RequiredArgsConstructor
@Data
public class PromotionActionDto {
    long typeId;
    String typeDescription;
    long availableCount;
    boolean active;
}
