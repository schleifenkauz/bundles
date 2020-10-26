/**
 * @author Nikolaus Knop
 */

package bundles

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Create a new empty bundle.
*/
fun createBundle(): Bundle = BundleImpl()

/**
 * Create a new bundle applying the [configure] block to it.
*/
inline fun createBundle(configure: Bundle.() -> Unit) = createBundle().apply(configure)

/**
 * Return a [ReadWriteProperty] delegating to the given [prop] of the [bundle].
*/
fun <T, Read : Any, Write : Any> property(
    bundle: Bundle,
    read: Read,
    write: Write,
    prop: Property<T, Read, Write>
): ReadWriteProperty<Any?, T> = object : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = bundle[read, prop]

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        bundle[write, prop] = value
    }
}

/**
 * Return a [ReadOnlyProperty] delegating to the given [prop] of the [bundle].
*/
fun <T, Read : Any, Write : Any> readonlyProperty(
    bundle: Bundle,
    read: Read,
    prop: Property<T, Read, Write>
): ReadOnlyProperty<Any?, T> = object : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = bundle[read, prop]
}

/**
 * Return a [ReadWriteProperty] delegating to the given [prop] of the [bundle].
 */
fun <T> property(bundle: Bundle, prop: Property<T, Any, Any>): ReadWriteProperty<Any?, T> =
    property(bundle, Any(), Any(), prop)

/**
 * Return a [ReadOnlyProperty] delegating to the given [prop] of the [bundle].
 */
fun <T> readonlyProperty(bundle: Bundle, prop: Property<T, Any, *>): ReadOnlyProperty<Any?, T> =
    readonlyProperty(bundle, Any(), prop)


