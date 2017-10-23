package com.fortegrp.at.common.extension
import com.fortegrp.at.common.annotation.Overrider
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

class OverriderExtension extends AbstractAnnotationDrivenExtension<Overrider> {
    @Override
    void visitFeatureAnnotation(Overrider annotation, FeatureInfo feature) {
        def initializerMethod = feature.parent.initializerMethod ?: feature.parent.superSpec.initializerMethod ?: feature.parent.superSpec.superSpec.initializerMethod
        initializerMethod.addInterceptor(new ParameterInterceptor(paramNames: annotation.params(), paramValues: annotation.values()))

    }

    @Override
    void visitSpecAnnotation(Overrider annotation, SpecInfo spec) {
        def initializerMethod = spec.initializerMethod ?: spec.superSpec.initializerMethod ?: spec.superSpec.superSpec.initializerMethod
        initializerMethod.addInterceptor(new ParameterInterceptor(paramNames: annotation.params(), paramValues: annotation.values()))
    }
}