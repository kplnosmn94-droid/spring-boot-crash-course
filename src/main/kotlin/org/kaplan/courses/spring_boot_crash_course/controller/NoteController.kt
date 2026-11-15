package org.kaplan.courses.spring_boot_crash_course.controller

import org.bson.types.ObjectId
import org.kaplan.courses.spring_boot_crash_course.controller.model.NoteRequest
import org.kaplan.courses.spring_boot_crash_course.controller.model.NoteResponse
import org.kaplan.courses.spring_boot_crash_course.database.data.Note
import org.kaplan.courses.spring_boot_crash_course.database.repository.NoteRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController( private val repository: NoteRepository) {

    @PostMapping
    fun save(request: NoteRequest): NoteResponse {
        val note = repository.save(
            Note(
                id= request.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = request.title,
                content = request.content,
                color = request.color,
                createdAt = Instant.now(),
                ownerId = ObjectId(request.ownerId)
            )
        )

        return note.toResponse()
    }

    @GetMapping
    fun findByOwnerId(@RequestParam(required = true) ownerId: String): List<NoteResponse> {
        return repository.findByOwnerId(ObjectId(ownerId)).map { it.toResponse() }
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