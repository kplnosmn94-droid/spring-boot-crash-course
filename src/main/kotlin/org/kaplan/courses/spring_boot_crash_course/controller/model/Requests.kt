package org.kaplan.courses.spring_boot_crash_course.controller.model


data class NoteRequest(
    val id: String?,
    val title: String,
    val content: String,
    val color: Long,
    val ownerId: String?
)