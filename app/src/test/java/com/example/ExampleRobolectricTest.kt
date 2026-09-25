package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.util.SecurityHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Diploma Notes", appName)
  }

  @Test
  fun `verify admin password hashing and verification`() {
    val rawPassword = "admin123"
    val hash = SecurityHelper.hashPassword(rawPassword)

    assertTrue(SecurityHelper.verifyPassword("admin123", hash))
    assertFalse(SecurityHelper.verifyPassword("wrongpass", hash))
  }
}
