package com.fortegrp.at.common.extension

import com.fortegrp.at.common.annotation.Priority
import com.fortegrp.at.common.env.Environment
import com.fortegrp.at.common.report.IReportCreator
import com.fortegrp.at.common.report.internal.ConfigLoader
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

//ReportCreator initialization stuff + Spec filtering (based on execution parameters)
class GlobalSpecExtension implements IGlobalExtension {

    def configLoader = new ConfigLoader()
    String reportCreatorClassName
    final reportCreatorSettings = [:]
    static String outputDir
    static firstVisit = true

    @Override
    void stop() {
    }

    @Override
    void start() {
    }

    @Override
    void visitSpec(SpecInfo specInfo) {
        if (firstVisit) {
            config()
            firstVisit = false
        }
        if (reportCreatorClassName)
            try {
                def reportCreator = instantiateReportCreator()
                configReportCreator(reportCreator)
                specInfo.addListener new SpecInfoListener(reportCreator)
            } catch (e) {
                e.printStackTrace()
                println "Failed to create instance of $reportCreatorClassName: $e"
            }

        //filter tests by test category
        if (!Environment.testCategory.equals("all")) {
            specInfo.features.findAll { !it.skipped }.each { feature ->
                def priorityAnnotation = feature.getDescription().getAnnotation(Priority)
                def testPriority = priorityAnnotation ? priorityAnnotation.value() : "low"
                if (!Environment.testCategory.contains(testPriority)) {
                    feature.setSkipped(true)
                }
            }
        }
    }

    void config() {
        def config = configLoader.loadConfig()
        reportCreatorClassName = config.getProperty(IReportCreator.class.name)
        outputDir = config.getProperty("spockframework.report.outputDir")
        try {
            reportCreatorSettings << loadSettingsFor(reportCreatorClassName, config)
        } catch (e) {
            e.printStackTrace()
            println "Error configuring ${this.class.name}! $e"
        }
    }

    def instantiateReportCreator() {
        def reportCreatorClass = Class.forName(reportCreatorClassName)
        reportCreatorClass.asSubclass(IReportCreator).newInstance()
    }

    def loadSettingsFor(String prefix, Properties config) {
        Collections.list(config.propertyNames()).grep { String key ->
            key.startsWith prefix + '.'
        }.collect { String key ->
            [(key - (prefix + '.')): config.getProperty(key)]
        }.collectEntries()
    }

    private void configReportCreator(IReportCreator reportCreator) {
        reportCreator.outputDir = outputDir
        reportCreatorSettings.each { field, value ->
            reportCreator."$field" = value
        }
    }

}

