/**
 *@author Nikolaus Knop
 */

package bundles

import reaktive.Observer
import reaktive.event.*
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

/**
 * A property change handler can be used to observe the values of properties in a given context.
 */
class PropertyChangeHandler {
    private val events = mutableMapOf<KClass<*>, MutableMap<Property<*, *>, BiEvent<Any, Any>>>()

    /**
     * Register a [handler] for the case that the given [property] changes.
     */
    fun <Ctx : Any, T : Any> observe(
        ctx: KClass<out Ctx>,
        property: Property<T, *>,
        handler: (Ctx, new: T) -> Unit
    ): Observer {
        val ev = events.getOrPut(ctx) { mutableMapOf() }.getOrPut(property) { biEvent() }
        @Suppress("UNCHECKED_CAST")
        return ev.stream.observe { context: Any, value: Any ->
            handler(context as Ctx, value as T)
        }
    }

    /**
     * Let this [PropertyChangeHandler] notify all the handlers associated with the given [context] and the [property].
     */
    fun <Ctx : Any, T : Any> valueChanged(context: Ctx, property: Property<in T, *>, new: T) {
        val cls = context::class
        for (c in cls.allSuperclasses + cls) {
            val forCtx = events[c] ?: continue
            val ev = forCtx[property] ?: continue
            ev.fire(context, new)
        }
    }
}