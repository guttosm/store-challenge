package com.jumbo.store.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ErrorResponse", description = "Error response structure")
public class ErrorResponse {

    @JsonProperty(index = 1, value = "status")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private HttpStatus httpStatus;

    @JsonProperty(index = 2, value = "code")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer httpCode;

    @JsonProperty(index = 3, value = "message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonProperty(index = 4, value = "details")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> details;

    @JsonProperty(index = 5, value = "timestamp")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LocalDateTime timestamp;
}
