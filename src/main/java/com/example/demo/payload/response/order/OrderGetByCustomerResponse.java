package com.example.demo.payload.response.order;

import com.example.demo.dto.OrderItemDTO;
import lombok.*;

import java.util.List;

/**
 * Represents a response object for retrieving orders associated with a customer, containing order details.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetByCustomerResponse {

    private List<OrderItemDTO> orderItems;

}
