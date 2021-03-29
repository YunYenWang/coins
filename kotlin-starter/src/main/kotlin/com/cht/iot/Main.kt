package com.cht.iot

import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("main"); // this is a Java object

fun main(args: Array<String>) {
    val staff = Staff() // this is a Java object

    staff.id = "037682"
    staff.name = "Rick"

    log.info("id: ${staff.id}, name: ${staff.name}")

    staff.run(); // ask Java object to use Kotlin object
}
