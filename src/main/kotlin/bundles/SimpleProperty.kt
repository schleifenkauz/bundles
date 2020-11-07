/**
 *@author Nikolaus Knop
 */

package bundles

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@PublishedApi internal class SimpleProperty<T : Any, in P : Permission>(
    override val name: String,
    @Transient override val default: T? = null
) : AbstractProperty<T, P>() {
    override fun toString(): String = "<property: $name>"

    override fun checkWriteAccess(permission: P, value: T?) {}
}