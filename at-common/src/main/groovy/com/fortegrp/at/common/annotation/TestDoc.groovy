package com.fortegrp.at.common.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])

/**
 * Created by yhraichonak
 * Annotation for auto test back-references (to TestTracking systems like Jira/Bugzilla ...)
 */
@interface TestDoc {
    String value()
}