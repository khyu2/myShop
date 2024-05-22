package study.myShop.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.product.exception.ProductException;
import study.myShop.domain.product.exception.ProductExceptionType;
import study.myShop.domain.product.dto.ProductRequest;
import study.myShop.domain.product.dto.ProductResponse;
import study.myShop.domain.product.entity.Product;
import study.myShop.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    public ProductResponse update(Long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
        );

        product.updateProduct(productRequest);

        return new ProductResponse(product);
    }

    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
        );

        productRepository.delete(product);
    }

    public Product getOne(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
        );
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productRepository.findAll());
    }
}
