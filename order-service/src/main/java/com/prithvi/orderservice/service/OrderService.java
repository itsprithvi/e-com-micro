package com.prithvi.orderservice.service;


import com.prithvi.orderservice.dto.InventoryResponse;
import com.prithvi.orderservice.dto.OrderLineItemsDto;
import com.prithvi.orderservice.dto.OrderRequest;
import com.prithvi.orderservice.model.Order;
import com.prithvi.orderservice.model.OrderLineItems;
import com.prithvi.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {
        System.out.println("Order creating...");

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());


        List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList()
                                                    .stream()
                                                    .map(this::mapToDto)
                                                    .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes =  order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        System.out.println("sending request to inventory....");
        // call inventory service and check and place the order
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                                  .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes)
                                            .build())
                                            .retrieve()
                                            .bodyToMono(InventoryResponse[].class)
                                            .block();
        System.out.println("received response from inventory....");

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if(allProductsInStock) {
            orderRepository.save(order);
            return "Order placed successfully...";
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());


        return orderLineItems;
    }
}
