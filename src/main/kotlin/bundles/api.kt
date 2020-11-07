/**
 * @author Nikolaus Knop
 */

package bundles

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.*
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Public permission that is granted to all modules.
 */
object Public : Permission("public")

/**
 * Readonly permission that is granted to no module.
 */
sealed class Readonly : Permission("readonly")

/**
 * Property that can be read and written by all modules.
 */
typealias PublicProperty<T> = Property<T, Public>

/**
 * Property that cannot be written by any module.
 */
typealias ReadonlyProperty<T> = Property<T, Readonly>

inline fun <reified T : Any, reified P : Permission> property(name: String, default: T? = null): Property<T, P> =
    if (runtimeTypeSafety) TypeSafeProperty(name, T::class, P::class, default) else SimpleProperty(name, default)

inline fun <reified T : Any> publicProperty(name: String, default: T? = null): PublicProperty<T> =
    property(name, default)

inline fun <reified T : Any> readonlyProperty(name: String, default: T? = null): ReadonlyProperty<T> =
    property(name, default)

/**
 * Create a new empty bundle.
 */
fun createBundle(): Bundle = BundleImpl()

/**
 * Create a new [Bundle] from the given bundle [entries].
 */
fun createBundle(entries: List<BundleEntry<*>>): Bundle = BundleImpl(entries)

/**
 * Create a new bundle using the given bundle builder [block].
 */
inline fun createBundle(block: BundleBuilder.() -> Unit): Bundle {
    val bundle = BundleImpl()
    val builder = bundle.Builder()
    builder.block()
    return bundle
}

/**
 * Return a [ReadWriteProperty] delegating to the given property of this [Bundle].
 */
fun <T : Any, P : Permission> Bundle.property(permission: P, prop: Property<T, P>): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = get(prop)

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            set(permission, prop, value)
        }
    }

/**
 * Return a [ReadOnlyProperty] delegating to the given [prop] of this [Bundle].
 */
fun <T : Any> Bundle.readonlyProperty(prop: Property<out T, *>): ReadOnlyProperty<Any?, T> =
    ReadOnlyProperty { _, _ -> get(prop) }

/**
 * Return a [ReadWriteProperty] delegating to the given [prop] of this [Bundle].
 */
fun <T : Any> Bundle.property(prop: PublicProperty<T>): ReadWriteProperty<Any?, T> = property(Public, prop)


/**
 * Syntactic sugar for [Bundle.set] ([Public], [property], [value]).
 */
operator fun <T : Any> Bundle.set(property: PublicProperty<T>, value: T) {
    set(Public, property, value)
}

/**
 * Syntactic sugar for [Bundle.delete] ([Public], [property]).
 */
fun <T : Any> Bundle.delete(property: PublicProperty<T>) {
    delete(Public, property)
}

/**
 * [SerializersModule] that defines a serializer for [Bundle].
 */
@OptIn(ExperimentalSerializationApi::class)
val bundlesSerializersModule = SerializersModule {
    contextual(KClassSerializer)
    polymorphic(Bundle::class, BundleImpl::class, BundleSerializer)
    val contextual = ContextualSerializer(Nothing::class)
    polymorphic(Property::class) {
        subclass(SimpleProperty.serializer(contextual, contextual))
        subclass(TypeSafeProperty.serializer(contextual, contextual))
    }
}

var runtimeTypeSafety = true