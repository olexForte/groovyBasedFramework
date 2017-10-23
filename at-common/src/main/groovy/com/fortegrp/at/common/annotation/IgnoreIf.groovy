package com.fortegrp.at.common.annotation

import com.fortegrp.at.common.extension.IgnoreIfExtension
import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by lfedorov on 1/14/2015.
 /**
 * Ignores the annotated spec/feature if the given condition holds.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(IgnoreIfExtension.class)
@interface IgnoreIf {
    Class<? extends Closure> value()
}

