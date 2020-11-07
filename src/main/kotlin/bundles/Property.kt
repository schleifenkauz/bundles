/**
 * @author Nikolaus Knop
 */

package bundles

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

    /**
     * Non public API
     */
    fun checkWriteAccess(permission: P, value: T?)
}