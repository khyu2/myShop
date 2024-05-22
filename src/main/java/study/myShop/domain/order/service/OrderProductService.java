package study.myShop.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.repository.OrderProductRepository;
import study.myShop.domain.product.entity.Product;
import study.myShop.domain.product.exception.ProductException;
import study.myShop.domain.product.exception.ProductExceptionType;
import study.myShop.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    public List<OrderProduct> create(List<OrderProductRequest> orderProductRequestList) {
        List<OrderProduct> orderProductList = new ArrayList<>();

        // 주문 상품을 확인하며 상품이 존재하는지 확인 후 저장
        for (OrderProductRequest orderProductRequest : orderProductRequestList) {
            Product product = productRepository.findById(orderProductRequest.orderProductId())
                    .orElseThrow(
                            () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
                    );

            OrderProduct orderProduct = OrderProduct.createOrderProduct(product, orderProductRequest.count());
            orderProductList.add(orderProduct);
        }

        orderProductRepository.saveAll(orderProductList);
        return orderProductList;
    }
}
