package org.careerseekers.userservice.utils.builders

import com.careerseekers.grpc.users.User
import org.careerseekers.userservice.annotations.Utility
import org.careerseekers.userservice.cache.UsersCacheLoader
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.UsersService

@Utility
class RpcUserBuilder(
    private val usersService: UsersService,
    private val usersCacheLoader: UsersCacheLoader,
) {
    fun buildRpcUser(id: Long): User {
        return usersService.getById(
            id, message = "Пользователь с ID $id не найден."
        )!!.let { user ->
            usersCacheLoader.loadItemToCache(user.toCache())

            User.newBuilder()
                .setId(user.id)
                .setFirstName(user.firstName)
                .setLastName(user.lastName)
                .setPatronymic(user.patronymic)
                .setDateOfBirth(user.dateOfBirth?.toTimestamp())
                .setEmail(user.email)
                .setMobileNumber(user.mobileNumber)
                .setPassword(user.password)
                .setRole(user.role.toString())
                .setAvatarId(user.avatarId)
                .setVerified(user.verified)
                .setIsMentor(user.isMentor)
                .setTgLink(user.telegramLink?.tgLink.toString())
                .build()
        }
    }

    fun buildRpcUser(user: Users): User {
        return User.newBuilder()
            .setId(user.id)
            .setFirstName(user.firstName)
            .setLastName(user.lastName)
            .setPatronymic(user.patronymic)
            .setDateOfBirth(user.dateOfBirth?.toTimestamp())
            .setEmail(user.email)
            .setMobileNumber(user.mobileNumber)
            .setPassword(user.password)
            .setRole(user.role.toString())
            .setAvatarId(user.avatarId)
            .setVerified(user.verified)
            .setIsMentor(user.isMentor)
            .setTgLink(user.telegramLink?.tgLink.toString())
            .build()
    }
}