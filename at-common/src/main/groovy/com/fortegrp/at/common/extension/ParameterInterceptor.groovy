package com.fortegrp.at.common.extension

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation

/**
 * Created by yhraychonak on 12/15/2014.
 */

class ParameterInterceptor implements IMethodInterceptor {
    def paramNames
    def paramValues

    void intercept(IMethodInvocation invocation) throws Throwable {
        invocation.proceed()
        if (paramNames.size() > 0) {
            def values = paramValues.getConstructor(Object.class, Object.class).newInstance(null, null).call()
            for (int i in 0..(paramNames.size() - 1)) {
                invocation.target."${paramNames[i]}" = values[i]
            }
        }
    }

}