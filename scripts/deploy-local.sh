#!/bin/bash

# Script de déploiement local vers Firebase App Distribution
# Usage: ./scripts/deploy-local.sh [debug|release] [--dry-run] [--skip-build] [--notify]

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables par défaut
BUILD_TYPE="debug"
DRY_RUN=false
SKIP_BUILD=false
NOTIFY=false

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

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE} $1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Fonction d'aide
show_help() {
    echo "Usage: $0 [debug|release] [OPTIONS]"
    echo ""
    echo "OPTIONS:"
    echo "  --dry-run      Simuler le déploiement sans uploader"
    echo "  --skip-build   Utiliser un APK existant"
    echo "  --notify       Envoyer une notification après déploiement"
    echo "  --help         Afficher cette aide"
    echo ""
    echo "EXEMPLES:"
    echo "  $0 debug                    # Déployer une version debug"
    echo "  $0 release --dry-run        # Simuler un déploiement release"
    echo "  $0 debug --skip-build       # Déployer un APK debug existant"
}

# Parser les arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        debug|release)
            BUILD_TYPE="$1"
            shift
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        --skip-build)
            SKIP_BUILD=true
            shift
            ;;
        --notify)
            NOTIFY=true
            shift
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            print_error "Option inconnue: $1"
            show_help
            exit 1
            ;;
    esac
done

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "app/build.gradle.kts" ]; then
    print_error "Ce script doit être exécuté depuis la racine du projet"
    exit 1
fi

print_header "Déploiement Local Firebase App Distribution"
print_message "Type de build: $BUILD_TYPE"
print_message "Mode dry-run: $DRY_RUN"
print_message "Skip build: $SKIP_BUILD"

# Vérifier la configuration Firebase
if [ ! -f "app/google-services.json" ]; then
    print_error "Fichier google-services.json non trouvé dans app/"
    exit 1
fi

if [ ! -f "firebase-groups.txt" ]; then
    print_error "Fichier firebase-groups.txt non trouvé"
    exit 1
fi

if [ ! -f "release_notes.txt" ]; then
    print_error "Fichier release_notes.txt non trouvé"
    exit 1
fi

print_message "Configuration Firebase vérifiée ✅"

# Nettoyer le projet (sauf si on skip le build)
if [ "$SKIP_BUILD" = false ]; then
    print_message "Nettoyage du projet..."
    ./gradlew clean
fi

# Construire l'APK (sauf si on skip le build)
if [ "$SKIP_BUILD" = false ]; then
    print_message "Construction de l'APK $BUILD_TYPE..."
    if [ "$BUILD_TYPE" = "release" ]; then
        ./gradlew assembleRelease
    else
        ./gradlew assembleDebug
    fi
fi

# Vérifier que l'APK a été créé
APK_PATH=""
if [ "$BUILD_TYPE" = "release" ]; then
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
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    else
        print_error "APK debug non trouvé"
        exit 1
    fi
fi

print_message "APK trouvé: $APK_PATH"
print_message "Taille: $(du -h "$APK_PATH" | cut -f1)"

# Déployer vers Firebase App Distribution
if [ "$DRY_RUN" = true ]; then
    print_message "Mode dry-run activé - simulation du déploiement..."
    print_message "APK qui serait déployé: $APK_PATH"
    print_message "Notes de version: $(cat release_notes.txt | head -3 | tr '\n' ' ')..."
    print_message "Groupes de testeurs: $(cat firebase-groups.txt)"
else
    print_message "Déploiement vers Firebase App Distribution..."
    if [ "$BUILD_TYPE" = "release" ]; then
        ./gradlew appDistributionUploadRelease
    else
        ./gradlew appDistributionUploadDebug
    fi
    
    print_message "✅ Déploiement terminé avec succès!"
    
    # Notification (optionnelle)
    if [ "$NOTIFY" = true ]; then
        print_message "Envoi de la notification..."
        # Ici vous pouvez ajouter l'envoi d'une notification (Slack, email, etc.)
        echo "🔔 Déploiement $BUILD_TYPE terminé avec succès!" | tee -a deploy.log
    fi
fi

print_header "Résumé du déploiement"
print_message "Type: $BUILD_TYPE"
print_message "APK: $APK_PATH"
print_message "Mode: $([ "$DRY_RUN" = true ] && echo "Simulation" || echo "Déploiement réel")"
print_message "Status: $([ "$DRY_RUN" = true ] && echo "Simulé" || echo "Réussi")"

if [ "$DRY_RUN" = false ]; then
    print_message "📱 Vérifiez Firebase Console pour voir votre build distribué"
    print_message "🔗 Console: https://console.firebase.google.com/project/synestesia-a0ea7/appdistribution"
fi
