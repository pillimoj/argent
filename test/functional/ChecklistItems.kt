package functional

import ApplicationTest
import TestAuthDefaultUser
import argent.data.users.User
import org.junit.BeforeClass
import kotlin.test.Test

class ChecklistItemsTest : ApplicationTest {
    override val authenticatedUser: User = TestAuthDefaultUser

    @Test
    fun seedDatabase() =
        testApp {
        }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp() {
        }
    }
}
