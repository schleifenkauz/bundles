/**
 * @author Nikolaus Knop
 */

package bundles

import reaktive.event.EventStream
import reaktive.value.ReactiveValue

/**
 * A bundle is a collection of [Property]s associated with values.
 */
interface Bundle {
    /**
     * This stream emits events when the value of property changes
     */
    val changed: EventStream<BundleChange<*>>

    /**
     * Return an [Iterable] of all the (property, value)-pairs in this [Bundle].
     */
    val entries: Sequence<BundleEntry<*>>

    /**
     * Return `true` if the given [property] is configured in this [Bundle].
     */
    fun hasProperty(property: Property<*, *>): Boolean

    /**
     * Get the value of the specified [property] in this [Bundle], or the [Property.default] value, if not present.
     * @throws NoSuchElementException if the specified [property] was not set before and no default value is provided.
     */
    operator fun <T : Any> get(property: Property<out T, *>): T

    /**
     * Return a [ReactiveValue] always holding the value of the specified [property] in this [Bundle],
     * or the [Property.default] value, if not present.
     * @throws NoSuchElementException if the specified [property] was not set before and no default value is provided.
     */
    fun <T : Any> getReactive(property: Property<out T, *>): ReactiveValue<T>

    /**
     * Set the value of the specified [property] to the constant [value] in this [Bundle].
     * Calls of `get(permission, property)` will return this [value] until a new value is set
     * You need to pass a [permission].
     */
    operator fun <P : Permission, T : Any> set(permission: P, property: Property<in T, P>, value: T)

    /**
     * Deletes the value of the given [property] from this bundle.
     * You need to pass a [permission].
     * @throws NoSuchElementException if the given [property] is not associated with any value
     */
    fun <P : Permission> delete(permission: P, property: Property<*, P>)
}