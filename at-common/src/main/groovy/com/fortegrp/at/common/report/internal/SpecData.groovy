package com.fortegrp.at.common.report.internal

import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo

class SpecData {
	SpecInfo info
	List<FeatureRun> featureRuns = [ ]
	long startTime
	long totalTime
}

class FeatureRun {
	FeatureInfo feature
	long time
	Map<IterationInfo, List<ExtendedErrorInfo>> failuresByIteration = [ : ]
	Throwable error

	FeatureRun withExecutionTime(time){
		this.time=time
		this
	}
}

class ExtendedErrorInfo  {
    public int stepNumber
    public ErrorInfo ei

    ExtendedErrorInfo(ErrorInfo ei, int stepNumber) {
		this.ei=ei
		this.stepNumber=stepNumber
	}

    def getException() {
		this.ei.exception
	}

    def getMethod() {
		this.ei.method
	}
}