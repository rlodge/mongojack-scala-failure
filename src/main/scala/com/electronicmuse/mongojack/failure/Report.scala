package com.electronicmuse.mongojack.failure

import java.util.Date

case class Report(
	 reportUri: String,
	 reportName: String,
	 lastUpdateDate: Date,
	 reportType: String
) {
}
