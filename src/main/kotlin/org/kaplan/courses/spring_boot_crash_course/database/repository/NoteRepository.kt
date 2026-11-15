package org.kaplan.courses.spring_boot_crash_course.database.repository

import org.bson.types.ObjectId
import org.kaplan.courses.spring_boot_crash_course.database.data.Note
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository : MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Note>
}