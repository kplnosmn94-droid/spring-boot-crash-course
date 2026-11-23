package org.kaplan.courses.spring_boot_crash_course.database.repository

import org.bson.types.ObjectId
import org.kaplan.courses.spring_boot_crash_course.database.data.RefreshToken
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository: MongoRepository<RefreshToken, ObjectId> {
    fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshToken?
    fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)
}