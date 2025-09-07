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
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse

@Entity
@Table(name = "user_documents")
data class UserDocuments(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnoreProperties(value = ["password"])
    var user: Users,

    @Column(nullable = false, unique = true)
    var snilsNumber: String,

    @Column(nullable = false, unique = true)
    var snilsId: Long,

    @Column(nullable = false)
    var studyingPlace: String,

    @Column(nullable = false, unique = true)
    var studyingCertificateId: Long,

    @Column(nullable = false)
    @field:Min(1)
    @field:Max(11)
    var learningClass: Short,

    @Column(nullable = false)
    var trainingGround: String,

    @Column(nullable = false, unique = true)
    var additionalStudyingCertificateId: Long,

    @Column(nullable = false)
    var parentRole: String,

    @Column(nullable = false, unique = true)
    var consentToChildPdpId: Long,

    @Column(nullable = false, unique = true)
    var birthCertificateId: Long,
) : ConvertableToHttpResponse<UserDocuments>