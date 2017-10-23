package com.fortegrp.at.common.report

import com.fortegrp.at.common.report.internal.SpecData

interface IReportCreator {

	void createReportFor( SpecData data )

	void setOutputDir( String path )

}