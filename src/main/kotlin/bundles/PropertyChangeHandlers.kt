/**
 *@author Nikolaus Knop
 */

@file:Suppress("UNCHECKED_CAST")

package bundles

import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

/**
 * An aggregate of [PropertyChangeHandler]s.
 */
class PropertyChangeHandlers {
    private val handlers = mutableMapOf<KClass<*>, PropertyChangeHandler<*>>()

    /**
     * Let the [PropertyChangeHandler]s compatible with the given [Ctx] class handle the given [value].
     */
    fun <Ctx : Any> handle(ctx: Ctx, property: Property<*, *, *>, value: Any?) {
        val cls = ctx::class
        for (superclass in sequenceOf(cls) + cls.allSuperclasses.asSequence()) {
            val h = handlers[superclass] as PropertyChangeHandler<Ctx>?
            h?.valueChanged(ctx, property, value)
        }
    }

    /**
     * Return the [PropertyChangeHandler] associated with the given class.
     */
    fun <Ctx : Any> forContext(cls: KClass<Ctx>): PropertyChangeHandler<Ctx> =
        handlers.getOrPut(cls) { PropertyChangeHandler<Ctx>() } as PropertyChangeHandler<Ctx>

    /**
     * Syntactic sugar for ``forContext(Ctx::class)``.
     */
    inline fun <reified Ctx : Any> forContext(): PropertyChangeHandler<Ctx> = forContext(Ctx::class)
}