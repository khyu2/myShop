package study.myShop.domain.product.dto;

import lombok.Data;
import study.myShop.domain.product.entity.Category;
import study.myShop.domain.product.entity.Product;

import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private String name;
    private String itemInfo;
    private Long price;
    private Long stock;
    private Category category;
    private LocalDateTime enroll;

    public ProductResponse(Product product) {
        this.name = product.getName();
        this.itemInfo = product.getItemInfo();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.category = product.getCategory();
        this.enroll = product.getEnroll();
    }
}
