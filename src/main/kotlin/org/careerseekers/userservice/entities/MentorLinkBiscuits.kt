package org.careerseekers.userservice.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse

@Entity
@Table(name = "mentor_links")
data class MentorLinkBiscuits(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false, unique = true)
    var biscuit: String,

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: Users,
) : ConvertableToHttpResponse<MentorLinkBiscuits>
