package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.users.ChildId
import com.careerseekers.grpc.users.ChildWithUser
import com.careerseekers.grpc.users.User
import com.careerseekers.grpc.users.UserId
import com.careerseekers.grpc.users.UsersServiceGrpc
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.cache.UsersCacheLoader
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.ChildService
import org.careerseekers.userservice.services.UsersService
import org.springframework.transaction.annotation.Transactional

@GrpcService
class RpcUsersService(
    private val usersService: UsersService,
    private val childService: ChildService,
    private val usersCacheLoader: UsersCacheLoader,
) : UsersServiceGrpc.UsersServiceImplBase() {

    override fun getById(request: UserId, responseObserver: StreamObserver<User>) {
        val response = buildRpcUser(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    @Transactional
    override fun getChildWithUser(request: ChildId, responseObserver: StreamObserver<ChildWithUser>) {
        val response = childService.getById(
            request.id,
            message = "Ребёнок с ID ${request.id} не найден."
        )!!.let { child ->
            val rpcUser = buildRpcUser(child.user.id)
            val rpcMentor = buildRpcUser(child.mentor?.id ?: child.user.id)

            ChildWithUser.newBuilder()
                .setId(child.id)
                .setLastName(child.lastName)
                .setFirstName(child.firstName)
                .setPatronymic(child.patronymic)
                .setDateOfBirth(child.dateOfBirth.toTimestamp())
                .setSchoolName(child.childDocuments?.studyingPlace)
                .setTrainingGroundName(child.childDocuments?.trainingGround)
                .setUser(rpcUser)
                .setMentor(rpcMentor)
                .build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    fun buildRpcUser(id: Long): User {
        return usersService.getById(
            id,
            message = "Пользователь с ID $id не найден."
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
}