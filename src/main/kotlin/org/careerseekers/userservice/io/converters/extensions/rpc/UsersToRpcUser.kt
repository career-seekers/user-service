package org.careerseekers.userservice.io.converters.extensions.rpc

import com.careerseekers.grpc.users.User
import com.careerseekers.grpc.users.VerificationStatus
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.io.converters.extensions.toTimestamp

fun Users.toRpcUser(): User = User.newBuilder()
    .setId(this.id)
    .setFirstName(this.firstName)
    .setLastName(this.lastName)
    .setPatronymic(this.patronymic)
    .setDateOfBirth(this.dateOfBirth?.toTimestamp())
    .setEmail(this.email)
    .setMobileNumber(this.mobileNumber)
    .setPassword(this.password)
    .setRole(this.role.toString())
    .setAvatarId(this.avatarId)
    .setVerified(VerificationStatus.valueOf(this.verified.name))
    .setIsMentor(this.isMentor)
    .setTgLink(this.telegramLink?.tgLink.toString())
    .build()
