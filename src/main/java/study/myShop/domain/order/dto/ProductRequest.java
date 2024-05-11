package study.myShop.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import study.myShop.domain.order.entity.Category;
import study.myShop.domain.order.entity.Product;

import java.time.LocalDateTime;

public record ProductRequest(
        @NotNull String name,
        @NotNull String itemInfo,
        @NotNull Long price,
        @NotNull Long stock,
        Category category,
        LocalDateTime enroll
) {

    public Product toEntity() {
        return Product.builder().name(name).itemInfo(itemInfo).price(price)
                .stock(stock).category(category).enroll(LocalDateTime.now()).build();
    }
}
