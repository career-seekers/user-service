package org.careerseekers.userservice.utils.validators

import org.careerseekers.userservice.annotations.Utility
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
            ?: throw NotFoundException("Верификационный код не найден. Повторите попытку позже.")

        if (!passwordEncoder.matches(verificationCode, cacheItem.code)) {
            verificationCodesCacheClient.deleteItemFromCache(email)
            if (cacheItem.retries < 3) {
                cacheItem.retries += 1
                verificationCodesCacheClient.loadItemToCache(cacheItem)
                throw BadRequestException("Неверный верификационный код.")
            } else {
                emailSendingProducer.sendMessage(
                    EmailSendingTaskDto(
                        email = email,
                        token = token,
                        eventType = mailEventTypes,
                        user = user
                    )
                )
                throw BadRequestException("Достигнуто максимальное количество попыток. На почту отправлен новый код.")
            }
        }
    }
}