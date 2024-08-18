package com.example.demo.payload.response.order;

import com.example.demo.dto.OrderItemDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a response object for retrieving order details.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetResponse {

    private List<OrderItemDTO> orderItems;

}
