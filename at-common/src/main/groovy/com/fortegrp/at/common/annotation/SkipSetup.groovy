package com.fortegrp.at.common.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])

/**
 * For skipping setup method (common for whole spec)
 */

@interface SkipSetup {}