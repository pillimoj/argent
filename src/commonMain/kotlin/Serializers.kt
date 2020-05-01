import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import io.ktor.util.date.GMTDate
import kotlinx.serialization.*

@Serializer(forClass = GMTDate::class)
object GMTDateSerializer: KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("GMTDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: GMTDate) {
        encoder.encodeLong(value.timestamp)
    }

    override fun deserialize(decoder: Decoder): GMTDate {
        return GMTDate(timestamp = decoder.decodeLong())
    }
}

@Serializer(forClass = Uuid::class)
object UUIDSerializer: KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Uuid", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uuid {
        return uuidFrom(decoder.decodeString())
    }
}