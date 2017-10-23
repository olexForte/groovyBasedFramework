package com.fortegrp.at.common.report.internal

import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.runtime.StackTraceUtils

@Singleton(lazy = true)
@Slf4j
class HtmlReportAggregator extends AbstractHtmlCreator<Map> {

    Map<String, Map> aggregatedData = [:]

    def stringFormatter = new DateStringFormatHelper()

    void aggregateReport(String specName, Map stats, String outputDir) {
        this.outputDir = outputDir
        aggregatedData[specName] = stats
        def reportsDir = createReportsDir()
        def existingResult = null

        def lockedReportFile = new File(reportsDir.path + "/index.html.lck")
        def reportFile = new File(reportsDir.path + "/index.html")
        for (int i in 0..2) {
            if (lockedReportFile.exists()) {
                log.warn("Report generator is busy. Wait for 1 second...")
                sleep(1000)
            } else {
                break
            }
        }
        if (reportFile.exists()) {
            reportFile.renameTo(lockedReportFile)
            existingResult = appendReports(lockedReportFile)
        }
        try {
            def resultSuffix = Calendar.instance.timeInMillis.toString()
            def buAggregatedData = aggregatedData.clone()
            aggregatedData.clear()
            aggregatedData.put(buAggregatedData.keySet().last(), buAggregatedData.get(buAggregatedData.keySet().last()))
            lockedReportFile.write(reportFor(stats, resultSuffix, existingResult))
            aggregatedData = buAggregatedData
            sortAndGroupReport(lockedReportFile)
            lockedReportFile.renameTo(reportFile)
        } catch (e) {
            StackTraceUtils.deepSanitize(e).printStackTrace()
            println "${this.class.name} failed to create aggregated reports, Reason: $e"
        }
    }

    def sortAndGroupReport(reportFile) {
        def xmlStr = reportFile.text.replaceAll(/(?s)<script.*script>/, "")
        def script = ""
        if (reportFile.text.indexOf("<script") > -1) {
            script = reportFile.text.substring(reportFile.text.indexOf("<script"), reportFile.text.indexOf("/script>") + 8)
        }
        def xml = new XmlSlurper().parseText(xmlStr)
        def oldXml = new XmlSlurper().parseText(xmlStr)
        def tableContainer = xml.depthFirst().findAll { it.@class.text().contains('sortable') }[0]
        def oldTableContainer = oldXml.depthFirst().findAll { it.@class.text().contains('sortable') }[0]
        def sortedRows = []
        for (int i = 0; i < oldTableContainer.tbody.children().size(); i++) {
            def div = oldTableContainer.tbody.children()[i]
            for (int j = 0; j < div.children().size(); j++) {
                sortedRows.add(div.children()[j])
            }
        }
        sortedRows.sort(true) { it.td[0].@id.toString() }
        for (int i = 1; i < tableContainer.tbody[0].children().size(); i++) {
            tableContainer.tbody[0].div[i].replaceNode {}
        }
        for (int i = 0; i < tableContainer.tbody[0].div[0].children().size(); i++) {
            tableContainer.tbody[0].div[0].tr[i].replaceNode {}
        }
        sortedRows.eachWithIndex { def entry, int i ->
            if (!sortedRows[i].td[0].@style.text().contains("opacity:0;")) {
                sortedRows[i].td[0].@style = sortedRows[i].td[0].@style.text() + (((i > 0) && sortedRows[i - 1].td[0].@id.toString() == sortedRows[i].td[0].@id.toString()) ? " opacity:0;" : "")
            }
            if (!sortedRows[i].td[0].@class.text().contains("artificial_hide")) {
                sortedRows[i].td[0].@class = sortedRows[i].td[0].@class.text() + (((i > 0) && sortedRows[i - 1].td[0].@id.toString() == sortedRows[i].td[0].@id.toString()) ? " artificial_hide" : "")
            }
            if (!sortedRows[i].td[1].@style.text().contains("opacity:0;")) {
                sortedRows[i].td[1].@style = sortedRows[i].td[1].@style.text() + (((i > 0) && sortedRows[i - 1].td[1].@id.toString() == sortedRows[i].td[1].@id.toString()) ? " opacity:0;" : "")
            }
            if (!sortedRows[i].td[1].@class.text().contains("artificial_hide")) {
                sortedRows[i].td[1].@class = sortedRows[i].td[1].@class.text() + (((i > 0) && sortedRows[i - 1].td[1].@id.toString() == sortedRows[i].td[1].@id.toString()) ? " artificial_hide" : "")
            }
        }
        sortedRows.each {
            tableContainer.tbody[0].div[0].appendNode(it)
        }

        reportFile.text = groovy.xml.XmlUtil.serialize(xml) + script
    }

    def appendReports(reportFile) {
        def result = ""
        def failed = 0
        def total = 0
        def ignored = 0
        def time = 0
        def xmlStr = reportFile.text.replaceAll(/(?s)<script.*script>/, "")
        def xml = new XmlSlurper().parseText(xmlStr)
        def executionStartedLabelStr = xml.depthFirst().findAll {
            (it.@class).toString().contains('execution-start-time')
        }[0].toString()

        def dateFromReport = Date.parse("MMM dd H:m:s z yyyy", (executionStartedLabelStr =~ /.*on (.*)/)[0][1])
        if (((executionStartedDate != null) && (dateFromReport.compareTo(executionStartedDate) < 0)) || (executionStartedDate == null)) {
            executionStartedDate = dateFromReport
        }
        def results = xml.depthFirst().findAll { (it.@id).toString().contains('results') }
        total = xml.depthFirst().findAll { it.@class.text().contains('sumTotal') }.get(0).text().toInteger()
        failed = xml.depthFirst().findAll { it.@class.text().contains('sumFailures') }.get(0).text().toInteger()
        ignored = xml.depthFirst().findAll {
            it.@class.text().contains('sumIgnoredFailures')
        }.get(0).text().toInteger()
        time = xml.depthFirst().findAll { it.@class.text().contains('sumExecTime') }.get(0).@id.toInteger()
        results.each {
            def tmpStr = groovy.xml.XmlUtil.serialize(it)
            tmpStr = tmpStr.substring(tmpStr.indexOf("><") + 1)
            result = result + tmpStr
        }
        [result, ["total": total, "failed": failed, "ignored": ignored, "time": time]]
    }

    @Override
    protected String reportHeader(Map data) {
        'Specification run results'
    }

    @Override
    protected void writeSummary(MarkupBuilder builder, Map stats, Map appendedStats) {
        def filterPanel = this.class.getResource('/report_aggregator_filter.html').text
        def aggregateData = recomputeAggregateData()

        aggregateData.fTotal = aggregateData.fTotal + ((appendedStats != null) ? appendedStats.total : 0)
        aggregateData.fFails = aggregateData.fFails + ((appendedStats != null) ? appendedStats.failed : 0)
        aggregateData.fIgnoredFails = aggregateData.fIgnoredFails + ((appendedStats != null) ? appendedStats.ignored : 0)
        aggregateData.time = aggregateData.time + ((appendedStats != null) ? appendedStats.time : 0)
        def cssClassIfTrue = { isTrue, String cssClass, String uniqueClass ->
            if (isTrue) ['class': cssClass + " " + uniqueClass] else ['class': uniqueClass]
        }
        builder.div('class': 'summary-reports') {
            //Temporary comment out due to useless
            //mkp.yieldUnescaped(filterPanel)
            h3 'Features summary:'
            String reportCreationDateString = DateStringFormatHelper.toDateString(new Date())
            def whenAndWho = "Report created on ${reportCreationDateString}" + " by ${System.getProperty('user.name')}"
            builder.div(['style': ReportStylesheet.styleMap.get("div.date-test-ran"), 'class': 'date-test-ran'], whenAndWho)
            builder.div(['style': ReportStylesheet.styleMap.get("execution-start-time"), 'class': 'execution-start-time'], executionStartedLabel + DateStringFormatHelper.toDateString(executionStartedDate))
            table('style': ReportStylesheet.styleMap.get('table.summary-table')) {
                thead {
                    th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Total features')
                    th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Feature failures')
                    th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Ignored features')
                    th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Success rate')
                    th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Total time')
                }
                tbody {
                    tr {
                        td(['style': ReportStylesheet.styleMap.get('table.summary-table td'), 'class': 'sumTotal'], aggregateData.fTotal)
                        td(['style': aggregateData.fFails ?
                                ReportStylesheet.styleMap.get('tr.failure td, td.failure') + ReportStylesheet.styleMap.get('table.summary-table td') :
                                ReportStylesheet.styleMap.get('table.summary-table td')]
                                + cssClassIfTrue(aggregateData.fFails, 'failure', 'sumFailures'), aggregateData.fFails)
                        td(['style': aggregateData.fIgnoredFails ?
                                ReportStylesheet.styleMap.get('.ignored') + ReportStylesheet.styleMap.get('table.summary-table td') :
                                ReportStylesheet.styleMap.get('table.summary-table td')]
                                + cssClassIfTrue(aggregateData.fIgnoredFails, 'ignored', 'sumIgnoredFailures'), aggregateData.fIgnoredFails)
                        td(['style': aggregateData.failed ?
                                ReportStylesheet.styleMap.get('tr.failure td, td.failure') + ReportStylesheet.styleMap.get('table.summary-table td') :
                                ReportStylesheet.styleMap.get('table.summary-table td')]
                                + cssClassIfTrue(aggregateData.failed, 'failure', 'successRate'), stringFormatter.toPercentage(successRate(aggregateData.fTotal, aggregateData.fIgnoredFails + aggregateData.fFails)))
                        def reportCreationDate = Date.parse("MMM dd H:m:s z yyyy", (whenAndWho =~ /.*on (.*) by.*/)[0][1])
                        td(['style': ReportStylesheet.styleMap.get('table.summary-table td'), 'class': 'sumExecTime', 'id': (reportCreationDate.getTime() - executionStartedDate.getTime())], stringFormatter.toTimeDuration(reportCreationDate.getTime() - executionStartedDate.getTime()))
                    }
                }
            }
        }
    }

    Map recomputeAggregateData() {
        def result = [total: 0, passed: 0, failed: 0, fTotal: 0, fFails: 0, fIgnoredFails: 0, time: 0.0]
        aggregatedData.values().each { Map stats ->
            result.total += 1
            result.passed += (stats.failures ? 0 : 1)
            result.failed += (stats.failures ? 1 : 0)
            result.fFails += stats.failures
            result.fIgnoredFails += stats.ignoreFailures
            result.fTotal += stats.totalRuns
            result.time += stats.time
        }
        result
    }

    @Override
    protected void writeDetails(MarkupBuilder builder, Map ignored, String resultSuffix, String appendResult) {
        builder.h3 'Specifications:'
        builder.input(['type':'checkbox', 'id':'failures_only_summary','onclick':"filter_summary_failures_only(false)" ], 'Failures Only')
        builder.table(['style': ReportStylesheet.styleMap.get('table.summary-table'), 'class': 'sortable']) {
            thead {
                th(['style': ReportStylesheet.styleMap.get('table.summary-table th'), "id": "package"], 'Package')
                th(['style': ReportStylesheet.styleMap.get('table.summary-table th'), "id": "colName"], 'Name')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Browser')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Report')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Features')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Failed')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Ignored')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Success rate')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Execution Time')
                th('style': ReportStylesheet.styleMap.get('table.summary-table th'), 'Start time')
            }
            tbody {
                div('id': 'results_' + resultSuffix) {
                    aggregatedData.keySet().sort().each { String specName ->
                        def stats = aggregatedData[specName]
                        def cssClasses = []
                        if (stats.failures) cssClasses << 'failure'
                        def testClearName = specName.substring(specName.lastIndexOf(".") + 1, specName.lastIndexOf("Spec_") + 4)
                        def testPackageName = specName.substring(0, specName.lastIndexOf(".")).replace("com.fortegrp.at.specs", "c.f.a.s")
                        def testSuffixes = specName.substring(specName.lastIndexOf("Spec_") + 5).split("_")
                        def (format, browser) = [null, testSuffixes[0]]
                        if (testSuffixes.size() > 1) {
                            (format, browser) = [testSuffixes[0], testSuffixes[1]]
                        }
                        tr(['style': stats.failures ? ReportStylesheet.styleMap.get('tr.failure td, td.failure') : "",
                            'class': (cssClasses ? cssClasses.join(' ') : "specResultRow"),
                            "id"   : specName]) {
                            td(['style': ReportStylesheet.styleMap.get('table.summary-table td'), "id": testPackageName, "class": "specResultPackageName"]) {
                                span testPackageName
                            }
                            td(['style': ReportStylesheet.styleMap.get('table.summary-table td'), "id": testClearName, "class": "specResultName"]) {
                                span testClearName
                            }
                            td('style': ReportStylesheet.styleMap.get('table.summary-table td'), "class": "browser", browser)
                            td('style': ReportStylesheet.styleMap.get('table.summary-table td')) {
                                a(href: "${specName}.html", "reports")
                            }
                            td(['style': ReportStylesheet.styleMap.get('table.summary-table td'), "class": "totalStat"], stats.totalRuns)
                            td(['style': ReportStylesheet.styleMap.get('table.summary-table td'), "class": "failedStat"], stats.failures)
                            td(['style': ReportStylesheet.styleMap.get('table.summary-table td'), "class": "ignoredStat"], stats.ignoreFailures)
                            td('style': ReportStylesheet.styleMap.get('table.summary-table td'), stringFormatter.toPercentage(stats.successRate))
                            td(['style': ReportStylesheet.styleMap.get('table.summary-table td') +" white-space: nowrap;", "class": "execution_time", "value": stats.time]){
                                div(['class':'div_artificial_hide'],String.format("%09d", stats.time))
                                span stringFormatter.toTimeDuration(stats.time)
                            }
                            td(['style': ReportStylesheet.styleMap.get('table.summary-table td')+ ReportStylesheet.styleMap.get('td.last'), "class": "start_time"]){
                                div(['class': 'div_artificial_hide'], stats.startTime)
                                span DateStringFormatHelper.toDateString(new Date(stats.startTime), "MM/dd/yyyy HH:mm:ss")
                            }
                        }
                    }
                }
                mkp.yieldUnescaped(appendResult)
            }
        }
    }

}
