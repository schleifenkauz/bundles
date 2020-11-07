/**
 * @author Nikolaus Knop
 */

package bundles

import kotlinx.serialization.Serializable

/**
 * Base class for all permissions.
 * @param name the name of this permission.
 */
@Serializable
abstract class Permission(private val name: String) {
    override fun toString(): String = "<permission: $name>"
}