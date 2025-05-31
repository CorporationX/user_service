package school.faang.user_service.entity.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_number", length = 32, nullable = false, unique = true)
    private Long transactionNumber;

    @Column(name = "amount", length = 32, nullable = false)
    private BigDecimal amount;

    @Column(name = "message")
    private String message;

    @Column(name = "currency", length = 3, nullable = false)
    private Currency currencyCode;

    @Column(name = "verification_code", length = 4)
    private Integer verificationCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creating_date")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "type", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionPurpose  purpose;

    @Column(name = "product_code", length = 64, nullable = false)
    private String purchaseItem;

    @Column(name = "transaction_status", length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
}
