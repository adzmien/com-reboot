package com.reboot.uam.lib.web;

import com.reboot.uam.lib.common.ApiResponse;
import com.reboot.uam.lib.common.ErrorDetail;
import com.reboot.uam.lib.common.ErrorDetail.ViolationItem;
import com.reboot.uam.lib.exception.BusinessRuleException;
import com.reboot.uam.lib.exception.BusinessRuleViolationsException;
import com.reboot.uam.lib.exception.DuplicateResourceException;
import com.reboot.uam.lib.exception.ForbiddenException;
import com.reboot.uam.lib.exception.ResourceNotFoundException;
import com.reboot.uam.lib.exception.ServiceCommunicationException;
import com.reboot.uam.lib.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler that maps every {@code RebootException} subtype
 * and Spring MVC validation exceptions to a structured {@link ApiResponse}.
 * Services inherit this handler by having {@code reboot-common-lib} on their classpath
 * and enabling component scanning for the {@code com.reboot.uam.lib.web} package.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR_CODE = "VALIDATION-001";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Void> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found. code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateResourceException.class)
    public ApiResponse<Void> handleDuplicate(DuplicateResourceException ex) {
        log.warn("Duplicate resource. code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BusinessRuleViolationsException.class)
    public ApiResponse<Void> handleBusinessRuleViolations(BusinessRuleViolationsException ex) {
        log.warn("Business rule violations. code={}, count={}", ex.getCode(), ex.getViolations().size());
        return ApiResponse.error(
                new ErrorDetail(ex.getCode(), ex.getMessage(), ex.getViolations()),
                ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BusinessRuleException.class)
    public ApiResponse<Void> handleBusinessRule(BusinessRuleException ex) {
        log.warn("Business rule violation. code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ApiResponse<Void> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized. code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ApiResponse<Void> handleForbidden(ForbiddenException ex) {
        log.warn("Forbidden. code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(ServiceCommunicationException.class)
    public ApiResponse<Void> handleServiceCommunication(ServiceCommunicationException ex) {
        log.error("Service communication failure. code={}, message={}", ex.getCode(), ex.getMessage(), ex);
        return ApiResponse.error(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of()), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        List<ViolationItem> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toViolationItem)
                .toList();
        String message = "Request validation failed";
        log.warn("Validation failed. violations={}", violations.size());
        return ApiResponse.error(new ErrorDetail(VALIDATION_ERROR_CODE, message, violations), message);
    }

    private ViolationItem toViolationItem(FieldError fieldError) {
        return ViolationItem.ofField(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
