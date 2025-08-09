package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.dto.users.UpdateUserDto
import org.careerseekers.userservice.dto.users.VerifyUserDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.FileTypes
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.UsersMapper
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.interfaces.CrudService
import org.careerseekers.userservice.utils.DocumentExistenceChecker
import org.careerseekers.userservice.utils.MobileNumberFormatter.checkMobileNumberValid
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsersService(
    override val repository: UsersRepository,
    private val usersMapper: UsersMapper,
    private val passwordEncoder: PasswordEncoder,
    private val documentExistenceChecker: DocumentExistenceChecker,
    @Lazy private val usersService: UsersService,
) : CrudService<Users, Long, CreateUserDto, UpdateUserDto> {

    @Value("\${file-service.default-avatar-id}")
    private lateinit var defaultAvatarId: String

    @Cacheable("users-service")
    override fun getById(id: Long?, throwable: Boolean, message: String): Users? {
        return super.getById(id, throwable, message)
    }

    fun getAllByIds(ids: List<Long>): List<Users> = repository.findAllById(ids)

    @Cacheable("users-service")
    fun getByEmail(email: String, throwable: Boolean = true): Users? {
        return repository.getByEmail(email)
            ?: if (throwable) throw NotFoundException("User with email $email not found") else null
    }

    @Cacheable("users-service")
    fun getByMobileNumber(mobileNumber: String, throwable: Boolean = true): Users? {
        return repository.getByMobileNumber(mobileNumber)
            ?: if (throwable) throw NotFoundException("User with mobile number $mobileNumber not found") else null
    }

    @Transactional
    override fun create(item: CreateUserDto): Users {
        if (item.avatarId != null && item.avatarId != defaultAvatarId.toLongOrNull()) {
            documentExistenceChecker.checkFileExistence(item.avatarId, FileTypes.AVATAR)
        }

        checkIfUserExistsByEmailOrMobile(item.email, item.mobileNumber)
        checkMobileNumberValid(item.mobileNumber)

        val userToSave = usersMapper.usersFromCreateDto(
            item.copy(password = passwordEncoder.encode(item.password), avatarId = item.avatarId ?: defaultAvatarId.toLongOrNull()),
        )
        return repository.save(userToSave)
    }

    @Transactional
    override fun createAll(items: List<CreateUserDto>) {
        items.forEach { checkIfUserExistsByEmailOrMobile(it.email, it.mobileNumber) }
        items.forEach { checkMobileNumberValid(it.mobileNumber) }

        val usersToSave = items.map {
            usersMapper.usersFromCreateDto(
                it.copy(password = passwordEncoder.encode(it.password))
            )
        }
        repository.saveAll(usersToSave)
    }

    @Transactional
    override fun update(item: UpdateUserDto): String {
        val user = usersService.getById(item.id, message = "User with id ${item.id} does not exist.")!!

        item.firstName?.let { user.firstName = it }
        item.lastName?.let { user.lastName = it }
        item.patronymic?.let { user.patronymic = it }

        return "User updated successfully."
    }

    @Transactional
    fun verifyUser(item: VerifyUserDto): String {
        usersService.getById(item.userId, message = "User with id ${item.userId} does not exist.").let {
            it?.verified = item.status
        }
        return "User verification updated successfully."
    }

    @Transactional
    override fun deleteById(id: Long): String {
        usersService.getById(id, message = "User with id $id does not exist.")?.let {
            repository.deleteById(id)
        }

        return "User deleted successfully."
    }

    @Transactional
    override fun deleteAll(): String {
        super.deleteAll()

        return "Users deleted successfully."
    }

    private fun checkIfUserExistsByEmailOrMobile(email: String, mobile: String) {
        if (usersService.getByEmail(email, false) != null) {
            throw DoubleRecordException("User with email $email already exists")
        }
        if (usersService.getByMobileNumber(mobile, false) != null) {
            throw DoubleRecordException("User with mobile number $mobile already exists")
        }
    }
}