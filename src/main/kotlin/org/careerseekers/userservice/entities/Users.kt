package org.careerseekers.userservice.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
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
    var patronymic: String,

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

    @Column(nullable = true)
    var avatarId: Long,

    @Column(nullable = false)
    var verified: Boolean = false,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnore
    val userDocuments: UserDocuments?,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnore
    val expertDocuments: ExpertDocuments?,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnore
    val tutorDocuments: TutorDocuments?,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnore
    val mentorDocuments: MentorDocuments?,
) : ConvertableToHttpResponse<Users>