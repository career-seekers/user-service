package org.careerseekers.userservice.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse
import java.util.Date

@Entity
@Table(name = "children")
data class Children(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var patronymic: String,

    @Column(nullable = false)
    var dateOfBirth: Date,

    @Column(nullable = true)
    var createdAt: Date?,

    @OneToOne(mappedBy = "child", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JsonIgnoreProperties("child")
    var childDocuments: ChildDocuments? = null,

    @JsonIgnoreProperties(value = ["password", "childDocuments", "expertDocuments", "tutorDocuments", "mentorDocuments", "menteeChildren", "children"])
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    var user: Users,

    @JsonIgnoreProperties(value = ["password", "childDocuments", "expertDocuments", "tutorDocuments", "mentorDocuments", "menteeChildren", "children"])
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id")
    var mentor: Users? = null,
) : ConvertableToHttpResponse<Children>