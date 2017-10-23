package com.fortegrp.at.common.extension

import com.fortegrp.at.common.env.Environment
import com.fortegrp.at.common.report.IReportCreator
import com.fortegrp.at.common.report.internal.ExtendedErrorInfo
import com.fortegrp.at.common.report.internal.FeatureRun
import com.fortegrp.at.common.report.internal.SpecData
import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.junit.runner.Description
import org.spockframework.runtime.IRunListener
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo

@Slf4j
class SpecInfoListener implements IRunListener {

    final IReportCreator reportCreator
    SpecData specData
    IterationInfo currentIteration
    long startT
    def featureExecutionTime
    FeatureRun featureRun

    SpecInfoListener(IReportCreator reportCreator) {
        this.reportCreator = reportCreator
    }

    void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description)
    }

    @Override
    synchronized void beforeSpec(SpecInfo spec) {
        log.info("[Fork #${Environment.forkNumber}] Specification execution (${spec.name})")
        specData = new SpecData(info: spec)
        startT = System.currentTimeMillis()
        specData.startTime=startT
    }

    @Override
    void beforeFeature(FeatureInfo feature) {
        log.info("[Fork #${Environment.forkNumber}] Feature execution (${feature.name})")
        feature.blocks.each {
            log.info("[Fork #${Environment.forkNumber}] " + it.getKind().toString() + ((it.getTexts().size() > 0) ? " " + it.texts : ""))
        }
        featureRun = new FeatureRun(feature: feature)
        specData.featureRuns << featureRun
        featureExecutionTime = new Date().time.longValue()
    }

    @Override
    void beforeIteration(IterationInfo iteration) {
        currentRun().failuresByIteration[iteration] = []
        currentIteration = iteration
    }

    @Override
    void afterIteration(IterationInfo iteration) {
        currentIteration = null
    }

    @Override
    void afterFeature(FeatureInfo feature) {
        featureExecutionTime = new Date().time.longValue() - featureExecutionTime
        specData.featureRuns.set(specData.featureRuns.indexOf(featureRun), featureRun.withExecutionTime(featureExecutionTime))
        featureExecutionTime = 0
        log.info "[Fork #${Environment.forkNumber}] Feature is finished (${feature.name})"
    }

    @Override
    void afterSpec(SpecInfo spec) {
        log.info "[Fork #${Environment.forkNumber}] Specification is finished (${spec.name})"
        assert specData.info == spec
        specData.totalTime = System.currentTimeMillis() - startT
        reportCreator.createReportFor specData
        specData = null
    }

    @Override
    void error(ErrorInfo error) {
        log.error("[Fork #${Environment.forkNumber}] ", error.exception)
        def failedStepIndex = detectFailedStep(error, currentRun())
        currentRun().failuresByIteration[currentIteration] << new ExtendedErrorInfo(error, failedStepIndex)
    }

    static File[] findAllTestFiles(scriptName) {
        def result = []
        new File("src/test/groovy").eachFileRecurse(FileType.FILES) {
            if (it.name =~ /${scriptName}\.groovy$/) {
                result << it
            }
        }
        result
    }

    static def detectFailedStep(ErrorInfo e, FeatureRun fr) {
        def blockLabels = fr.feature.blocks*.texts
        def blockSearchResult = [:]
        String methodSourceCode = ""
        try {

            methodSourceCode = findAllTestFiles(e.method.feature.parent.name)[0].text
        }
        catch (Exception ex) {
            def g = 2
        }
        methodSourceCode = methodSourceCode.replace('"', '').replaceAll("(given:|when:|then:|expect:|where:|cleanup:|setup:|and:)", "")
        try {
            methodSourceCode = methodSourceCode.toString().split("\r\n")[(e.method.line.toInteger())..(e.exception.stackTrace.last().lineNumber)]*.trim().join("")
        } catch (IndexOutOfBoundsException ex) {
        }
        for (int i = 0; i < blockLabels.size() - 1; i++) {
            def blockName = blockLabels[i]*.trim().join("")
            blockSearchResult.put(methodSourceCode.indexOf(blockName), fr.feature.blocks.get(i))
        }
        fr.feature.blocks.indexOf(blockSearchResult.get(blockSearchResult.keySet().toArray().max()))
    }

    @Override
    void specSkipped(SpecInfo spec) {
        log.info "[Fork #${Environment.forkNumber}] Specification is skipped (${spec.name})"
    }

    @Override
    void featureSkipped(FeatureInfo feature) {
        log.info "[Fork #${Environment.forkNumber}] Feature is skipped (${feature.name})"
    }

    private FeatureRun currentRun() {
        specData.featureRuns.last()
    }
}
