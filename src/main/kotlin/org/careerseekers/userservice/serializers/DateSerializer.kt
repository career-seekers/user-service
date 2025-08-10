package org.careerseekers.userservice.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date

object DateSerializer : KSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        val string = dateFormat.format(value)
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Date {
        val string = decoder.decodeString()
        return dateFormat.parse(string) ?: throw SerializationException("Invalid date format: $string")
    }
}