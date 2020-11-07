package bundles

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

internal object KClassSerializer : KSerializer<KClass<*>> {
    override val descriptor: SerialDescriptor = serializer<String>().descriptor

    override fun serialize(encoder: Encoder, value: KClass<*>) {
        encoder.encodeString(value.jvmName)
    }

    override fun deserialize(decoder: Decoder): KClass<*> {
        val name = decoder.decodeString()
        val classLoader = Thread.currentThread().contextClassLoader
        return classLoader.loadClass(name).kotlin
    }
}