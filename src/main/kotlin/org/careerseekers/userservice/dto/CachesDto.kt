package org.careerseekers.userservice.dto

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.serializers.DateSerializer
import java.util.Date

@Polymorphic
@Serializable
sealed class CachesDto : DtoClass

@Serializable
@SerialName("UsersCacheDto")
data class UsersCacheDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    @Serializable(with = DateSerializer::class)
    val dateOfBirth: Date,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val role: UsersRoles,
    val avatarId: Long,
    val verified: Boolean,
) : CachesDto()

@Serializable
@SerialName("VerificationCodeDto")
data class VerificationCodeDto(
    val code: String,
    var retries: Int
) : CachesDto()