package org.careerseekers.userservice.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse

@Entity
@Table(name = "expert_documents")
data class ExpertDocuments(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @JsonIgnoreProperties(value = ["password"])
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: Users,

    @Column(nullable = false)
    var institution: String,

    @Column(nullable = false)
    var post: String,

    @Column(nullable = false, unique = true)
    var consentToExpertPdpId: Long
) : ConvertableToHttpResponse<ExpertDocuments>
