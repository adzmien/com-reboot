package com.reboot.uam.lib.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or type as KYC-sensitive data (e.g. national ID, date of birth).
 * Used by serialisation interceptors and audit handlers to mask or omit the value
 * when producing logs, audit events, or public API responses.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface KycSensitive {
}
