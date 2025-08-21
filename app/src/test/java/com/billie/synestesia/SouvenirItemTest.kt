package com.billie.synestesia

import com.billie.synestesia.models.SouvenirItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SouvenirItemTest {
    private lateinit var souvenir: SouvenirItem

    @Before
    fun setUp() {
        souvenir =
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
    }

    @Test
    fun testSouvenirItemCreationWithValidData() {
        assertNotNull(souvenir)
        assertEquals("test-id", souvenir.id)
        assertEquals("Test Souvenir", souvenir.titre)
        assertEquals("Test Description", souvenir.description)
        assertEquals(48.8566, souvenir.latitude!!, 0.001)
        assertEquals(2.3522, souvenir.longitude!!, 0.001)
        assertEquals(1234567890L, souvenir.date)
        assertEquals("#FF0000", souvenir.couleur)
        assertEquals("test-audio.mp3", souvenir.audio)
        assertEquals("test-photo.jpg", souvenir.photo)
        assertEquals("user123", souvenir.userId)
    }

    @Test
    fun testSouvenirItemWithEmptyStrings() {
        val emptySouvenir =
            SouvenirItem(
                id = "",
                titre = "",
                description = "",
                latitude = 0.0,
                longitude = 0.0,
                date = 0L,
                couleur = "",
                photo = "",
                audio = "",
                userId = ""
            )

        assertEquals("", emptySouvenir.id)
        assertEquals("", emptySouvenir.titre)
        assertEquals("", emptySouvenir.description)
        assertEquals("", emptySouvenir.couleur)
        assertEquals("", emptySouvenir.photo)
        assertEquals("", emptySouvenir.audio)
        assertEquals("", emptySouvenir.userId)
    }

    @Test
    fun testSouvenirItemWithNullValues() {
        val nullSouvenir =
            SouvenirItem(
                id = null,
                titre = "",
                description = "",
                latitude = null,
                longitude = null,
                date = 0L,
                couleur = "",
                photo = "",
                audio = "",
                userId = ""
            )

        assertNull(nullSouvenir.id)
        assertEquals("", nullSouvenir.titre)
        assertEquals("", nullSouvenir.description)
        assertEquals("", nullSouvenir.couleur)
        assertEquals("", nullSouvenir.photo)
        assertEquals("", nullSouvenir.audio)
        assertEquals("", nullSouvenir.userId)
        assertNull(nullSouvenir.latitude)
        assertNull(nullSouvenir.longitude)
    }

    @Test
    fun testSouvenirItemCoordinateEdgeCases() {
        // Test limites de latitude (-90 à 90)
        val maxLatSouvenir =
            SouvenirItem(
                id = "max-lat",
                titre = "Max Latitude",
                description = "Test",
                latitude = 90.0,
                longitude = 0.0,
                date = 0L,
                couleur = "#000000",
                photo = "",
                audio = "",
                userId = ""
            )
        assertEquals(90.0, maxLatSouvenir.latitude!!, 0.001)

        // Test limites de longitude (-180 à 180)
        val maxLonSouvenir =
            SouvenirItem(
                id = "max-lon",
                titre = "Max Longitude",
                description = "Test",
                latitude = 0.0,
                longitude = 180.0,
                date = 0L,
                couleur = "#000000",
                photo = "",
                audio = "",
                userId = ""
            )
        assertEquals(180.0, maxLonSouvenir.longitude!!, 0.001)
    }

    @Test
    fun testSouvenirItemColorValidation() {
        // Test couleurs valides
        val validColors = listOf("#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000")
        validColors.forEach { color ->
            val colorSouvenir =
                SouvenirItem(
                    id = "color-test",
                    titre = "Color Test",
                    description = "Test",
                    latitude = 0.0,
                    longitude = 0.0,
                    date = 0L,
                    couleur = color,
                    photo = "",
                    audio = "",
                    userId = ""
                )
            assertEquals(color, colorSouvenir.couleur)
        }
    }

    @Test
    fun testSouvenirItemDateHandling() {
        val now = System.currentTimeMillis()
        val souvenir =
            SouvenirItem(
                id = "date-test",
                titre = "Date Test",
                description = "Test",
                latitude = 0.0,
                longitude = 0.0,
                date = now,
                couleur = "#000000",
                photo = "",
                audio = "",
                userId = ""
            )

        assertEquals(now, souvenir.date)
        assertTrue(souvenir.date > 0)
    }

    @Test
    fun testSouvenirItemToLatLngConversion() {
        val souvenirWithCoords = SouvenirItem(latitude = 48.8566, longitude = 2.3522)
        val souvenirWithoutCoords = SouvenirItem()

        val latLng = souvenirWithCoords.toLatLng()
        assertNotNull(latLng)
        assertEquals(48.8566, latLng!!.latitude, 0.001)
        assertEquals(2.3522, latLng.longitude, 0.001)

        val nullLatLng = souvenirWithoutCoords.toLatLng()
        assertNull(nullLatLng)
    }
}
