package study.myShop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import study.myShop.domain.order.dto.ProductRequest;
import study.myShop.domain.order.exception.ProductException;
import study.myShop.domain.order.exception.ProductExceptionType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;
    private String itemInfo;
    private Long price;
    private Long stock;

    @Enumerated(EnumType.STRING)
    private Category category;

    private LocalDateTime enroll;

    public void removeStock(Long count) {
        long restStock = this.stock - count;

        if (restStock < 0) {
            throw new ProductException(ProductExceptionType.ALREADY_EXIST_PRODUCT);
        }

        this.stock = restStock;
    }

    public void updateProduct(ProductRequest request) {
        this.name = request.name();
        this.itemInfo = request.itemInfo();
        this.price = request.price();
        this.stock = request.stock();
        this.category = request.category();
    }
}
