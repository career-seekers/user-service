package org.careerseekers.userservice.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse
import java.util.Date

@Entity
@Table(name = "users")
data class Users (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    val patronymic: String,

    @Column(nullable = true)
    var dateOfBirth: Date,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, unique = true)
    var mobileNumber: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var role: UsersRoles,

    @Column(nullable = false)
    var avatarId: Long
) : ConvertableToHttpResponse<Users>