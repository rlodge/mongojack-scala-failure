package com.electronicmuse.mongojack.failure

import java.time.{OffsetDateTime, ZoneId}
import java.util.Date

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import de.flapdoodle.embed.process.runtime.Network
import org.junit.runner.RunWith
import org.mongojack.internal.MongoJackModule
import org.mongojack.{DBQuery, JacksonMongoCollection}
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSpec, Matchers}

import scala.collection.convert.{DecorateAsJava, DecorateAsScala}

@RunWith(classOf[JUnitRunner])
class ACaseClassTest extends FunSpec
	with Matchers
	with DecorateAsScala
	with DecorateAsJava
	with BeforeAndAfterAll
	with BeforeAndAfterEach {

	val bindIp = "localhost"
	val port = 12345
	val mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).net(new Net(bindIp, port, Network.localhostIsIPv6)).build
	var mongodExecutable: MongodExecutable = _
	var mongo: MongoClient = _
	var mongoDatabase: MongoDatabase = _
	var mongod: MongodProcess = _

	lazy val objectMapper: ObjectMapper = {
		val m = new ObjectMapper()
		m.findAndRegisterModules()
		m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		m.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
		MongoJackModule.configure(m)
		m
	}

	override protected def beforeAll(): Unit = {
		super.beforeAll()
		val starter = MongodStarter.getDefaultInstance

		mongodExecutable = starter.prepare(mongodConfig)
		mongod = mongodExecutable.start
		mongo = new MongoClient(bindIp, port)
		mongoDatabase = mongo.getDatabase("someDb")
		print("Started!")
	}

	describe("The existing MongoJack build") {

		it("should fail to serialize scala classes correctly") {
			val coll = JacksonMongoCollection.builder()
				.withObjectMapper(objectMapper)
				.build(
					mongoDatabase.getCollection("ACaseClass"),
					classOf[ACaseClass]
				)
			val expected = ACaseClass(
				"foo",
				new Date(),
				OffsetDateTime.now(ZoneId.of("UTC")),
				List(
					Child(
						new Date(),
						OffsetDateTime.now(ZoneId.of("UTC"))
					),
					Child(
						new Date(),
						OffsetDateTime.now(ZoneId.of("UTC"))
					),
					Child(
						new Date(),
						OffsetDateTime.now(ZoneId.of("UTC"))
					)
				)
			)
			coll.save(expected)

			val actual = coll.findOne(DBQuery.is("someValue", "foo"))

			actual shouldEqual expected
		}

		it("should save a more complex object tree") {
			val coll = JacksonMongoCollection.builder()
				.withObjectMapper(objectMapper)
				.build(
					mongoDatabase.getCollection("ReportManifest"),
					classOf[ReportManifest]
				)
			val expected = ReportManifest(
				userIdentifier = "b",
				courseReports = List(
					CourseReports(
						courseUri = "b",
						courseName = "name",
						reports = List(Report("report:uri", "report name", new Date(), "type"))
					)
				),
				institutionReports = List(
					InstitutionReports(
						institutionUri = "b",
						institutionName = "name",
						reports = List(Report("report:uri", "report name", new Date(), "type"))
					)
				)
			)
			coll.save(expected)

			val actual = coll.findOne(DBQuery.is("userIdentifier", "b"))

			actual shouldEqual expected
		}

	}

	override protected def afterAll(): Unit = {
		if (mongodExecutable != null) {
			mongodExecutable.stop()
			mongod.stop()
		}
		super.afterAll()
	}

}
