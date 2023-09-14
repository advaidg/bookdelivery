package com.example.demo.service.impl;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.exception.book.UserNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.model.mapper.order.OrderMapper;
import com.example.demo.model.mapper.user.UserMapper;
import com.example.demo.payload.request.order.CreateOrderRequest;
import com.example.demo.repository.OrderRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.OrderItemService;
import com.example.demo.service.OrderSaveService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderSaveServiceImpl implements OrderSaveService {

    private final OrderItemService orderItemService;

    private final UserService userService;

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderDTO createOrder(CreateOrderRequest createOrderRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        User user = userService.findByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(customUserDetails.getId())));

        UserDTO userDTO = UserMapper.toDTO(user);

        Set<OrderItemDTO> orderDetailDTOSet = new HashSet<>();
        createOrderRequest.getOrderDetailSet().stream().forEach(
                orderItem -> orderDetailDTOSet.add(orderItemService.createOrderItem(orderItem))
        );

        OrderDTO orderDTO = OrderDTO.builder()
                .user(userDTO)
                .orderItems(orderDetailDTOSet)
                .createdAt(LocalDateTime.now())
                .build();

        Order order = OrderMapper.toOrder(orderDTO);
        order.setCreatedAt(LocalDateTime.now()); // TODO : ADD createdAt to Order

        Order orderCompleted = orderRepository.save(order);

        return OrderMapper.toOrderDTO(orderCompleted);
    }
}
