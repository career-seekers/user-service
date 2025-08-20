package org.careerseekers.userservice.utils

import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.UsersCacheDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.springframework.security.crypto.password.PasswordEncoder

@Utility
class EmailVerificationCodeVerifier(
    private val verificationCodesCacheClient: VerificationCodesCacheClient,
    private val passwordEncoder: PasswordEncoder,
    private val emailSendingProducer: KafkaEmailSendingProducer,
) {

    fun verify(
        email: String,
        verificationCode: String,
        token: String? = null,
        mailEventTypes: MailEventTypes,
        user: UsersCacheDto? = null
    ) {
        val cacheItem = verificationCodesCacheClient.getItemFromCache(email)
            ?: throw NotFoundException("Cached verification code was not found.")

        if (!passwordEncoder.matches(verificationCode, cacheItem.code)) {
            verificationCodesCacheClient.deleteItemFromCache(email)
            if (cacheItem.retries < 3) {
                cacheItem.retries += 1
                verificationCodesCacheClient.loadItemToCache(cacheItem)
                throw BadRequestException("Incorrect verification code")
            } else {
                emailSendingProducer.sendMessage(
                    EmailSendingTaskDto(
                        email = email,
                        token = token,
                        eventType = mailEventTypes,
                        user = user)
                )
                throw BadRequestException("The maximum number of attempts has been reached. A new code has been sent to the mail")
            }
        }
    }
}