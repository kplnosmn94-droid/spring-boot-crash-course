package org.kaplan.courses.spring_boot_crash_course.controller

import org.bson.types.ObjectId
import org.kaplan.courses.spring_boot_crash_course.controller.model.NoteRequest
import org.kaplan.courses.spring_boot_crash_course.controller.model.NoteResponse
import org.kaplan.courses.spring_boot_crash_course.database.data.Note
import org.kaplan.courses.spring_boot_crash_course.database.data.User
import org.kaplan.courses.spring_boot_crash_course.database.repository.NoteRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController( private val repository: NoteRepository) {

    @PostMapping
    fun save(@RequestBody request: NoteRequest): NoteResponse {
        val ownerId = SecurityContextHolder.getContext().authentication?.principal as String
        val note = repository.save(
            Note(
                id= request.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = request.title,
                content = request.content,
                color = request.color,
                createdAt = Instant.now(),
                ownerId = ObjectId(ownerId)
            )
        )

        return note.toResponse()
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication!!.principal as String
        return repository.findByOwnerId(ObjectId(ownerId)).map { it.toResponse() }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val note = repository.findById(ObjectId(id)).orElseThrow { IllegalStateException("No note found with id $id") }
        val ownerId = SecurityContextHolder.getContext().authentication?.principal as String
        if (note.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id))
        }
    }

    private fun Note.toResponse(): NoteResponse {
        return NoteResponse(
            id = id.toHexString(),
            title = title,
            content = content,
            color = color,
            createdAt = createdAt,
        )
    }
}