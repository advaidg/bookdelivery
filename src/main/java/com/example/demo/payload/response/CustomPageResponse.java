package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Represents a custom response containing paginated data.
 *
 * @param <T> The type of content elements in the response.
 */
@Getter
@Builder
public class CustomPageResponse<T> {

    /**
     * The list of content elements in the response.
     */
    private List<T> content;


    /**
     * Creates a CustomPageResponse from a Spring Data Page object.
     *
     * @param page The Spring Data Page object to convert.
     * @param <T>  The type of content elements in the response.
     * @return A CustomPageResponse containing paginated data.
     */
    public static <T> CustomPageResponse<T> of(Page<T> page) {
        return CustomPageResponse.<T>builder()
                .content(page.getContent())
                .build();
    }

}
