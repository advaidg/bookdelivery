package com.example.demo.payload.response.order;

import com.example.demo.dto.OrderItemDTO;
import lombok.*;

import java.util.List;

/**
 * Represents a response object for order creation, containing order details.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedResponse {

    private List<OrderItemDTO> orderItems;
}
