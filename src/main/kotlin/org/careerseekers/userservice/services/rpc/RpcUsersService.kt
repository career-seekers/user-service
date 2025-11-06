package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.users.ChildId
import com.careerseekers.grpc.users.ChildWithUser
import com.careerseekers.grpc.users.User
import com.careerseekers.grpc.users.UserId
import com.careerseekers.grpc.users.UsersServiceGrpc
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.ChildService
import org.careerseekers.userservice.utils.builders.RpcUserBuilder
import org.springframework.transaction.annotation.Transactional

@GrpcService
class RpcUsersService(
    private val childService: ChildService,
    private val rpcUserBuilder: RpcUserBuilder
) : UsersServiceGrpc.UsersServiceImplBase() {

    override fun getById(request: UserId, responseObserver: StreamObserver<User>) {
        val response = rpcUserBuilder.buildRpcUser(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    @Transactional
    override fun getChildWithUser(request: ChildId, responseObserver: StreamObserver<ChildWithUser>) {
        val response = childService.getById(
            request.id,
            message = "Ребёнок с ID ${request.id} не найден."
        )!!.let { child ->
            ChildWithUser.newBuilder()
                .setId(child.id)
                .setLastName(child.lastName)
                .setFirstName(child.firstName)
                .setPatronymic(child.patronymic)
                .setDateOfBirth(child.dateOfBirth.toTimestamp())
                .setSchoolName(child.childDocuments?.studyingPlace)
                .setTrainingGroundName(child.childDocuments?.trainingGround)
                .setUser(rpcUserBuilder.buildRpcUser(child.user))
                .setMentor(rpcUserBuilder.buildRpcUser(child.mentor ?: child.user))
                .build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}