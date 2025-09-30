package org.careerseekers.userservice.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse
import java.util.Date

@Entity
@Table(name = "users")
data class Users(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var patronymic: String,

    @Column(nullable = true)
    var dateOfBirth: Date? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var mobileNumber: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var role: UsersRoles,

    @Column(nullable = true)
    var avatarId: Long,

    @Column(nullable = false)
    var verified: Boolean = false,

    @Column(nullable = false)
    var isMentor: Boolean = false,

    @Column(nullable = true)
    var tutorId: Long? = null,

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var jwtTokens: MutableList<JwtTokensStorage>? = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnoreProperties("user")
    var expertDocuments: ExpertDocuments?,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnoreProperties("user")
    var tutorDocuments: TutorDocuments?,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnoreProperties("user")
    var mentorDocuments: MentorDocuments?,

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "mentor", cascade = [CascadeType.ALL], orphanRemoval = true)
    var menteeChildren: MutableList<Children>? = mutableListOf(),

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var children: MutableList<Children>? = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties(value = ["user"])
    var telegramLink: TelegramLinks? = null
) : ConvertableToHttpResponse<Users>