/**
 * @author Nikolaus Knop
 */

package bundles

/**
 * A [Property] of type [T] needing the given [Read] and [Write] permissions to be read or written.
 * @property name the display name of property
 * @property default an optional default value
 */
open class Property<T, in Read : Any, in Write : Any>(val name: String) {
    /**
     * Return the default value of this property or throw a [NoSuchElementException] if there is no default value.
     */
    open val default: T get() = throw NoSuchElementException("No default value for property $this")

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Property<*, *, *>

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        /**
         * Return a new [Property] with the given name and the specified [default] value.
         */
        fun <T, Read : Any, Write : Any> withDefault(name: String, default: T) =
            object : Property<T, Read, Write>(name) {
                override val default: T = default
            }
    }
}

open class ReactiveProperty<T, Read : Any, Write : Any>(name: String) : Property<T, Read, Write>(name) {
    companion object {
        /**
         * Return a new [ReactiveProperty] with the given name and the specified [default] value.
         */
        fun <T, Read : Any, Write : Any> withDefault(name: String, default: T) =
            object : ReactiveProperty<T, Read, Write>(name) {
                override val default: T = default
            }
    }
}

open class SimpleProperty<T>(name: String) : Property<T, Any, Any>(name) {
    companion object {
        /**
         * Return a new [SimpleProperty] with the given name and the specified [default] value.
         */
        fun <T> withDefault(name: String, default: T) = object : SimpleProperty<T>(name) {
            override val default: T = default
        }
    }
}

open class SimpleReactiveProperty<T>(name: String) :
    ReactiveProperty<T, Any, Any>(name) {

    companion object {
        /**
         * Return a new [SimpleReactiveProperty] with the given name and the specified [default] value.
         */
        fun <T> withDefault(name: String, default: T) = object : SimpleReactiveProperty<T>(name) {
            override val default: T = default
        }
    }
}