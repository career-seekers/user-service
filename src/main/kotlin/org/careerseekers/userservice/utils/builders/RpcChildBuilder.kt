package org.careerseekers.userservice.utils.builders

import com.careerseekers.grpc.children.FullChild
import com.careerseekers.grpc.children.ShortChild
import org.careerseekers.userservice.annotations.Utility
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.io.converters.extensions.toTimestamp
import org.careerseekers.userservice.services.ChildService

@Utility
class RpcChildBuilder(
    private val childService: ChildService,
    private val rpcUserBuilder: RpcUserBuilder
) {

    fun buildRpcChild(id: Long): ShortChild? {
        return childService.getById(id, message = "Ребёнок с ID $id не найден.")?.let { child ->
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

    fun buildRpcChild(child: Children): ShortChild? {
        return ShortChild.newBuilder()
            .setId(child.id)
            .setLastName(child.lastName)
            .setFirstName(child.firstName)
            .setPatronymic(child.patronymic)
            .setDateOfBirth(child.dateOfBirth.toTimestamp())
            .setCreatedAt(child.createdAt?.toTimestamp())
            .build()
    }

    fun buildRpcFullChild(id: Long): FullChild {
        return childService.getById(id, message = "Ребёнок с ID $id не найден.")!!.let { child ->
            FullChild.newBuilder()
                .setId(child.id)
                .setLastName(child.lastName)
                .setFirstName(child.firstName)
                .setPatronymic(child.patronymic)
                .setDateOfBirth(child.dateOfBirth.toTimestamp())
                .setCreatedAt(child.createdAt?.toTimestamp())
                .setUser(rpcUserBuilder.buildRpcUser(child.user))
                .setMentor(rpcUserBuilder.buildRpcUser(child.mentor ?: child.user))
                .build()
        }
    }

    fun buildRpcFullChild(child: Children): FullChild {
        return FullChild.newBuilder()
            .setId(child.id)
            .setLastName(child.lastName)
            .setFirstName(child.firstName)
            .setPatronymic(child.patronymic)
            .setDateOfBirth(child.dateOfBirth.toTimestamp())
            .setCreatedAt(child.createdAt?.toTimestamp())
            .setUser(rpcUserBuilder.buildRpcUser(child.user))
            .setMentor(rpcUserBuilder.buildRpcUser(child.mentor ?: child.user))
            .build()
    }
}