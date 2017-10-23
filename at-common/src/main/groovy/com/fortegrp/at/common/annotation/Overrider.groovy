package com.fortegrp.at.common.annotation

import com.fortegrp.at.common.extension.OverriderExtension
import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Annotation for Overriding variables
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD,ElementType.TYPE])
@ExtensionAnnotation(OverriderExtension)

//@Overrider(params = ["param1","param12"], values = { ["value1","value2"] })

@interface Overrider {
    String[] params()

    Class<? extends Closure> values()
}
