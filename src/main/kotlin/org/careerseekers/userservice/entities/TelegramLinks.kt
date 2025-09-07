package org.careerseekers.userservice.entities

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.validation.constraints.NotBlank
import org.careerseekers.userservice.io.converters.ConvertableToHttpResponse

@Entity
data class TelegramLinks(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false, unique = true)
    @field:JsonProperty("tgLink")
    @field:NotBlank
    var tgLink: String?,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: Users,
) : ConvertableToHttpResponse<TelegramLinks>
