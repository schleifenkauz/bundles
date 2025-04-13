/**
 * @author Nikolaus Knop
 */

package bundles

import kotlin.reflect.KType

/**
 * A [Property] of type [T] needing the [P] permission to be written.
 */
interface Property<T : Any, in P : Permission> {
    /**
     * The display name of property
     */
    val name: String

    /**
     * The default value of this property.
     */
    val default: T?

    val propertyType: KType?

    /**
     * Non public API
     */
    fun checkWriteAccess(permission: P, value: T?)
}