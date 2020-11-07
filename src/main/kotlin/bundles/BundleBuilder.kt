/**
 *@author Nikolaus Knop
 */

package bundles

/**
 * A [BundleBuilder] is used to create a new [Bundle] with some properties preconfigured.
 * While building a new bundle permissions don't have to be provided.
 */
interface BundleBuilder {
    /**
     * Set the given [property] to the specified [value].
     */
    operator fun <T : Any> set(property: Property<T, *>, value: T)
}