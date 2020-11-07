/**
 *@author Nikolaus Knop
 */

package bundles

/**
 * Indicates the change of [property] from [oldValue] to [newValue].
 * @property bundle the [Bundle] which changed
 * @property property the property that changed
 * @property oldValue the old value of the property or `null` if this is the initial value of the property
 * @property newValue the new value of the property or `null` if the property was deleted and there is no default value
 */
data class BundleChange<T : Any>(
    val bundle: Bundle,
    val property: Property<in T, *>,
    val oldValue: T?,
    val newValue: T?
)