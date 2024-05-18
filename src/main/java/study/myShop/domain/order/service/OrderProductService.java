package study.myShop.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.entity.Product;
import study.myShop.domain.order.exception.ProductException;
import study.myShop.domain.order.exception.ProductExceptionType;
import study.myShop.domain.order.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderProductService {

    private final ProductRepository productRepository;

    public List<OrderProduct> create(List<OrderProductRequest> orderProductRequestList) {
        List<OrderProduct> orderProductList = new ArrayList<>();

        for (OrderProductRequest orderProductRequest : orderProductRequestList) {
            OrderProduct orderProduct = new OrderProduct();

            Product product = productRepository.findById(orderProductRequest.orderProductId())
                    .orElseThrow(
                            () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
                    );

            orderProduct.setProduct(product);
            orderProduct.setOrderPrice(product.getPrice()); // 주문 상품의 가격 = 상품의 가격
            orderProduct.setCount(orderProductRequest.count()); // 주문 상품 개수 = 주문 요청 개수
            orderProductList.add(orderProduct);
        }

        return orderProductList;
    }
}
