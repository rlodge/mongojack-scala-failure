package com.electronicmuse.mongojack.failure

case class CourseReports(
	courseUri: String,
	courseName: String,
	reports: List[Report]
) {
}
