package com.reboot.uam.lib;

import com.reboot.uam.lib.exception.ServiceNotFoundException;
import com.reboot.uam.lib.response.ApiResponse;
import com.reboot.uam.lib.web.BaseRestControllerAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @RestControllerAdvice
    static class TestAdvice extends BaseRestControllerAdvice {}

    private final TestAdvice advice = new TestAdvice();

    @Test
    void ok_response_has_success_true_and_data() {
        ApiResponse<String> response = ApiResponse.ok("hello");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("hello");
        assertThat(response.getErrorCode()).isNull();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void error_response_has_success_false_and_code() {
        ApiResponse<Void> response = ApiResponse.error("UAM-001", "Not found");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getErrorCode()).isEqualTo("UAM-001");
        assertThat(response.getMessage()).isEqualTo("Not found");
    }

    @Test
    void reboot_exception_maps_to_correct_http_status_and_error_code() {
        ServiceNotFoundException ex = new ServiceNotFoundException("User not found", "UAM-001");

        ResponseEntity<ApiResponse<Void>> entity = advice.handleRebootException(ex);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(entity.getBody()).isNotNull();
        assertThat(entity.getBody().isSuccess()).isFalse();
        assertThat(entity.getBody().getErrorCode()).isEqualTo("UAM-001");
        assertThat(entity.getBody().getMessage()).isEqualTo("User not found");
    }
}
