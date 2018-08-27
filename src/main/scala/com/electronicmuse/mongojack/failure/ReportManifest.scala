package com.electronicmuse.mongojack.failure

import javax.persistence.Id
import org.bson.types.ObjectId


case class ReportManifest(
	userIdentifier: String,
	courseReports: List[CourseReports],
	institutionReports: List[InstitutionReports]
) {
	@Id var id: ObjectId  = new ObjectId()

}

object ReportManifest {
	def collection = "ReportManifest"
}