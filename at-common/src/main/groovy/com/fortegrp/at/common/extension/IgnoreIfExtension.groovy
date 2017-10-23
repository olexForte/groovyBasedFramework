package com.fortegrp.at.common.extension

import com.fortegrp.at.common.annotation.IgnoreIf
import org.spockframework.runtime.GroovyRuntimeUtil
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.ExtensionException
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.ISkippable
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.util.InternalSpockError

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by lfedorov on 1/14/2015.
 */
class IgnoreIfExtension extends AbstractAnnotationDrivenExtension<IgnoreIf> {
    private static final Pattern JAVA_VERSION = Pattern.compile("(\\d+\\.\\d+).*")

    private static final Object DELEGATE = new Object() {

        Properties getProperties() {
            return System.getProperties()
        }

        BigDecimal getJavaVersion() {
            String versionString = System.getProperty("java.version")
            Matcher matcher = JAVA_VERSION.matcher(versionString)
            if (matcher.matches()) return new BigDecimal(matcher.group(1))
            throw new InternalSpockError(versionString)
        }
    }

    @Override
    void visitSpecAnnotation(IgnoreIf annotation, SpecInfo spec) {
        doVisit(annotation, spec)
    }

    @Override
    void visitFeatureAnnotation(IgnoreIf annotation, FeatureInfo feature) {
        doVisit(annotation, feature)
    }

    private void doVisit(IgnoreIf annotation, ISkippable skippable) {
        Closure condition = createCondition(annotation.value())
        Object result = evaluateCondition(condition)
        if (GroovyRuntimeUtil.isTruthy(result)) skippable.setSkipped(true)
    }

    private Closure createCondition(Class<? extends Closure> clazz) {
        try {
            return clazz.getConstructor(Object.class, Object.class).newInstance(null, null)
        } catch (Exception e) {
            throw new ExtensionException("Failed to instantiate @IgnoreIf condition", e)
        }
    }

    private Object evaluateCondition(Closure condition) {
        condition.setDelegate(DELEGATE)
        condition.setResolveStrategy(Closure.DELEGATE_ONLY)

        try {
            return condition.call()
        } catch (Exception e) {
            throw new ExtensionException("Failed to evaluate @IgnoreIf condition", e)
        }
    }
}