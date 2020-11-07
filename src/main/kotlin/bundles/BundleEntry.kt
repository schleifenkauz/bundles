/**
 *@author Nikolaus Knop
 */

package bundles

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlin.reflect.KClass

/**
 * An entry in a [Bundle].
 * @property property the key of the entry
 * @property value the value of the entry
 */
@Serializable(with = BundleEntry.Serializer::class)
data class BundleEntry<out T : Any>(val property: Property<out T, *>, val value: T) {
    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    internal object Serializer : KSerializer<BundleEntry<*>> {
        private val contextual = ContextualSerializer(Any::class)
        private val propertySerializer = PolymorphicSerializer(Property::class)

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BundleEntry") {
            element("property", propertySerializer.descriptor)
            element<String>("valueClass", isOptional = true)
            element("value", contextual.descriptor)
        }

        override fun serialize(encoder: Encoder, value: BundleEntry<*>) =
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, propertySerializer, value.property)
                val valueSerializer = if (value.property is TypeSafeProperty) {
                    value.property.propertyType.serializer() as KSerializer<Any>
                } else {
                    encodeStringElement(descriptor, 1, value.value.javaClass.name)
                    value.value::class.serializer() as KSerializer<Any>
                }
                encodeSerializableElement(descriptor, 2, valueSerializer, value.value)
            }

        override fun deserialize(decoder: Decoder): BundleEntry<*> =
            decoder.decodeStructure(descriptor) {
                lateinit var property: Property<*, *>
                lateinit var valueClass: KClass<*>
                lateinit var value: Any
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> {
                            property = decodeSerializableElement(descriptor, 0, propertySerializer)
                            if (property is TypeSafeProperty) valueClass = property.propertyType
                        }
                        1 -> {
                            val valueClassName = decodeStringElement(descriptor, 1)
                            valueClass = Thread.currentThread().contextClassLoader.loadClass(valueClassName).kotlin
                        }
                        2 -> {
                            val ser = valueClass.serializer()
                            value = decodeSerializableElement(descriptor, 2, ser)
                        }
                        CompositeDecoder.DECODE_DONE -> break
                        else                         -> error("Unexpected index: $index")
                    }
                }
                BundleEntry(property, value)
            }
    }
}
