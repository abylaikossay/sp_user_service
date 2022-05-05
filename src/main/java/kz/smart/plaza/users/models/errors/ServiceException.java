package kz.smart.plaza.users.models.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ServiceException extends RuntimeException {
    protected String message;
    protected ErrorCode errorCode;
    protected HttpStatus httpStatus;
}
