package org.careerseekers.userservice.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse

@Entity
@Table(name = "expert_documents")
data class ExpertDocuments(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @JsonIgnoreProperties(value = ["password", "userDocuments", "expertDocuments", "tutorDocuments", "mentorDocuments"])
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: Users,

    @Column(nullable = false)
    var institution: String,

    @Column(nullable = false)
    var post: String,

    @Column(nullable = false)
    var consentToExpertPdp: Boolean = true,
) : ConvertableToHttpResponse<ExpertDocuments>
