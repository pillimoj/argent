package argent.api.dto

import io.ktor.util.date.GMTDate
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import java.util.UUID

@Serializer(forClass = GMTDate::class)
object GMTDateSerializer : KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("GMTDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: GMTDate) {
        encoder.encodeLong(value.timestamp)
    }

    override fun deserialize(decoder: Decoder): GMTDate {
        return GMTDate(timestamp = decoder.decodeLong())
    }
}

@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Uuid", PrimitiveKind.STRING)

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