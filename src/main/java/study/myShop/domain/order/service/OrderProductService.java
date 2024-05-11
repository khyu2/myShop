package study.myShop.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.repository.OrderProductRepository;
import study.myShop.domain.order.repository.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    List<OrderProduct> create(List<OrderProductRequest> orderProductRequests) {
        List<OrderProduct> orderProducts = new ArrayList<>();

        Map<Long, Long> productIdMap = new HashMap<>();

        for (OrderProductRequest orderProductRequest : orderProductRequests) {
            if (productIdMap.get(orderProductRequest.orderProductId()) != null) {
                productIdMap.put(orderProductRequest.orderProductId(),
                        productIdMap.get(orderProductRequest.orderProductId())
                                + orderProductRequest.count());
            }
        }
        return null;
    }
}
