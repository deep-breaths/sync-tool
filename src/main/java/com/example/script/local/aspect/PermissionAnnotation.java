package com.example.script.local.aspect;

import java.lang.annotation.*;

/**
 * @author albert lewis
 * @date 2024/3/13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionAnnotation{
}