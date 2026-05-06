package com.reboot.uam.api.common;

import com.reboot.uam.lib.web.BaseRestControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * UAM-service exception handler.
 * Inherits {@link com.reboot.uam.lib.exception.RebootException} mapping from
 * {@link BaseRestControllerAdvice}; add UAM-specific handlers here as needed.
 */
@RestControllerAdvice
public class UamExceptionHandler extends BaseRestControllerAdvice {
}
