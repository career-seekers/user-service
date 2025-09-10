package org.careerseekers.userservice.mocks

import io.mockk.mockk
import io.mockk.spyk
import org.careerseekers.userservice.cache.TemporaryPasswordsCache
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.mappers.UsersMapper
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder

open class UsersServiceMocks {
    protected val repository = mockk<UsersRepository>()
    protected val jwtUtil = mockk<JwtUtil>()
    protected val usersMapper = mockk<UsersMapper>()
    protected val passwordEncoder = mockk<PasswordEncoder>()
    protected val emailSendingProducer = mockk<KafkaEmailSendingProducer>()
    protected val verificationCodesCacheClient = mockk<VerificationCodesCacheClient>()
    protected val emailVerificationCodeVerifier = mockk<EmailVerificationCodeVerifier>()
    protected val temporaryPasswordsCache = mockk<TemporaryPasswordsCache>()

    protected val usersServiceMock = mockk<UsersService>(relaxed = true)
    protected val serviceUnderTest = spyk(UsersService(
        repository,
        jwtUtil,
        usersMapper,
        passwordEncoder,
        emailSendingProducer,
        temporaryPasswordsCache,
        verificationCodesCacheClient,
        emailVerificationCodeVerifier,
        usersServiceMock
    ), recordPrivateCalls = true)
}