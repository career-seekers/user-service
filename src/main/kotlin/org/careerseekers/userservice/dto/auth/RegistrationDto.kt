package org.careerseekers.userservice.dto.auth

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.serializers.DateSerializer
import org.careerseekers.userservice.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
@Polymorphic
sealed interface RegistrationDto : DtoClass {
    val verificationCode: String
    val firstName: String
    val lastName: String
    val patronymic: String
    @Serializable(with = DateSerializer::class)
    val dateOfBirth: Date
    val email: String
    val mobileNumber: String
    val password: String?
    val role: UsersRoles
    val avatarId: Long?
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID
}

@Serializable
@SerialName("UserRegistrationDto")
data class UserRegistrationDto(
    override val verificationCode: String,
    override val firstName: String,
    override val lastName: String,
    override val patronymic: String,
    @Serializable(with = DateSerializer::class)
    override val dateOfBirth: Date,
    override val email: String,
    override val mobileNumber: String,
    override val password: String?,
    override val role: UsersRoles,
    override val avatarId: Long?,
    @Serializable(with = UUIDSerializer::class)
    override val uuid: UUID,
) : RegistrationDto

@Serializable
@SerialName("UserWithChildRegistrationDto")
data class UserWithChildRegistrationDto(
    override val verificationCode: String,
    override val firstName: String,
    override val lastName: String,
    override val patronymic: String,
    @Serializable(with = DateSerializer::class)
    override val dateOfBirth: Date,
    override val email: String,
    override val mobileNumber: String,
    override val password: String?,
    override val role: UsersRoles,
    override val avatarId: Long?,
    @Serializable(with = UUIDSerializer::class)
    override val uuid: UUID,
    val mentorEqualsUser: Boolean,
    val childFirstName: String,
    val childLastName: String,
    val childPatronymic: String,
    @Serializable(with = DateSerializer::class)
    val childDateOfBirth: Date,
    val mentorId: Long? = null,
) : RegistrationDto