package bundles

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object BundleSerializer : KSerializer<BundleImpl> {
    @Serializable
    data class SerializedBundle(val entries: List<BundleEntry<@Contextual Any>>)

    override val descriptor: SerialDescriptor = SerializedBundle.serializer().descriptor

    override fun serialize(encoder: Encoder, value: BundleImpl) {
        val serialized = SerializedBundle(value.entries.toList())
        val serializer = serializer<SerializedBundle>()
        encoder.encodeSerializableValue(serializer, serialized)
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): BundleImpl {
        val serializer = serializer<SerializedBundle>()
        val serialized = decoder.decodeSerializableValue(serializer)
        return BundleImpl(serialized.entries)
    }
}