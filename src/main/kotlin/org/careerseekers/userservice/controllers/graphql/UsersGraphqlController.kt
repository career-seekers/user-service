package org.careerseekers.userservice.controllers.graphql

import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.repositories.ChildrenRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UsersGraphqlController(
    private val usersRepository: UsersRepository,
    private val childrenRepository: ChildrenRepository,
) {

    @QueryMapping
    fun user(@Argument id: Long) = usersRepository.findById(id)

    @QueryMapping
    fun users() = usersRepository.findAll()

    @QueryMapping
    fun usersByChildIds(@Argument ids: List<Long>): MutableList<Users> {
        val children = childrenRepository.findAllById(ids)
        val uniqueUserIds = children.mapNotNull { it.user.id }.distinct()

        return usersRepository.findAllById(uniqueUserIds)
    }
}