package com.electronicmuse.mongojack.failure

import java.time.OffsetDateTime
import java.util.Date

import javax.persistence.Id
import org.bson.types.ObjectId

case class ACaseClass(
	someValue: String,
	someDate: Date,
	someOffsetDateTime: OffsetDateTime,
	someChildren: List[Child]
) {
	@Id var id: ObjectId  = new ObjectId()
}
