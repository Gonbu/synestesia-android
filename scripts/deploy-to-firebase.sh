#!/bin/bash

# Script de déploiement vers Firebase App Distribution
# Usage: ./scripts/deploy-to-firebase.sh [debug|release]

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "app/build.gradle.kts" ]; then
    print_error "Ce script doit être exécuté depuis la racine du projet"
    exit 1
fi

# Déterminer le type de build
BUILD_TYPE=${1:-debug}

if [ "$BUILD_TYPE" != "debug" ] && [ "$BUILD_TYPE" != "release" ]; then
    print_error "Type de build invalide. Utilisez 'debug' ou 'release'"
    exit 1
fi

print_message "Démarrage du déploiement vers Firebase App Distribution..."
print_message "Type de build: $BUILD_TYPE"

# Nettoyer le projet
print_message "Nettoyage du projet..."
./gradlew clean

# Construire l'APK
print_message "Construction de l'APK $BUILD_TYPE..."
if [ "$BUILD_TYPE" = "release" ]; then
    ./gradlew assembleRelease
else
    ./gradlew assembleDebug
fi

# Vérifier que l'APK a été créé (gérer les APKs signés et non signés)
APK_PATH=""
if [ "$BUILD_TYPE" = "release" ]; then
    # Pour release, vérifier d'abord l'APK signé, puis l'APK non signé
    if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
        APK_PATH="app/build/outputs/apk/release/app-release.apk"
    elif [ -f "app/build/outputs/apk/release/app-release-unsigned.apk" ]; then
        APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
        print_warning "APK non signé détecté. Assurez-vous d'avoir configuré la signature pour la production."
    else
        print_error "Aucun APK release trouvé"
        exit 1
    fi
else
    # Pour debug, vérifier l'APK debug
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    else
        print_error "APK debug non trouvé"
        exit 1
    fi
fi

print_message "APK créé avec succès: $APK_PATH"

# Déployer vers Firebase App Distribution
print_message "Déploiement vers Firebase App Distribution..."
if [ "$BUILD_TYPE" = "release" ]; then
    ./gradlew appDistributionUploadRelease
else
    ./gradlew appDistributionUploadDebug
fi

print_message "Déploiement terminé avec succès!"
print_message "Vérifiez Firebase Console pour voir votre build distribué"
