package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.users.Child
import com.careerseekers.grpc.users.ChildList
import com.careerseekers.grpc.users.User
import com.careerseekers.grpc.users.UserId
import com.careerseekers.grpc.users.UserWithChildren
import com.careerseekers.grpc.users.UsersServiceGrpc
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.cache.UsersCacheLoader
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.UsersService
import org.springframework.transaction.annotation.Transactional

@GrpcService
class RpcUsersService(
    private val usersService: UsersService,
    private val usersCacheLoader: UsersCacheLoader,
) : UsersServiceGrpc.UsersServiceImplBase() {

    override fun getById(request: UserId, responseObserver: StreamObserver<User>) {
        val response = usersService.getById(
            request.id,
            message = "Пользователь с ID ${request.id} не найден."
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

    @Transactional
    override fun getUserWithChildren(request: UserId, responseObserver: StreamObserver<UserWithChildren>) {
        val response = usersService.getById(
            request.id,
            message = "Пользователь с ID ${request.id} не найден."
        )!!.let { user ->
            usersCacheLoader.loadItemToCache(user.toCache())

            val childList = ChildList.newBuilder()
            user.children?.forEach { child ->
                val rpcChild = Child.newBuilder()
                    .setId(child.id)
                    .setLastName(child.lastName)
                    .setFirstName(child.firstName)
                    .setPatronymic(child.patronymic)
                    .setDateOfBirth(child.dateOfBirth.toTimestamp())
                    .build()

                childList.addChildren(rpcChild)
            }

            UserWithChildren.newBuilder()
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
                .setChildren(childList)
                .build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}