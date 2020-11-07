/**
 *@author Nikolaus Knop
 */

package bundles

import reaktive.event.EventStream
import reaktive.event.event
import reaktive.impl.WeakReactive
import reaktive.value.*

@Suppress("UNCHECKED_CAST")
@PublishedApi internal class BundleImpl() : Bundle {
    constructor(entries: Iterable<BundleEntry<*>>) : this() {
        for ((p, v) in entries) properties[p] = v
    }

    private val change = event<BundleChange<*>>()
    override val changed: EventStream<BundleChange<*>>
        get() = change.stream
    private val properties = mutableMapOf<Property<*, *>, Any>()
    private val managed = mutableMapOf<Property<*, *>, WeakReactive<ReactiveVariable<Any>>>()

    override val entries: Sequence<BundleEntry<*>>
        get() = properties.entries.asSequence().map { (p, v) -> BundleEntry(p, v) }

    override fun hasProperty(property: Property<*, *>): Boolean = property in properties

    override fun <T : Any> get(property: Property<out T, *>): T {
        val value = properties[property] as T?
        return value ?: property.default ?: throw NoSuchElementException("No value for $property")
    }

    override fun <T : Any> getReactive(property: Property<out T, *>): ReactiveValue<T> {
        if (property.default == null) throw NoSuchElementException("$property has no default value")
        val value = get(property)
        val reactive = managed[property]?.reactive
        return if (reactive == null) {
            val new = reactiveVariable(value)
            managed[property] = new.weak as WeakReactive<ReactiveVariable<Any>>
            new
        } else reactive as ReactiveValue<T>
    }

    override fun <P : Permission, T : Any> set(permission: P, property: Property<in T, P>, value: T) {
        property.checkWriteAccess(permission, value)
        val old = properties[property]
        if (value == old) return
        properties[property] = value
        change.fire(BundleChange(this, property, old as T?, value))
        if (property in managed) {
            managed[property]?.reactive?.set(value)
        }
    }

    override fun <P : Permission> delete(permission: P, property: Property<*, P>) {
        property.checkWriteAccess(permission, null)
        val old = properties.remove(property) ?: throw NoSuchElementException("Cannot delete $property")
        change.fire(BundleChange(this, property as Property<Any, *>, old, property.default))
        if (property in managed) {
            val v = managed[property]?.reactive
            if (v != null) {
                val default = property.default ?: throw NoSuchElementException("No value for $property")
                v.set(default)
            }
        }
    }

    inner class Builder : BundleBuilder {
        override fun <T : Any> set(property: Property<T, *>, value: T) {
            properties[property] = value
        }
    }
}