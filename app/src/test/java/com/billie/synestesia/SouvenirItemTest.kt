package com.billie.synestesia

import com.billie.synestesia.models.SouvenirItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SouvenirItemTest {

    @Test
    fun `test SouvenirItem creation with valid data`() {
        val souvenir =
            SouvenirItem(
                id = "test-id",
                titre = "Test Souvenir",
                description = "Test Description",
                latitude = 48.8566,
                longitude = 2.3522,
                date = 1234567890L,
                couleur = "#FF0000",
                photo = "test-photo.jpg",
                audio = "test-audio.mp3",
                userId = "user123"
            )

        assertEquals("test-id", souvenir.id)
        assertEquals("Test Souvenir", souvenir.titre)
        assertEquals("Test Description", souvenir.description)
        assertEquals(48.8566, souvenir.latitude!!, 0.0001)
        assertEquals(2.3522, souvenir.longitude!!, 0.0001)
        assertEquals(1234567890L, souvenir.date)
        assertEquals("#FF0000", souvenir.couleur)
        assertEquals("test-photo.jpg", souvenir.photo)
        assertEquals("test-audio.mp3", souvenir.audio)
        assertEquals("user123", souvenir.userId)
    }

    @Test
    fun `test SouvenirItem with default values`() {
        val souvenir = SouvenirItem()

        assertNull(souvenir.id)
        assertEquals("", souvenir.titre)
        assertEquals("", souvenir.description)
        assertNull(souvenir.latitude)
        assertNull(souvenir.longitude)
        assertEquals(0L, souvenir.date)
        assertEquals("", souvenir.couleur)
        assertEquals("", souvenir.photo)
        assertEquals("", souvenir.audio)
        assertEquals("", souvenir.userId)
    }

    @Test
    fun `test SouvenirItem toLatLng conversion`() {
        val souvenirWithCoords = SouvenirItem(latitude = 48.8566, longitude = 2.3522)

        val souvenirWithoutCoords = SouvenirItem()

        val latLng = souvenirWithCoords.toLatLng()
        assertNotNull(latLng)
        assertEquals(48.8566, latLng!!.latitude, 0.0001)
        assertEquals(2.3522, latLng.longitude, 0.0001)

        val nullLatLng = souvenirWithoutCoords.toLatLng()
        assertNull(nullLatLng)
    }
}
