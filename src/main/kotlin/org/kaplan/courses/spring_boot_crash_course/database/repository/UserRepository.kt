package org.kaplan.courses.spring_boot_crash_course.database.repository

import org.bson.types.ObjectId
import org.kaplan.courses.spring_boot_crash_course.database.data.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, ObjectId> {
    fun findByEmail(email: String): User?
}