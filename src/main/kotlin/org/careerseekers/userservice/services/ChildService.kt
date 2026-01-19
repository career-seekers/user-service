package org.careerseekers.userservice.services

import org.careerseekers.userservice.annotations.ChildrenUpdate
import org.careerseekers.userservice.dto.filters.ChildrenFilterDto
import org.careerseekers.userservice.dto.users.CreateChildDto
import org.careerseekers.userservice.dto.users.UpdateChildDto
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.mappers.ChildrenMapper
import org.careerseekers.userservice.repositories.ChildrenRepository
import org.careerseekers.userservice.repositories.spec.ChildrenSpecifications.hasName
import org.careerseekers.userservice.services.interfaces.CrudService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChildService(
    override val repository: ChildrenRepository,
    private val usersService: UsersService,
    private val childrenMapper: ChildrenMapper,
) : CrudService<Children, Long, CreateChildDto, UpdateChildDto> {

    fun getAll(filters: ChildrenFilterDto, pageable: Pageable): Page<Children> {
        val specs = listOfNotNull(
            hasName(filters.name),
        )

        return repository.findAll(Specification.allOf(specs), pageable)
    }

    fun getByUserId(userId: Long) = repository.findByUserId(userId)

    @ChildrenUpdate
    @Transactional
    override fun create(item: CreateChildDto): Children {
        item.user = usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.")!!
        item.mentorId?.let {
            item.mentor = usersService.getById(it, message = "Пользователь с ID ${item.mentorId} не найден.")!!
        }

        return repository.save(childrenMapper.childFromDto(item))
    }

    @ChildrenUpdate
    @Transactional
    override fun createAll(items: List<CreateChildDto>): MutableList<Children> {
        val children = mutableListOf<Children>()
        for (item in items) {
            item.user = usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.")!!
            item.mentorId?.let {
                item.mentor =
                    usersService.getById(item.mentorId, message = "Пользователь с ID ${item.mentorId} не найден.")!!
            }

            children.add(repository.save(childrenMapper.childFromDto(item)))
        }

        return children
    }

    @Transactional
    override fun update(item: UpdateChildDto): String {
        getById(item.id, message = "Ребёнок с ID ${item.id} не найден.")!!.apply {
            item.lastName?.let { lastName = it }
            item.firstName?.let { firstName = it }
            item.patronymic?.let { patronymic = it }
            item.dateOfBirth?.let { dateOfBirth = it }
            item.mentorId?.let {
                val user =
                    usersService.getById(item.mentorId, message = "Пользователь с ID ${item.mentorId} не найден.")!!
                if (user.role != UsersRoles.MENTOR && user.role != UsersRoles.USER && !user.isMentor) {
                    throw BadRequestException("Пользователь с ID $it не является Наставником Чемпионата.")
                }

                mentor = user
            }
        }.also(repository::save)

        return "Данные о ребёнке обновлены успешно."
    }

    @ChildrenUpdate
    @Transactional
    override fun deleteById(id: Long): String {
        getById(id, message = "Ребёнок с ID $id не найден.")!!.let {
            repository.delete(it)
        }
        return "Данные о ребёнке удалены успешно."
    }

    @ChildrenUpdate
    @Transactional
    override fun deleteAll(): String {
        repository.deleteAll()

        return "Данные обо всех детях удалены успешно."
    }
}