package com.example.kopring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value

@SpringBootApplication
class KopringApplication

fun main(args: Array<String>) {
	runApplication<KopringApplication>(*args)

//	@Value("${db.host}")

	try {
		val ds = PGSimpleDataSource()
		ds.setServerNames(arrayOf("localhost"))
		ds.setPortNumbers(intArrayOf(26257))
		ds.setDatabaseName("defaultdb")
		ds.setUser("root")
		ds.setSsl(false)
		ds.setSslMode("disable")
		ds.setApplicationName("App")
		println("Hey! You connected to your CockroachDB cluster.")
	} catch (e: Exception) {
		e.printStackTrace()
	}

}
