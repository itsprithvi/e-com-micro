package com.prithvi.orderservice.service;


import com.prithvi.orderservice.dto.OrderLineItemsDto;
import com.prithvi.orderservice.dto.OrderRequest;
import com.prithvi.orderservice.model.Order;
import com.prithvi.orderservice.model.OrderLineItems;
import com.prithvi.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());


        List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList()
                                                    .stream()
                                                    .map(this::mapToDto)
                                                    .toList();

        order.setOrderLineItemsList(orderLineItems);

        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());

        return orderLineItems;
    }
}
