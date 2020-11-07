/**
 *@author Nikolaus Knop
 */

package bundles

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import reaktive.value.ReactiveValue
import reaktive.value.now
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith

object BundleSpec : Spek({
    Feature("Bundle") {
        val prop = publicProperty<Int>("test-property")
        val bundle by memoized { createBundle() }
        Scenario("create empty") {
            When("creating an empty bundle") {}
            Then("it should have no entries") {
                bundle.entries.toList().size shouldEqual 0
            }
            Then("getting the value of some property should thrown an exception") {
                assertFailsWith<NoSuchElementException> { bundle[prop] }
            }
        }
        Scenario("create from entries") {
            lateinit var b: Bundle
            When("creating a bundle from a list of entries") {
                b = createBundle(listOf(BundleEntry(prop, 1)))
            }
            Then("those entries should be present in the created bundle") {
                assert(b.hasProperty(prop))
                b[prop] shouldEqual 1
            }
        }
        Scenario("create configured") {
            lateinit var b: Bundle
            When("creating a bundle supplying a builder lambda") {
                b = createBundle {
                    set(prop, 1)
                }
            }
            Then("all the properties set in the builder block should be present") {
                assert(b.hasProperty(prop))
                b[prop] shouldEqual 1
            }
        }
        Scenario("set/get/delete") {
            When("setting the initial value") {
                bundle[prop] = 1
            }
            Then("it should be present") {
                bundle[prop] shouldEqual 1
            }
            When("changing the value") {
                bundle[prop] = 2
            }
            Then("it should be updated") {
                bundle[prop] shouldEqual 2
            }
            When("deleting the value of the property") {
                bundle.delete(prop)
            }
            with(ExceptionCollector()) {
                When("then querying the value") {
                    execute { bundle[prop] }
                }
                Then("it should throw an exception") {
                    expect<NoSuchElementException>()
                }
            }
        }
        @Suppress("UNCHECKED_CAST")
        Scenario("illegal get") {
            testFailsWith<ClassCastException>("getting the value of a wrongly cast property") {
                bundle[prop] = 1
                println(bundle[prop as PublicProperty<Boolean>])
            }
        }
        @Suppress("UNCHECKED_CAST")
        Scenario("illegal set") {
            testFailsWith<IllegalStateException>("setting a property to a value of the wrong type") {
                bundle[prop as PublicProperty<Boolean>] = true
            }
        }
        @Suppress("UNCHECKED_CAST")
        Scenario("illegal permission") {
            testFailsWith<IllegalStateException>("using an invalid permission") {
                val readonly = readonlyProperty<Int>("p2")
                bundle[readonly as PublicProperty<Int>] = 1
            }
        }
        Scenario("duplicate property with different type") {
            testFailsWith<IllegalStateException>("introducing a property with a used name and another type") {
                publicProperty<Boolean>(prop.name)
            }

        }
        Scenario("duplicate property with different permission") {
            testFailsWith<IllegalStateException>("introducing a property with a used name and another permission") {
                readonlyProperty<Int>(prop.name)
            }

        }
        Scenario("reactive properties") {
            val p = publicProperty("reactive-property", default = 1)
            testFailsWith<NoSuchElementException>("getting a reactive value without a default value") {
                bundle.getReactive(prop)
            }
            lateinit var v: ReactiveValue<Int>
            When("getting a reactive value") {
                v = bundle.getReactive(p)
            }
            Then("it should have the default value initially") {
                v.now shouldEqual 1
            }
            When("setting the value") {
                bundle[p] = 2
            }
            Then("the reactive value should be updated") {
                v.now shouldEqual 2
            }
            When("deleting the property") {
                bundle.delete(p)
            }
            Then("the reactive value should be set to the default value") {
                v.now shouldEqual 1
            }
        }
        Scenario("serialization") {
            @Serializable
            data class Person(val name: String, val age: Int)

            lateinit var text: String
            lateinit var deserialized: Bundle
            val p by memoized { publicProperty<Person>("person") }
            val json = Json { serializersModule = bundlesSerializersModule }
            When("serializing and then deserializing a bundle") {
                runtimeTypeSafety = false
                bundle[p] = Person("Nikolaus", 17)
                text = json.encodeToString(bundle)
                println(text)
                deserialized = json.decodeFromString(text)
            }
            Then("it should still have the same properties") {
                deserialized[p] shouldEqual Person("Nikolaus", 17)
            }
        }
    }
})