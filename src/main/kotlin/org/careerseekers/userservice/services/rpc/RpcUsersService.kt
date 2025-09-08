package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.users.User
import com.careerseekers.grpc.users.UserId
import com.careerseekers.grpc.users.UsersServiceGrpc
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.cache.UsersCacheLoader
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.UsersService

@GrpcService
class RpcUsersService(
    private val usersService: UsersService,
    private val usersCacheLoader: UsersCacheLoader,
) : UsersServiceGrpc.UsersServiceImplBase() {

    override fun getById(request: UserId, responseObserver: StreamObserver<User>) {
        val response = usersService.getById(
            request.id,
            message = "User with id ${request.id} not found."
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
                .build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}