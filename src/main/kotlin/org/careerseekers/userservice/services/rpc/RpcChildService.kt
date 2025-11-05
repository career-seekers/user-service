package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.children.*
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.ChildService

@GrpcService
class RpcChildService(
    private val rpcUsersService: RpcUsersService,
    private val childService: ChildService,
) : ChildrenServiceGrpc.ChildrenServiceImplBase() {

    override fun getById(request: Id, responseObserver: StreamObserver<ShortChild?>) {
        val response = buildRpcChild(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getByIdFull(request: Id, responseObserver: StreamObserver<FullChild>) {
        val response = buildRpcFullChild(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getAll(request: Empty, responseObserver: StreamObserver<ChildrenList>) {
        val childrenList = ChildrenList.newBuilder()

        childService.getAll()
            .map { child -> buildRpcChild(child.id) }
            .run { childrenList.addAllChildren(this) }

        responseObserver.onNext(childrenList.build())
        responseObserver.onCompleted()
    }

    override fun getAllFull(request: Empty, responseObserver: StreamObserver<FullChildrenList>) {
        val childrenList = FullChildrenList.newBuilder()

        childService.getAll()
            .map { child -> buildRpcFullChild(child.id) }
            .run { childrenList.addAllChildren(this) }

        responseObserver.onNext(childrenList.build())
        responseObserver.onCompleted()
    }


    /**
     * Auxiliary functions to speed up development and reduce the amount of code.
     */
    fun buildRpcChild(id: Long): ShortChild? {
        return childService.getById(id, message = "Ребенок с id $id не найден.")?.let { child ->
            ShortChild.newBuilder()
                .setId(child.id)
                .setLastName(child.lastName)
                .setFirstName(child.firstName)
                .setPatronymic(child.patronymic)
                .setDateOfBirth(child.dateOfBirth.toTimestamp())
                .setCreatedAt(child.createdAt?.toTimestamp())
                .build()
        }
    }

    fun buildRpcFullChild(id: Long): FullChild? {
        return childService.getById(id, message = "Ребенок с id $id не найден.")?.let { child ->
            FullChild.newBuilder()
                .setId(child.id)
                .setLastName(child.lastName)
                .setFirstName(child.firstName)
                .setPatronymic(child.patronymic)
                .setDateOfBirth(child.dateOfBirth.toTimestamp())
                .setCreatedAt(child.createdAt?.toTimestamp())
                .setUser(rpcUsersService.buildRpcUser(child.user.id))
                .setMentor(rpcUsersService.buildRpcUser(child.mentor?.id ?: child.user.id))
                .build()
        }
    }
}