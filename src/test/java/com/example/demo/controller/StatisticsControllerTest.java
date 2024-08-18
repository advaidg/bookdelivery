package com.example.demo.controller;

import com.example.demo.base.BaseControllerTest;
import com.example.demo.dto.OrderReportDTO;
import com.example.demo.model.mapper.order.OrderReportMapper;
import com.example.demo.payload.request.pagination.PaginationRequest;
import com.example.demo.payload.response.CustomPageResponse;
import com.example.demo.payload.response.CustomResponse;
import com.example.demo.payload.response.order.OrderReportResponse;
import com.example.demo.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatisticsControllerTest extends BaseControllerTest {

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void givenCustomerIdAndPaginationRequest_WhenCustomerRole_ReturnOrderReportResponse() throws Exception {

        // Given
        Long customerId = 1L;

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .page(1)
                .size(10)
                .build();

        OrderReportDTO orderReportDTO = OrderReportDTO.builder()
                .month(Month.SEPTEMBER.name())
                .year(2023)
                .totalOrderCount(100L)
                .totalBookCount(200L)
                .totalPrice(BigDecimal.valueOf(1200.90))
                .build();
        // When
        Page<OrderReportDTO> pageOfOrderReportDTOs = new PageImpl<>(Collections.singletonList(orderReportDTO));

        when(statisticsService.getOrderStatisticsByCustomerId(customerId, paginationRequest)).thenReturn(pageOfOrderReportDTOs);

        // Then
        CustomPageResponse<OrderReportResponse> customPageResponse = OrderReportMapper.toOrderReportResponseList(pageOfOrderReportDTOs);
        CustomResponse<CustomPageResponse<OrderReportResponse>> expectedResponse = CustomResponse.ok(customPageResponse);
        String monthPath = "$.response.content[0].month";
        String yearPath = "$.response.content[0].year";
        String totalOrderCountPath = "$.response.content[0].totalOrderCount";
        String totalBookCountPath = "$.response.content[0].totalBookCount";
        String isSuccessPath = "$.isSuccess";
        String httpStatusPath = "$.httpStatus";
        String timePath = "$.time";

        mockMvc.perform(get("/api/v1/statistics/{customerId}", customerId)
                        .header(HttpHeaders.AUTHORIZATION, mockUserToken)
                        .content(objectMapper.writeValueAsString(paginationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(monthPath).value(orderReportDTO.getMonth()))
                .andExpect(jsonPath(yearPath).value(orderReportDTO.getYear()))
                .andExpect(jsonPath(totalOrderCountPath).value(orderReportDTO.getTotalOrderCount()))
                .andExpect(jsonPath(totalBookCountPath).value(orderReportDTO.getTotalBookCount()))
                .andExpect(jsonPath(isSuccessPath).value(expectedResponse.getIsSuccess()))
                .andExpect(jsonPath(httpStatusPath).value(expectedResponse.getHttpStatus().name()))
                .andExpect(jsonPath(timePath).isNotEmpty());
    }

    @Test
    void givenCustomerIdAndPaginationRequest_WhenAdminRole_ReturnOrderReportResponse() throws Exception {

        // Given
        Long customerId = 1L;

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .page(1)
                .size(10)
                .build();

        OrderReportDTO orderReportDTO = OrderReportDTO.builder()
                .month(Month.SEPTEMBER.name())
                .year(2023)
                .totalOrderCount(100L)
                .totalBookCount(200L)
                .totalPrice(BigDecimal.valueOf(1200.90))
                .build();
        // When
        Page<OrderReportDTO> pageOfOrderReportDTOs = new PageImpl<>(Collections.singletonList(orderReportDTO));

        when(statisticsService.getOrderStatisticsByCustomerId(customerId, paginationRequest)).thenReturn(pageOfOrderReportDTOs);

        // Then
        CustomPageResponse<OrderReportResponse> customPageResponse = OrderReportMapper.toOrderReportResponseList(pageOfOrderReportDTOs);
        CustomResponse<CustomPageResponse<OrderReportResponse>> expectedResponse = CustomResponse.ok(customPageResponse);
        String monthPath = "$.response.content[0].month";
        String yearPath = "$.response.content[0].year";
        String totalOrderCountPath = "$.response.content[0].totalOrderCount";
        String totalBookCountPath = "$.response.content[0].totalBookCount";
        String isSuccessPath = "$.isSuccess";
        String httpStatusPath = "$.httpStatus";
        String timePath = "$.time";

        mockMvc.perform(get("/api/v1/statistics/{customerId}", customerId)
                        .header(HttpHeaders.AUTHORIZATION, mockAdminToken)
                        .content(objectMapper.writeValueAsString(paginationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(monthPath).value(orderReportDTO.getMonth()))
                .andExpect(jsonPath(yearPath).value(orderReportDTO.getYear()))
                .andExpect(jsonPath(totalOrderCountPath).value(orderReportDTO.getTotalOrderCount()))
                .andExpect(jsonPath(totalBookCountPath).value(orderReportDTO.getTotalBookCount()))
                .andExpect(jsonPath(isSuccessPath).value(expectedResponse.getIsSuccess()))
                .andExpect(jsonPath(httpStatusPath).value(expectedResponse.getHttpStatus().name()))
                .andExpect(jsonPath(timePath).isNotEmpty());
    }

    @Test
    void givenPaginationRequest_WhenAdminRole_ReturnOrderReportResponse() throws Exception {

        // Given
        PaginationRequest paginationRequest = PaginationRequest.builder()
                .page(1)
                .size(10)
                .build();

        OrderReportDTO orderReportDTO = OrderReportDTO.builder()
                .month(Month.SEPTEMBER.name())
                .year(2023)
                .totalOrderCount(100L)
                .totalBookCount(200L)
                .totalPrice(BigDecimal.valueOf(1200.90))
                .build();
        // When
        Page<OrderReportDTO> pageOfOrderReportDTOs = new PageImpl<>(Collections.singletonList(orderReportDTO));

        when(statisticsService.getAllOrderStatistics(paginationRequest)).thenReturn(pageOfOrderReportDTOs);

        // Then
        CustomPageResponse<OrderReportResponse> customPageResponse = OrderReportMapper.toOrderReportResponseList(pageOfOrderReportDTOs);
        CustomResponse<CustomPageResponse<OrderReportResponse>> expectedResponse = CustomResponse.ok(customPageResponse);
        String monthPath = "$.response.content[0].month";
        String yearPath = "$.response.content[0].year";
        String totalOrderCountPath = "$.response.content[0].totalOrderCount";
        String totalBookCountPath = "$.response.content[0].totalBookCount";
        String isSuccessPath = "$.isSuccess";
        String httpStatusPath = "$.httpStatus";
        String timePath = "$.time";

        mockMvc.perform(get("/api/v1/statistics")
                        .header(HttpHeaders.AUTHORIZATION, mockAdminToken)
                        .content(objectMapper.writeValueAsString(paginationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(monthPath).value(orderReportDTO.getMonth()))
                .andExpect(jsonPath(yearPath).value(orderReportDTO.getYear()))
                .andExpect(jsonPath(totalOrderCountPath).value(orderReportDTO.getTotalOrderCount()))
                .andExpect(jsonPath(totalBookCountPath).value(orderReportDTO.getTotalBookCount()))
                .andExpect(jsonPath(isSuccessPath).value(expectedResponse.getIsSuccess()))
                .andExpect(jsonPath(httpStatusPath).value(expectedResponse.getHttpStatus().name()))
                .andExpect(jsonPath(timePath).isNotEmpty());
    }
}
