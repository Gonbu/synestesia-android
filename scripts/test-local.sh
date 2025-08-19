#!/bin/bash

# Script de test local pour Synestesia
# Usage: ./scripts/test-local.sh [option]

echo "🧪 Tests locaux Synestesia"
echo "=========================="

case "$1" in
    "unit")
        echo "📱 Exécution des tests unitaires..."
        ./gradlew test
        ;;
    "quality")
        echo "🔍 Vérification de la qualité du code..."
        ./gradlew ktlintCheck detekt
        ;;
    "build")
        echo "🏗️ Build des APK..."
        ./gradlew assembleDebug assembleRelease
        ;;
    "all")
        echo "🚀 Exécution de tous les tests et builds..."
        ./gradlew test ktlintCheck detekt assembleDebug assembleRelease
        ;;
    "integration")
        echo "⚠️  Tests d'intégration nécessitent un émulateur Android"
        echo "   Pour exécuter: ./gradlew connectedAndroidTest"
        echo "   Assurez-vous d'avoir un émulateur en cours ou un device connecté"
        ;;
    *)
        echo "Options disponibles:"
        echo "  unit       - Tests unitaires uniquement"
        echo "  quality    - Vérification qualité du code"
        echo "  build      - Build des APK"
        echo "  all        - Tout (tests + builds)"
        echo "  integration- Info sur les tests d'intégration"
        echo ""
        echo "Exemple: ./scripts/test-local.sh unit"
        ;;
esac
