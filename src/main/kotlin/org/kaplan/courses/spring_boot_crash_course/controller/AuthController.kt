package org.kaplan.courses.spring_boot_crash_course.controller

import org.kaplan.courses.spring_boot_crash_course.controller.model.AuthRequest
import org.kaplan.courses.spring_boot_crash_course.controller.model.RefreshRequest
import org.kaplan.courses.spring_boot_crash_course.security.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/auth"])
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody body: AuthRequest) {
        authService.register(body.email, body.password)
    }

    @PostMapping("/login")
    fun login(@RequestBody body: AuthRequest): AuthService.TokenPair {
        return authService.login(body.email, body.password)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody body: RefreshRequest): AuthService.TokenPair {
        return authService.refreshToken(body.refreshToken)
    }
}