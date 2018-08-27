package com.electronicmuse.mongojack.failure

case class InstitutionReports(
	institutionUri: String,
	institutionName: String,
	reports: List[Report]
) {


}
