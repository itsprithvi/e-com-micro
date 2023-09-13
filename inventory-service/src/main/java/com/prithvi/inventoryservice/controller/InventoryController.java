package com.prithvi.inventoryservice.controller;


import com.prithvi.inventoryservice.dto.InventoryResponse;
import com.prithvi.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // http://localhost:8082/api/inventory/iphone-13,iphone-15
    // http://localhost:8082/api/inventory?skuCode=iphone-15&skuCode=iphone15-red // this is the list of sku
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        System.out.println("This inventory method is called...");
        return inventoryService.isInStock(skuCode);
    }
}
