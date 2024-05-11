package study.myShop.domain.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.myShop.domain.order.dto.ProductRequest;
import study.myShop.domain.order.dto.ProductResponse;
import study.myShop.domain.order.entity.Product;
import study.myShop.domain.order.exception.ProductException;
import study.myShop.domain.order.exception.ProductExceptionType;
import study.myShop.domain.order.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        if (productRepository.existsByName(productRequest.name())) {
            throw new ProductException(ProductExceptionType.ALREADY_EXIST_PRODUCT);
        }

        Product product = productRequest.toEntity();
        productRepository.save(product);

        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
        );

        product.updateProduct(productRequest);

        return new ProductResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
        );

        productRepository.delete(product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productRepository.findAll());
    }
}
