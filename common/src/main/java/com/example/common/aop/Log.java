package com.example.common.aop;

import com.example.common.domain.enums.BusinessStatus;
import com.example.common.domain.enums.BusinessType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
    public String title() default "";
    public BusinessType businessType() default BusinessType.OTHER;

}
