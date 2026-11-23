package org.kaplan.courses.spring_boot_crash_course.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {

    private val bcrypt = BCryptPasswordEncoder()

    fun encode(value: String): String = bcrypt.encode(value)!!

    fun matches(value: String, encodedValue: String): Boolean = bcrypt.matches(value, encodedValue)

}