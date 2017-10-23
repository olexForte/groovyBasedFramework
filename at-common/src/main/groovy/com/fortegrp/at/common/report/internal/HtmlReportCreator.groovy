package com.fortegrp.at.common.report.internal

import com.fortegrp.at.common.env.Environment
import com.fortegrp.at.common.extension.GlobalSpecExtension
import com.fortegrp.at.common.report.IReportCreator
import groovy.time.TimeCategory
import groovy.xml.MarkupBuilder
import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.IterationInfo

import static org.spockframework.runtime.model.BlockKind.*

class HtmlReportCreator extends AbstractHtmlCreator<SpecData> implements IReportCreator {
    def reportAggregator = HtmlReportAggregator.instance
    def stringFormatter = new DateStringFormatHelper()
    def problemWriter = new ProblemBlockWriter(stringFormatter: stringFormatter)

    final block2String = [
            (SETUP)   : 'Given:',
            (CLEANUP) : 'Cleanup:',
            (THEN)    : 'Then:',
            (EXPECT)  : 'Expect:',
            (WHEN)    : 'When:',
            (WHERE)   : 'Where:',
            'AND'     : 'And:',
            'EXAMPLES': 'Examples:'
    ]

    void setFeatureReportCss(String css) {
        super.setCss(css)
    }

    void setSummaryReportCss(String css) {
        reportAggregator?.css = css
    }

    void createReportFor(SpecData data) {
        def specClassName = data.info.description.className + Environment.reportGlobalSuffix()
        def reportsDir = createReportsDir()
        if (reportsDir.exists()) {
            if (data.featureRuns.size() > 0) {
                try {
                    new File(reportsDir, specClassName + '.html')
                            .write(reportFor(data))
                } catch (e) {
                    throw new RuntimeException("Failed to create HTML reports:", e)
                }
            }
        } else {
            println "${this.class.name} cannot create output directory: ${reportsDir.absolutePath}"
        }
    }

    @Override
    protected String reportHeader(SpecData data) {
        "Report for ${data.info.description.className + Environment.reportGlobalSuffix()}"
    }

    void writeSummary(MarkupBuilder builder, SpecData data, Map appendStat = null) {
        builder.div('class': 'summary-reports')
                {
            builder.a('href': 'index.html','<< Summary')
            h3 'Summary:'
            builder.div('class': 'date-test-ran', whenAndWho)
            if (executionStartedDate == null) {
                String reportCreationDateString = (whenAndWho =~ /.*on (.*) by.*/)[0][1]
                def reportCreationDate = Date.parse("MMM dd H:m:s z yyyy", reportCreationDateString)
                def runStartTime = new Date()
                use(TimeCategory) {
                    runStartTime = reportCreationDate - Math.round(stats(data).time / 1000).toInteger().seconds
                }
                executionStartedDate = runStartTime
            }
            builder.div('class': 'execution-start-time', executionStartedLabel + DateStringFormatHelper.toDateString(executionStartedDate))

            table('class': 'summary-table') {
                thead {
                    th 'Executed features'
                    th 'Failures'
                    th 'Ignored'
                    th 'Success rate'
                    th 'Time'
                }
                tbody {
                    tr {
                        def stats = stats(data)
                        td('class': 'totalStat', stats.totalRuns)
                        td('class': 'failedStat', stats.failures)
                        td('class': 'ignoredStat', stats.ignoreFailures)
                        td stringFormatter.toPercentage(stats.successRate)
                        td('class': 'timeStat', stringFormatter.toTimeDuration(stats.time))
                        reportAggregator?.aggregateReport(data.info.description.className + Environment.reportGlobalSuffix(), stats, outputDir)
                    }
                }
            }
        }
    }

    protected Map stats(SpecData data) {
        def failures = calculateFeatureFailuresCountWithinDatadrivenTest(data)
        def executed = calculateFeatureRunCountWithinDatadrivenText(data)
        def ignored = calculateIgnoredFeatureRunCountWithinDatadrivenText(data)
        def successRate = successRate(executed + ignored, failures + ignored)
        [failures   : failures, ignoreFailures: ignored, totalRuns: executed + ignored,
         successRate: successRate, time: data.totalTime, startTime:data.startTime]
    }

    def calculateFeatureRunCountWithinDatadrivenText(SpecData data) {
        def result = 0
        data.featureRuns.each { run ->
            result += run.failuresByIteration.size()
        }
        result
    }

    def calculateIgnoredFeatureRunCountWithinDatadrivenText(SpecData data) {
        def result = 0
        data.info.features.findAll { it.name.contains("[IGNORED:") }.each { feature ->
            result++
        }
        result
    }

    def calculateFeatureFailuresCountWithinDatadrivenTest(SpecData data) {
        def result = 0
        data.featureRuns.each { run ->
            def failedIterationCount = 0
            run.failuresByIteration.values().each { failedIterationCount += it.size() }
            result += failedIterationCount
        }
        result
    }

    protected void writeDetails(MarkupBuilder builder, SpecData data, String suffix, String appendResult) {
        builder.h3 "Features:"
        builder.mkp.yieldUnescaped(this.class.getResource('/report_creator_filter.html').text)
        builder.table('class': 'features-table') {
            colgroup {
                col('class': 'block-kind-col')
                col('class': 'block-text-col')
            }
            writeFeature(builder, data)
        }
    }

    private void writeFeature(MarkupBuilder builder, SpecData data) {
        data.info.allFeatures.each { FeatureInfo feature ->
            def run = data.featureRuns.find { run -> run.feature == feature }
            if (run || (feature.getName().contains("IGNORED"))) {
                builder.tbody(['id': feature.getName(), 'class': 'test-result collapsed-result shown-result']) {
                    writeFeatureDescription(builder, feature, run)
                    for (int i = 0; i < feature.blocks.size(); i++) {
                        writeBlock(builder, feature.blocks.get(i), run, i)
                    }
                    if (run) {
                        writeRun(builder, run)
                        writeProblemBlock(builder, run)
                    }
                }
            }
        }
    }

    private void writeBlock(MarkupBuilder builder, BlockInfo block, FeatureRun run, int ind) {
        def prefix = (ind + 1).toString() + "."
        def failedBlock = (run) ? run.failuresByIteration.values().toArray()[0].find { it.stepNumber == ind } : ""
        if (!isEmptyOrContainsOnlyEmptyStrings(block.texts))
            block.texts.eachWithIndex({ blockText, index ->
                writeBlockRow(builder, (failedBlock ? 'ex-fail' : ''), (index == 0 ? block.kind : 'AND'), substNamedParams(blockText, run), prefix)
            })
        else
            writeBlockRow(builder, (failedBlock ? 'ex-fail' : ''), block.kind, '----', prefix)
    }

    private substNamedParams(String source, FeatureRun run, doQuote = true) {
        if (run) {
            def quotationStart = doQuote ? "<strong>" : ""
            def quotationEnd = doQuote ? "</strong>" : ""
            def paramNames = run.feature.parameterNames.toArray()
            def paramValues = run.failuresByIteration.keySet()*.dataValues.toList()
            def indexOfRuntime = paramNames.findIndexOf { it == "runtime" }
            def paramNamesAgr = (indexOfRuntime == -1) ? paramNames : (paramNames[0..indexOfRuntime].asList() +
                    ((paramValues[0][indexOfRuntime] instanceof HashMap) ? paramValues[0][indexOfRuntime].keySet() : []))
            def paramValuesArgs = []
            paramValues.each {
                paramValuesArgs.add((indexOfRuntime == -1) ? it : it[0..indexOfRuntime].asList() + ((paramValues[0][indexOfRuntime] instanceof HashMap) ? it[indexOfRuntime].values() : []))
            }
            def result = source
            for (def i = 0; i < paramNamesAgr.size(); i++) {
                def regex = /.*#${paramNamesAgr[i]}\.([a-zA-Z\.]*).*/
                if ((source =~ regex).matches()) {
                    def arguments = (source =~ regex)[0][1]
                    def subst = "undefined"
                    if (paramValuesArgs*.toList()*.get(i).unique().size() == 1) {
                        try {
                            subst = quotationStart + paramValuesArgs[0][i]."${arguments}" + quotationEnd
                        } catch (Exception e) {
                        }
                    } else {
                        subst = quotationStart + "#" + paramNamesAgr[i] + "." + arguments + quotationEnd
                    }
                    result = result.replace("#" + paramNamesAgr[i] + "." + arguments, subst)
                } else {
                    if (paramValuesArgs*.toList()*.get(i).unique().size() == 1) {
                        result = result.replace("#" + paramNamesAgr[i], quotationStart + paramValuesArgs[0][i].toString() + quotationEnd)
                    } else {
                        result = result.replace("#" + paramNamesAgr[i], quotationStart + "#" + paramNamesAgr[i] + quotationEnd)
                    }
                }
            }
            result
        } else {
            return source
        }
    }

    private expandRuntimeParamNames(List paramNames) {
        paramNames
    }

    private expandRuntimeParamValues(List paramValues) {
    }

    private writeBlockRow(MarkupBuilder builder, cssClass, blockKind, text, prefix) {
        builder.tr(cssClass) {
            writeBlockKindTd(builder, blockKind, prefix, cssClass)
            td {
                div('class': 'block-text') {
                    builder.mkp.yieldUnescaped(text)
                }
            }
        }
    }

    protected boolean isEmptyOrContainsOnlyEmptyStrings(List<String> strings) {
        !strings || strings.every { it.trim() == '' }
    }

    private void writeBlockKindTd(MarkupBuilder builder, blockKindKey, prefix = "", addClass = "") {
        builder.td {
            div('class': 'block-kind ' + addClass, prefix + " " + block2String[blockKindKey])
        }
    }
/**
 * Method that writes iteration results in data-driven tests
 *
 * @param builder
 * @param run
 */
    private void writeRun(MarkupBuilder builder, FeatureRun run) {
        if (!run || !run.feature.parameterized) return
        builder.tr {
            def paramNames = run.feature.parameterNames.toArray()
            def paramValues = run.failuresByIteration.keySet()*.dataValues.toList()
            def paramValuesArgs = []
            writeBlockKindTd(builder, 'EXAMPLES')
            td {
                div('class': 'spec-examples') {
                    table('class': 'ex-table') {
                        thead {
                            //Expand runtime variables
                            def indexOfRuntime = paramNames.findIndexOf { it == "runtime" }
                            def paramNamesAgr = (indexOfRuntime == -1) ? paramNames : (paramNames[0..indexOfRuntime - 1].asList() +
                                    ((paramValues[0][indexOfRuntime] instanceof HashMap) ? paramValues[0][indexOfRuntime].keySet() : []))
                            paramValues.each {
                                paramValuesArgs.add((indexOfRuntime == -1) ? it : (it[0..indexOfRuntime - 1].asList() +
                                        ((paramValues[0][indexOfRuntime] instanceof HashMap) ? it[indexOfRuntime].values() : [])))
                            }
                            paramNamesAgr.each { param ->
                                th('class': 'ex-header', param)
                            }
                        }
                        tbody {
                            run.failuresByIteration.each { iteration, errors ->
                                writeIteration(builder, iteration, paramValuesArgs[run.failuresByIteration.keySet().toList().indexOf(iteration)], errors, run.failuresByIteration.keySet().toList().indexOf(iteration))
                            }
                        }
                    }
                }
            }
            td {
                div('class': 'spec-status', iterationsResult(run))
            }
        }

    }

    private String iterationsResult(FeatureRun run) {
        def totalRuns = run.failuresByIteration.size()
        def totalErrors = run.failuresByIteration.values().count { !it.empty }
        "${totalRuns - totalErrors}/${totalRuns} passed"
    }

    private void writeIteration(MarkupBuilder builder, IterationInfo iteration, paramVals,
                                List<ErrorInfo> errors, index) {
        builder.tr('class': (errors ? 'ex-fail' : 'ex-pass')) {
            for (def i = 0; i < paramVals.size(); i++) {
                td('class': 'ex-value', paramVals[i])
            }
            td('class': 'ex-result', iterationResult(errors))
        }
    }

    private String iterationResult(List<ErrorInfo> errors) {
        errors ? 'FAIL' : 'OK'
    }

    private void writeFeatureDescription(MarkupBuilder builder, FeatureInfo feature, FeatureRun run) {
        def additionalCssClass = (!run) ? " ignored" : (run.failuresByIteration.any {
            !it.value.isEmpty()
        } ? ' failure' : ' ')
        builder.tr {
            td(colspan: '10') {
                div(['class': 'feature-description ' + additionalCssClass, 'onclick': "expandCollapse(event);"])
                    {
                        mkp.yieldUnescaped(substNamedParams(feature.name, run, false))
                    }
                div("Execution time:" + ((run != null) ? DateStringFormatHelper.toTimeDuration(run.time) : "N/A"))
            }
        }
    }

    private void writeProblemBlock(MarkupBuilder builder, FeatureRun run) {
        if (run.failuresByIteration.values().any { !it.isEmpty() })
            builder.tr {

                td(colspan: '10') {
                    div('class': 'problem-description') {
                        div('class': 'problem-header', 'The following problems occurred:')
                        div('class': 'problem-list') {
                            problemWriter.writeProblems(builder, run)
                        }
                    }
                }
            }
    }


    static def getScreenshots(String absoluteSpecName, featureNameRegexp) {
        def result = []
        String featureScreenshotDir = absoluteSpecName.replace('.', '/')
        String specGebReportDir = GlobalSpecExtension.outputDir + '/' + featureScreenshotDir
        new FileNameByRegexFinder().getFileNames(specGebReportDir, /${featureNameRegexp.replace("-", "\\-")}.*png/).each {
            result.add(featureScreenshotDir + '/' + new File(it).getName())
        }
        result.size() > 0 ? result[0] : null
    }
}
