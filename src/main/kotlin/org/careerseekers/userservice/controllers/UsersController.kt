package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.controllers.interfaces.CrudController
import org.careerseekers.userservice.dto.users.ChangeUserRoleDto
import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.dto.users.UpdateUserDto
import org.careerseekers.userservice.dto.users.VerifyUserDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users-service/v1/users")
class UsersController(
    override val service: UsersService,
    val repository: UsersRepository,
) : CrudController<Users, Long, CreateUserDto, UpdateUserDto> {

    @GetMapping("/")
    override fun getAll(): BasicSuccessfulResponse<List<Users>> =
        service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long): BasicSuccessfulResponse<Users> =
        service.getById(
            id,
            message = "User with id $id does not exist."
        )!!.toHttpResponse()

    @GetMapping("getByEmail/{email}")
    fun getByEmail(@PathVariable email: String): BasicSuccessfulResponse<Users> =
        service.getByEmail(email)!!.toHttpResponse()

    @GetMapping("/getByMobileNumber/{mobileNumber}")
    fun getByMobileNumber(@PathVariable mobileNumber: String): BasicSuccessfulResponse<Users> =
        service.getByMobileNumber(mobileNumber)!!.toHttpResponse()

    @GetMapping("/getByRole/{role}")
    fun getByRole(@PathVariable role: String): BasicSuccessfulResponse<List<Users>> =
        service.getByRole(UsersRoles.valueOf(role.uppercase())).toHttpResponse()

    @GetMapping("/getByTutorId/{id}")
    fun getByTutorId(@PathVariable id: Long): BasicSuccessfulResponse<List<Users>> =
        service.getByTutorId(id).toHttpResponse()

    @PostMapping("/")
    override fun create(@RequestBody item: CreateUserDto): BasicSuccessfulResponse<Users> =
        service.create(item).toHttpResponse()

    @PostMapping("/createAll")
    override fun createAll(@RequestBody items: List<CreateUserDto>): BasicSuccessfulResponse<String> {
        service.createAll(items)
        return BasicSuccessfulResponse("Users created successfully")
    }

    @PatchMapping("/")
    override fun update(@RequestBody item: UpdateUserDto): BasicSuccessfulResponse<String> =
        service.update(item).toHttpResponse()

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/updateUserRole")
    fun updateUserRole(@RequestBody item: ChangeUserRoleDto): BasicSuccessfulResponse<String> =
        service.updateRole(item).toHttpResponse()

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/verify")
    fun verifyUser(@RequestBody item: VerifyUserDto) = service.verifyUser(item).toHttpResponse()

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long): BasicSuccessfulResponse<String> =
        service.deleteById(id).toHttpResponse()

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/")
    override fun deleteAll(): BasicSuccessfulResponse<String> =
        service.deleteAll().toHttpResponse()
}