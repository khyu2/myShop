package study.myShop.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import study.myShop.domain.exception.ProductException;
import study.myShop.domain.exception.ProductExceptionType;
import study.myShop.domain.product.dto.ProductRequest;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private Category category;

    private LocalDateTime enroll;

    public Product(Long id, String name, String itemInfo, Long price, Long stock, Category category, LocalDateTime enroll) {
        this.id = id;
        this.name = name;
        this.itemInfo = itemInfo;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.enroll = enroll;
    }

    public void addStock(Long quantity) {
        this.stock += quantity;
    }

    public void removeStock(Long quantity) {
        long restStock = this.stock - quantity;

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

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", itemInfo='" + itemInfo + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
