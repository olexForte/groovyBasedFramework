package com.fortegrp.at.common.annotation

import com.fortegrp.at.common.extension.RetrySpecExtension
import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(RetrySpecExtension.class)
@interface RetryOnFailure {

    int times() default 1

}
