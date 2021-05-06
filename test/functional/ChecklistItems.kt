package functional

import ApplicationTest
import TestAuthDefaultUser
import argent.data.users.User
import org.junit.BeforeClass
import org.junit.jupiter.api.Test

class ChecklistItemsTest : ApplicationTest {
    override val authenticatedUser: User = TestAuthDefaultUser

    @BeforeClass
    fun setUp() {
    }

    @Test
    fun seedDatabase() = testApp {
    }
}
