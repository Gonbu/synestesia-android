package com.billie.synestesia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SouvenirFormValidationTest {

    private lateinit var validator: SouvenirFormValidator

    @Before
    fun setUp() {
        validator = SouvenirFormValidator()
    }

    @Test
    fun testValidSouvenirData() {
        val validData =
            SouvenirFormData(
                titre = "Mon Souvenir",
                description = "Une belle journée à Paris",
                couleur = "#FF0000",
                hasPhoto = true,
                hasAudio = true
            )

        val result = validator.validate(validData)
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testEmptyTitle() {
        val invalidData =
            SouvenirFormData(
                titre = "",
                description = "Description valide",
                couleur = "#FF0000",
                hasPhoto = true,
                hasAudio = false
            )

        val result = validator.validate(invalidData)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "titre" })
        assertEquals(
            "Le titre est obligatoire",
            result.errors.first { it.field == "titre" }.message
        )
    }

    @Test
    fun testTitleTooShort() {
        val invalidData =
            SouvenirFormData(
                titre = "Ab",
                description = "Description valide",
                couleur = "#FF0000",
                hasPhoto = true,
                hasAudio = false
            )

        val result = validator.validate(invalidData)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "titre" })
        assertEquals(
            "Le titre doit contenir au moins 3 caractères",
            result.errors.first { it.field == "titre" }.message
        )
    }

    @Test
    fun testTitleTooLong() {
        val longTitle = "A".repeat(101)
        val invalidData =
            SouvenirFormData(
                titre = longTitle,
                description = "Description valide",
                couleur = "#FF0000",
                hasPhoto = true,
                hasAudio = false
            )

        val result = validator.validate(invalidData)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "titre" })
        assertEquals(
            "Le titre ne peut pas dépasser 100 caractères",
            result.errors.first { it.field == "titre" }.message
        )
    }

    @Test
    fun testInvalidColorFormat() {
        val invalidColors = listOf("FF0000", "red", "#GG0000", "#FFF", "invalid")

        invalidColors.forEach { color ->
            val invalidData =
                SouvenirFormData(
                    titre = "Titre valide",
                    description = "Description valide",
                    couleur = color,
                    hasPhoto = true,
                    hasAudio = false
                )

            val result = validator.validate(invalidData)
            assertFalse("Color $color should be invalid", result.isValid)
            assertTrue(result.errors.any { it.field == "couleur" })
        }
    }

    @Test
    fun testValidColorFormats() {
        val validColors = listOf("#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000")

        validColors.forEach { color ->
            val validData =
                SouvenirFormData(
                    titre = "Titre valide",
                    description = "Description valide",
                    couleur = color,
                    hasPhoto = true,
                    hasAudio = false
                )

            val result = validator.validate(validData)
            assertTrue("Color $color should be valid", result.isValid)
        }
    }

    @Test
    fun testRequireAtLeastOneMedia() {
        val invalidData =
            SouvenirFormData(
                titre = "Titre valide",
                description = "Description valide",
                couleur = "#FF0000",
                hasPhoto = false,
                hasAudio = false
            )

        val result = validator.validate(invalidData)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "media" })
        assertEquals(
            "Au moins une photo ou un audio est requis",
            result.errors.first { it.field == "media" }.message
        )
    }

    @Test
    fun testDescriptionLength() {
        // Test description too long
        val longDescription = "A".repeat(501)
        val invalidData =
            SouvenirFormData(
                titre = "Titre valide",
                description = longDescription,
                couleur = "#FF0000",
                hasPhoto = true,
                hasAudio = false
            )

        val result = validator.validate(invalidData)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "description" })
        assertEquals(
            "La description ne peut pas dépasser 500 caractères",
            result.errors.first { it.field == "description" }.message
        )
    }

    @Test
    fun testMultipleValidationErrors() {
        val invalidData =
            SouvenirFormData(
                titre = "",
                description = "",
                couleur = "invalid",
                hasPhoto = false,
                hasAudio = false
            )

        val result = validator.validate(invalidData)
        assertFalse(result.isValid)
        assertEquals(
            3,
            result.errors.size
        ) // titre, couleur, media (description vide n'est pas une erreur)

        // Vérifier que les bonnes erreurs sont présentes
        assertTrue(result.errors.any { it.field == "titre" })
        assertTrue(result.errors.any { it.field == "couleur" })
        assertTrue(result.errors.any { it.field == "media" })
    }

    @Test
    fun testCoordinateValidation() {
        val validData =
            SouvenirFormData(
                titre = "Titre valide",
                description = "Description valide",
                couleur = "#FF0000",
                hasPhoto = true,
                hasAudio = false,
                latitude = 48.8566,
                longitude = 2.3522
            )

        val result = validator.validate(validData)
        assertTrue(result.isValid)

        // Test invalid coordinates
        val invalidData =
            SouvenirFormData(
                titre = "Titre valide",
                description = "Description valide",
                couleur = "#FF0000",
                hasPhoto = true,
                hasAudio = false,
                latitude = 91.0, // Invalid latitude
                longitude = 2.3522
            )

        val invalidResult = validator.validate(invalidData)
        assertFalse(invalidResult.isValid)
        assertTrue(invalidResult.errors.any { it.field == "latitude" })
    }
}

// Data classes for testing
data class SouvenirFormData(
    val titre: String,
    val description: String,
    val couleur: String,
    val hasPhoto: Boolean,
    val hasAudio: Boolean,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class ValidationResult(val isValid: Boolean, val errors: List<ValidationError>)

data class ValidationError(val field: String, val message: String)

// Mock validator class for testing
class SouvenirFormValidator {
    fun validate(data: SouvenirFormData): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate title
        when {
            data.titre.isBlank() -> errors.add(ValidationError("titre", "Le titre est obligatoire"))
            data.titre.length < 3 ->
                errors.add(
                    ValidationError("titre", "Le titre doit contenir au moins 3 caractères")
                )
            data.titre.length > 100 ->
                errors.add(
                    ValidationError("titre", "Le titre ne peut pas dépasser 100 caractères")
                )
        }

        // Validate description length
        if (data.description.length > 500) {
            errors.add(
                ValidationError(
                    "description",
                    "La description ne peut pas dépasser 500 caractères"
                )
            )
        }

        // Validate color format
        if (!data.couleur.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
            errors.add(ValidationError("couleur", "Format de couleur invalide"))
        }

        // Validate media requirement
        if (!data.hasPhoto && !data.hasAudio) {
            errors.add(ValidationError("media", "Au moins une photo ou un audio est requis"))
        }

        // Validate coordinates if provided
        data.latitude?.let { lat ->
            if (lat < -90 || lat > 90) {
                errors.add(ValidationError("latitude", "Latitude invalide"))
            }
        }
        data.longitude?.let { lng ->
            if (lng < -180 || lng > 180) {
                errors.add(ValidationError("longitude", "Longitude invalide"))
            }
        }

        return ValidationResult(errors.isEmpty(), errors)
    }
}
