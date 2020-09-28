@file:Suppress("EXPERIMENTAL_API_USAGE")

package argent.api.dto

import io.ktor.util.date.GMTDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

@Serializer(forClass = GMTDate::class)
object GMTDateSerializer : KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GMTDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: GMTDate) {
        encoder.encodeLong(value.timestamp)
    }

    override fun deserialize(decoder: Decoder): GMTDate {
        return GMTDate(timestamp = decoder.decodeLong())
    }
}

@ExperimentalSerializationApi
@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        val stringValue = decoder.decodeString()
        try {
            return UUID.fromString(stringValue)
        } catch (e: IllegalArgumentException) {
            throw SerializationException("Invalid UUID: $stringValue")
        }
    }
}