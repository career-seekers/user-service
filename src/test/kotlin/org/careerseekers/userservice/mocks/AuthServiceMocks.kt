package org.careerseekers.userservice.mocks

import io.mockk.mockk
import io.mockk.spyk
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.services.AuthService
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.services.processors.ExpertRegistrationProcessor
import org.careerseekers.userservice.services.processors.IUsersRegistrationProcessor
import org.careerseekers.userservice.services.processors.UserRegistrationProcessor
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder

open class AuthServiceMocks {
    protected val jwtUtil = mockk<JwtUtil>()
    protected val usersService = mockk<UsersService>()
    protected val passwordEncoder = mockk<PasswordEncoder>()
    protected val userPostProcessor = mockk<UserRegistrationProcessor>()
    protected val expertPostProcessor = mockk<ExpertRegistrationProcessor>()
    protected val registrationPostProcessors = listOf<IUsersRegistrationProcessor>(userPostProcessor, expertPostProcessor)
    protected val emailSendingProducer = mockk<KafkaEmailSendingProducer>()
    protected val emailVerificationCodeVerifier = mockk<EmailVerificationCodeVerifier>()
    protected val verificationCodesCacheClient = mockk<VerificationCodesCacheClient>()

    protected val serviceUnderTest = spyk(AuthService(
        jwtUtil = jwtUtil,
        usersService = usersService,
        passwordEncoder = passwordEncoder,
        registrationPostProcessors = registrationPostProcessors,
        emailSendingProducer = emailSendingProducer,
        emailVerificationCodeVerifier = emailVerificationCodeVerifier,
        verificationCodesCacheClient = verificationCodesCacheClient,
    ))
}