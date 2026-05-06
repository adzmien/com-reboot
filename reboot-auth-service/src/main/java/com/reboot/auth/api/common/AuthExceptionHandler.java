package com.reboot.auth.api.common;

import com.reboot.uam.lib.web.BaseRestControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Auth-service exception handler.
 * Inherits {@link com.reboot.uam.lib.exception.RebootException} mapping from
 * {@link BaseRestControllerAdvice}; add auth-specific handlers here as needed.
 */
@RestControllerAdvice
public class AuthExceptionHandler extends BaseRestControllerAdvice {
}
