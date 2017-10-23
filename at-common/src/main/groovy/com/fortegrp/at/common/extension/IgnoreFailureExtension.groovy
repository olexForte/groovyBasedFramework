package com.fortegrp.at.common.extension

import com.fortegrp.at.common.annotation.IgnoreFailure
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by yhraichonak
 **/
class IgnoreFailureExtension extends AbstractAnnotationDrivenExtension<IgnoreFailure>{

    void visitFeatureAnnotation(IgnoreFailure annotation, FeatureInfo feature) {
        setFeatureIgnored(feature, annotation.value())
    }

    void visitSpecAnnotation(IgnoreFailure annotation, SpecInfo spec) {
        spec.features.each {
            setFeatureIgnored(it, annotation.value())
        }
    }

    def setFeatureIgnored(FeatureInfo feature, String annotationValue){
        if (!feature.isSkipped() && !feature.isExcluded()) {
            feature.setName(feature.getName() + " [IGNORED: '" + annotationValue + "']")
            feature.setSkipped(true)
        }
    }
}

