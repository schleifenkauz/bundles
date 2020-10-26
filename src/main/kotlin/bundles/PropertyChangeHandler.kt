/**
 *@author Nikolaus Knop
 */

package bundles

/**
 * A property change handler can be used to dynamically (yet with type-safety) register listeners
 * dependent on a context of type [Ctx] for the properties.
 */
class PropertyChangeHandler<Ctx : Any> {
    private val listeners = mutableMapOf<Property<*, *, *>, MutableSet<(Ctx, Any?) -> Unit>>()

    /**
     * Register a [handler] for the case that the given [property] changes.
    */
    fun <T> handle(property: Property<T, *, *>, handler: (Ctx, new: T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        handler as (Ctx, Any?) -> Unit
        val set = listeners.getOrPut(property) { mutableSetOf() }
        set.add(handler)
    }

    internal fun valueChanged(context: Ctx, property: Property<*, *, *>, new: Any?) {
        val set = listeners[property] ?: return
        set.forEach { listener -> listener.invoke(context, new) }
    }
}