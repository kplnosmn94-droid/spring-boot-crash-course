package org.kaplan.courses.spring_boot_crash_course.controller.model

import java.time.Instant

data class NoteResponse(
    val id: String,
    val title: String,
    val content: String,
    val color: Long,
    val createdAt: Instant,
)