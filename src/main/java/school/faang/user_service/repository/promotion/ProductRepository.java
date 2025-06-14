package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
