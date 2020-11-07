/**
 * @author Nikolaus Knop
 */

package bundles

import org.spekframework.spek2.style.gherkin.ScenarioBody
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.fail

infix fun <T> T.shouldEqual(expected: T) {
    assertEquals(expected, this)
}

class ExceptionCollector {
    private var exception: Throwable? = null

    fun execute(action: () -> Unit) {
        try {
            action()
        } catch (ex: Throwable) {
            exception = ex
        }
    }

    fun expect(ex: KClass<out Throwable>) {
        when {
            exception == null         -> fail("block did not throw an exception")
            !ex.isInstance(exception) -> fail("Expected exception of $ex but got $exception")
        }
    }

    inline fun <reified E : Throwable> expect() {
        expect(E::class)
    }
}

inline fun <reified E : Throwable> ScenarioBody.testFailsWith(description: String, crossinline action: () -> Unit) {
    var exception: Throwable? = null
    When(description) {
        try {
            action()
        } catch (ex: Throwable) {
            exception = ex
        }
    }
    Then("it should throw an exception") {
        if (exception !is E) fail("Expected exception of ${E::class} but got $exception")
    }
}