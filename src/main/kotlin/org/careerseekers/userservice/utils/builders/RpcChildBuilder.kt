package org.careerseekers.userservice.utils.builders

import com.careerseekers.grpc.children.FullChild
import com.careerseekers.grpc.children.ShortChild
import org.careerseekers.userservice.annotations.Utility
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.ChildService

@Utility
class RpcChildBuilder(
    private val childService: ChildService,
    private val rpcUserBuilder: RpcUserBuilder
) {

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
                .setUser(rpcUserBuilder.buildRpcUser(child.user.id))
                .setMentor(rpcUserBuilder.buildRpcUser(child.mentor?.id ?: child.user.id))
                .build()
        }
    }
}