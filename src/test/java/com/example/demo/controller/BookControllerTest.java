package com.example.demo.controller;

import com.example.demo.base.BaseControllerTest;
import com.example.demo.builder.BookBuilder;
import com.example.demo.dto.BookDTO;
import com.example.demo.model.Book;
import com.example.demo.model.mapper.book.BookMapper;
import com.example.demo.payload.request.book.BookCreateRequest;
import com.example.demo.payload.request.book.BookUpdateRequest;
import com.example.demo.payload.request.book.BookUpdateStockRequest;
import com.example.demo.payload.request.pagination.PaginationRequest;
import com.example.demo.payload.response.CustomPageResponse;
import com.example.demo.payload.response.CustomResponse;
import com.example.demo.payload.response.book.BookCreatedResponse;
import com.example.demo.payload.response.book.BookGetResponse;
import com.example.demo.payload.response.book.BookUpdatedResponse;
import com.example.demo.service.impl.BookServiceImpl;
import com.example.demo.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookControllerTest extends BaseControllerTest {

    private static final String RESPONSE_ID = "$.response.id";
    private static final String RESPONSE_ISBN = "$.response.isbn";
    private static final String RESPONSE_NAME = "$.response.name";
    private static final String RESPONSE_AUTHOR_FULL_NAME = "$.response.authorFullName";
    private static final String RESPONSE_STOCK = "$.response.stock";
    private static final String IS_SUCCESS = "$.isSuccess";
    private static final String HTTP_STATUS = "$.httpStatus";
    private static final String TIME = "$.time";

    @MockBean
    private BookServiceImpl bookService;

    @Test
    void givenCreateBookRequest_whenAdminRole_ReturnBookCreatedResponse() throws Exception {

        // Given
        BookCreateRequest mockRequest = BookCreateRequest.builder()
                .authorFullName("Author full name")
                .name("name")
                .isbn("1234567890")
                .stock(123)
                .price(BigDecimal.TEN)
                .build();

        Book book = new BookBuilder().withValidFields().build();

        BookDTO bookDTO = BookMapper.toDTO(book);

        // When
        BookCreatedResponse bookCreatedResponse = BookMapper.toCreatedResponse(bookDTO);
        CustomResponse<BookCreatedResponse> customResponseOfBookCreatedResponse = CustomResponse.created(bookCreatedResponse);

        Mockito.when(bookService.createBook(mockRequest)).thenReturn(bookDTO);

        // Then
        mockMvc.perform(post("/api/v1/books")
                        .header(HttpHeaders.AUTHORIZATION, mockAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(RESPONSE_ID).value(bookCreatedResponse.getId()))
                .andExpect(jsonPath(RESPONSE_ISBN).value(bookCreatedResponse.getIsbn()))
                .andExpect(jsonPath(RESPONSE_NAME).value(bookCreatedResponse.getName()))
                .andExpect(jsonPath(RESPONSE_AUTHOR_FULL_NAME).value(bookCreatedResponse.getAuthorFullName()))
                .andExpect(jsonPath(RESPONSE_STOCK).value(bookCreatedResponse.getStock()))
                .andExpect(jsonPath(IS_SUCCESS).value(customResponseOfBookCreatedResponse.getIsSuccess()))
                .andExpect(jsonPath(HTTP_STATUS).value(customResponseOfBookCreatedResponse.getHttpStatus().name()))
                .andExpect(jsonPath(TIME).isNotEmpty());
    }

    @Test
    void givenBookId_whenAdminRoleAndBookFound_ReturnBookGetResponse() throws Exception {

        // given
        String bookId = RandomUtil.generateUUID();

        Book book = new BookBuilder().withValidFields().build();

        BookDTO bookDTO = BookMapper.toDTO(book);
        BookGetResponse bookGetResponse = BookMapper.toGetResponse(bookDTO);

        // when
        Mockito.when(bookService.getBookById(bookId)).thenReturn(bookDTO);

        // then
        CustomResponse<BookGetResponse> customResponseOfBookGetResponse = CustomResponse.ok(bookGetResponse);
        mockMvc.perform(get("/api/v1/books/{orderId}", bookId)
                        .header(HttpHeaders.AUTHORIZATION, mockAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookGetResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(RESPONSE_ID).value(bookGetResponse.getId()))
                .andExpect(jsonPath(RESPONSE_ISBN).value(bookGetResponse.getIsbn()))
                .andExpect(jsonPath(RESPONSE_NAME).value(bookGetResponse.getName()))
                .andExpect(jsonPath(RESPONSE_AUTHOR_FULL_NAME).value(bookGetResponse.getAuthorFullName()))
                .andExpect(jsonPath(RESPONSE_STOCK).value(bookGetResponse.getStock()))
                .andExpect(jsonPath(IS_SUCCESS).value(customResponseOfBookGetResponse.getIsSuccess()))
                .andExpect(jsonPath(HTTP_STATUS).value(customResponseOfBookGetResponse.getHttpStatus().getReasonPhrase()))
                .andExpect(jsonPath(TIME).isNotEmpty());

    }

    @Test
    void givenBookId_whenCustomerRoleAndBookFound_ReturnBookGetResponse() throws Exception {

        // given
        String bookId = RandomUtil.generateUUID();

        Book book = new BookBuilder().withValidFields().build();

        BookDTO bookDTO = BookMapper.toDTO(book);
        BookGetResponse bookGetResponse = BookMapper.toGetResponse(bookDTO);

        // when
        Mockito.when(bookService.getBookById(bookId)).thenReturn(bookDTO);

        // then
        CustomResponse<BookGetResponse> customResponseOfBookGetResponse = CustomResponse.ok(bookGetResponse);
        mockMvc.perform(get("/api/v1/books/{orderId}", bookId)
                        .header(HttpHeaders.AUTHORIZATION, mockUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookGetResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(RESPONSE_ID).value(bookGetResponse.getId()))
                .andExpect(jsonPath(RESPONSE_ISBN).value(bookGetResponse.getIsbn()))
                .andExpect(jsonPath(RESPONSE_NAME).value(bookGetResponse.getName()))
                .andExpect(jsonPath(RESPONSE_AUTHOR_FULL_NAME).value(bookGetResponse.getAuthorFullName()))
                .andExpect(jsonPath(RESPONSE_STOCK).value(bookGetResponse.getStock()))
                .andExpect(jsonPath(IS_SUCCESS).value(customResponseOfBookGetResponse.getIsSuccess()))
                .andExpect(jsonPath(HTTP_STATUS).value(customResponseOfBookGetResponse.getHttpStatus().getReasonPhrase()))
                .andExpect(jsonPath(TIME).isNotEmpty());

    }

    @Test
    void givenPaginatedFindAllRequest_whenCustomerRoleAndBookListFound_ReturnPageResponseBookGetResponse() throws Exception {

        // given
        String bookId1 = RandomUtil.generateUUID();
        String bookId2 = RandomUtil.generateUUID();

        Book book1 = new BookBuilder().withValidFields().withId(bookId1).build();

        Book book2 = new BookBuilder().withValidFields().withId(bookId2).build();

        BookDTO bookDTO1 = BookMapper.toDTO(book1);
        BookDTO bookDTO2 = BookMapper.toDTO(book2);

        PaginationRequest request = new PaginationRequest(1, 10);

        Page<BookDTO> mockPageOfOrderDTOs = new PageImpl<>(List.of(bookDTO1, bookDTO2));

        CustomPageResponse<BookGetResponse> customPageResponseOfBookGetResponse = BookMapper.toGetResponse(mockPageOfOrderDTOs);

        // when
        Mockito.when(bookService.getAllBooks(any(PaginationRequest.class))).thenReturn(mockPageOfOrderDTOs);

        // then
        CustomResponse<CustomPageResponse<BookGetResponse>> response = CustomResponse.ok(customPageResponseOfBookGetResponse);
        mockMvc.perform(post("/api/v1/books/all")
                        .header(HttpHeaders.AUTHORIZATION, mockUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content[0].id").value(bookId1))
                .andExpect(jsonPath("$.response.content[1].id").value(bookId2))
                .andExpect(jsonPath(IS_SUCCESS).value(response.getIsSuccess()))
                .andExpect(jsonPath(HTTP_STATUS).value(response.getHttpStatus().getReasonPhrase()))
                .andExpect(jsonPath(TIME).isNotEmpty());

    }

    @Test
    void givenPaginatedFindAllRequest_whenAdminRoleAndBookListFound_ReturnPageResponseBookGetResponse() throws Exception {

        // given
        String bookId1 = RandomUtil.generateUUID();
        String bookId2 = RandomUtil.generateUUID();

        Book book1 = new BookBuilder().withValidFields().withId(bookId1).build();

        Book book2 = new BookBuilder().withValidFields().withId(bookId2).build();

        BookDTO bookDTO1 = BookMapper.toDTO(book1);
        BookDTO bookDTO2 = BookMapper.toDTO(book2);

        PaginationRequest request = new PaginationRequest(1, 10);

        Page<BookDTO> mockPageOfOrderDTOs = new PageImpl<>(List.of(bookDTO1, bookDTO2));

        CustomPageResponse<BookGetResponse> customPageResponseOfBookGetResponse = BookMapper.toGetResponse(mockPageOfOrderDTOs);

        // when
        Mockito.when(bookService.getAllBooks(any(PaginationRequest.class))).thenReturn(mockPageOfOrderDTOs);

        // then
        CustomResponse<CustomPageResponse<BookGetResponse>> response = CustomResponse.ok(customPageResponseOfBookGetResponse);
        mockMvc.perform(post("/api/v1/books/all")
                        .header(HttpHeaders.AUTHORIZATION, mockAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content[0].id").value(bookId1))
                .andExpect(jsonPath("$.response.content[1].id").value(bookId2))
                .andExpect(jsonPath(IS_SUCCESS).value(response.getIsSuccess()))
                .andExpect(jsonPath(HTTP_STATUS).value(response.getHttpStatus().getReasonPhrase()))
                .andExpect(jsonPath(TIME).isNotEmpty());

    }

    @Test
    void givenBookIdAndBookUpdateStockRequest_whenAdminRoleAndBookFound_ReturnBookUpdatedResponse() throws Exception {

        // given
        Book book = new BookBuilder().withValidFields().build();
        String bookId = book.getId();

        BookUpdateStockRequest bookUpdateStockRequest = BookUpdateStockRequest.builder()
                .stock(5)
                .build();

        BookDTO updatedBookEntity = BookDTO.builder()
                .stock(bookUpdateStockRequest.getStock())
                .build();

        BookUpdatedResponse bookUpdatedResponse = BookMapper.toUpdatedResponse(updatedBookEntity);

        // when
        Mockito.when(bookService.updateBookStockById(bookId, bookUpdateStockRequest)).thenReturn(updatedBookEntity);


        // then
        CustomResponse<BookUpdatedResponse> customResponseOfBookUpdatedResponse = CustomResponse.ok(bookUpdatedResponse);

        mockMvc.perform(put("/api/v1/books/stock-amount/{bookId}", bookId)
                        .header(HttpHeaders.AUTHORIZATION, mockAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdateStockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(RESPONSE_ID).value(bookUpdatedResponse.getId()))
                .andExpect(jsonPath(RESPONSE_ISBN).value(bookUpdatedResponse.getIsbn()))
                .andExpect(jsonPath(RESPONSE_NAME).value(bookUpdatedResponse.getName()))
                .andExpect(jsonPath(RESPONSE_AUTHOR_FULL_NAME).value(bookUpdatedResponse.getAuthorFullName()))
                .andExpect(jsonPath(RESPONSE_STOCK).value(bookUpdatedResponse.getStock()))
                .andExpect(jsonPath(IS_SUCCESS).value(customResponseOfBookUpdatedResponse.getIsSuccess()))
                .andExpect(jsonPath(HTTP_STATUS).value(customResponseOfBookUpdatedResponse.getHttpStatus().getReasonPhrase()))
                .andExpect(jsonPath(TIME).isNotEmpty());

    }

    @Test
    void givenBookIdAndBookUpdateRequest_whenAdminRoleAndBookFound_ReturnBookUpdatedResponse() throws Exception {

        // given
        String bookId = RandomUtil.generateUUID();
        Book book = new BookBuilder().withValidFields().withId(bookId).build();

        BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                .name("Update Book Name")
                .authorFullName("Update Book Author Full Name")
                .isbn("Update ISBN")
                .price(BigDecimal.valueOf(69.99))
                .build();

        BookDTO updatedBook = BookMapper.toDTO(book);
        BookUpdatedResponse bookUpdatedResponse = BookMapper.toUpdatedResponse(updatedBook);

        // when
        Mockito.when(bookService.updateBookById(bookId, updateRequest)).thenReturn(updatedBook);

        // then
        CustomResponse<BookUpdatedResponse> customResponseOfBookUpdatedResponse = CustomResponse.ok(bookUpdatedResponse);

        mockMvc.perform(put("/api/v1/books/{bookId}", bookId)
                        .header(HttpHeaders.AUTHORIZATION, mockAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(RESPONSE_ID).value(bookUpdatedResponse.getId()))
                .andExpect(jsonPath(RESPONSE_ISBN).value(bookUpdatedResponse.getIsbn()))
                .andExpect(jsonPath(RESPONSE_NAME).value(bookUpdatedResponse.getName()))
                .andExpect(jsonPath(RESPONSE_AUTHOR_FULL_NAME).value(bookUpdatedResponse.getAuthorFullName()))
                .andExpect(jsonPath(RESPONSE_STOCK).value(bookUpdatedResponse.getStock()))
                .andExpect(jsonPath(IS_SUCCESS).value(customResponseOfBookUpdatedResponse.getIsSuccess()))
                .andExpect(jsonPath(HTTP_STATUS).value(customResponseOfBookUpdatedResponse.getHttpStatus().getReasonPhrase()))
                .andExpect(jsonPath(TIME).isNotEmpty());
    }

}
