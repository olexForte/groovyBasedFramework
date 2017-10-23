package com.fortegrp.at.common.extension

import com.fortegrp.at.common.annotation.RetryOnFailure
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

class RetrySpecExtension extends AbstractAnnotationDrivenExtension<RetryOnFailure> {

    void visitFeatureAnnotation(RetryOnFailure retries, FeatureInfo feature) {
        clearInterceptors(feature)
        feature.getFeatureMethod().interceptors.add(new RetryInterceptor(retries.times()))
    }

    void visitSpecAnnotation(RetryOnFailure retries, SpecInfo spec) {

        SpecInfo specToAdd = spec
        spec.subSpec
        List<SpecInfo> selfAndSuperSpecs = [spec]
        List<SpecInfo> selfAndSubSpecs = [spec]
        while (specToAdd.getSuperSpec()) {
            selfAndSuperSpecs << specToAdd.getSuperSpec()
            specToAdd = specToAdd.getSuperSpec()
        }
        specToAdd = spec
        while (specToAdd.subSpec) {
            selfAndSubSpecs << specToAdd.subSpec
            specToAdd = specToAdd.subSpec
        }

        if (selfAndSuperSpecs.any { it.getReflection().isAnnotationPresent(RetryOnFailure.class)}) {
            List<FeatureInfo> featuresToRetry = [selfAndSubSpecs.features].flatten().unique()
            for (FeatureInfo feature : featuresToRetry) {
                clearInterceptors(feature)
                feature.getFeatureMethod().addInterceptor(new RetryInterceptor(retries.times()))
            }
        }
    }

    private void clearInterceptors(FeatureInfo featureInfo) {
        featureInfo.featureMethod.interceptors.removeAll { it.class == RetryInterceptor }
    }
}
