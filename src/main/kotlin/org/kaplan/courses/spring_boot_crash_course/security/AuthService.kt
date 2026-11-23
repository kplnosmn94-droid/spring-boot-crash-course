package org.kaplan.courses.spring_boot_crash_course.security

import org.bson.types.ObjectId
import org.kaplan.courses.spring_boot_crash_course.database.data.RefreshToken
import org.kaplan.courses.spring_boot_crash_course.database.data.User
import org.kaplan.courses.spring_boot_crash_course.database.repository.RefreshTokenRepository
import org.kaplan.courses.spring_boot_crash_course.database.repository.UserRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    data class TokenPair(val accessToken: String, val refreshToken: String)

    fun register(email: String, password: String): User {
        return userRepository.save(User(email, hashEncoder.encode(password)))
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid credentials.")
        if(!hashEncoder.matches(password, user.hashedPassword)) throw BadCredentialsException("Invalid credentials.")

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(newAccessToken, newRefreshToken)
    }

    @Transactional
    fun refreshToken(refreshToken: String): TokenPair {
        if(!jwtService.validateRefreshToken(refreshToken)) throw BadCredentialsException("Invalid refresh token.")

        val userId = jwtService.getUserIdFromJWT(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            throw BadCredentialsException("Invalid refresh token.")
        }
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw BadCredentialsException("Refresh token not recognized.")
        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)
        return TokenPair(newAccessToken, newRefreshToken)
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(RefreshToken(userId, expiresAt, hashed))
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}