/**
 * @author Nikolaus Knop
 */

@file:Suppress("UNCHECKED_CAST")
@file:UseSerializers(KClassSerializer::class)

package bundles

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Serializable
@PublishedApi internal class TypeSafeProperty<T : Any, in P : Permission>(
    override val name: String,
    override val propertyType: KType,
    private val permissionClass: KClass<in P>,
    @Transient override val default: T? = null,
) : AbstractProperty<T, P>() {
    init {
        checkNameAvailable(this)
    }

    override fun toString(): String = "<property: $name, type: $propertyType, permission: $permissionClass>"

    override fun checkWriteAccess(permission: P, value: T?) {
        check(permissionClass.isInstance(permission)) { "$permission is not an instance of $permissionClass" }
        if (value != null) {
            val klass = propertyType.classifier as KClass<*>
            check(klass.isInstance(value)) { "$value is not an instance of $propertyType" }
        }
    }

    companion object {
        private val properties = mutableMapOf<String, TypeSafeProperty<*, *>>()

        private fun checkNameAvailable(property: TypeSafeProperty<*, *>) {
            val present = properties[property.name]
            if (present == null) {
                properties[property.name] = property
            } else if (property.propertyType != present.propertyType || property.permissionClass != present.permissionClass) {
                error("Two properties with same name but conflicting signatures: $property != $present")
            }
        }
    }
}