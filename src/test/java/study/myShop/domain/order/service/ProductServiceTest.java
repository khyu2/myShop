package study.myShop.domain.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.order.dto.ProductRequest;
import study.myShop.domain.order.dto.ProductResponse;
import study.myShop.domain.order.entity.Category;
import study.myShop.domain.order.entity.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired ProductService productService;

    @Test
    @Transactional
    void 상품저장() throws Exception {
        //given
        ProductRequest productRequest = new ProductRequest("Apple", "맛있는 사과", 2000L, 30L, Category.Food, null);

        //when
        ProductResponse productResponse = productService.create(productRequest);

        //then
        assertEquals(productRequest.name(), productResponse.getName());
        assertEquals(productRequest.stock(), productResponse.getStock());
    }

    // 상품 저장 실패 코드 작성하기

    @Test
    @Transactional
    void 상품_description_변경() throws Exception {
        //given
        ProductRequest productRequest = new ProductRequest("Apple", "맛있는 사과", 2000L, 30L, Category.Food, null);
        productService.create(productRequest);
        List<Product> allProducts = productService.getAllProducts();
        Long productId = allProducts.get(0).getId();

        //when
        ProductRequest update = new ProductRequest("Apple", "맛없는 사과 ㅜㅜ", 2000L, 20L, Category.Food, null);
        ProductResponse product = productService.update(productId, update);

        //then
        assertNotEquals(productRequest.itemInfo(), product.getItemInfo());
        assertNotEquals(productRequest.stock(), product.getStock());
    }

    @Test
    @Transactional
    void 상품_삭제() throws Exception {
        //given
        ProductRequest productRequest = new ProductRequest("Apple", "맛있는 사과", 2000L, 30L, Category.Food, null);
        productService.create(productRequest);

        //when
        productService.delete(1L);

        //then
        assertEquals(0, productService.getAllProducts().size());
    }
}