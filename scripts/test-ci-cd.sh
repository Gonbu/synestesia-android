#!/bin/bash

# Script de test pour vérifier la configuration des workflows CI/CD
# Usage: ./scripts/test-ci-cd.sh

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE} $1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_failure() {
    echo -e "${RED}❌ $1${NC}"
}

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "app/build.gradle.kts" ]; then
    print_error "Ce script doit être exécuté depuis la racine du projet"
    exit 1
fi

print_header "Test de Configuration CI/CD Firebase App Distribution"

# Test 1: Vérification des fichiers de configuration
print_message "1. Vérification des fichiers de configuration..."

# Vérifier les workflows CI/CD
if [ -f ".github/workflows/firebase-app-distribution.yml" ]; then
    print_success "GitHub Actions workflow trouvé"
else
    print_failure "GitHub Actions workflow manquant"
fi

if [ -f ".gitlab-ci.yml" ]; then
    print_success "GitLab CI pipeline trouvé"
else
    print_failure "GitLab CI pipeline manquant"
fi

# Vérifier les scripts de déploiement
if [ -f "scripts/deploy-to-firebase.sh" ]; then
    print_success "Script de déploiement principal trouvé"
else
    print_failure "Script de déploiement principal manquant"
fi

if [ -f "scripts/deploy-local.sh" ]; then
    print_success "Script de déploiement local trouvé"
else
    print_failure "Script de déploiement local manquant"
fi

# Test 2: Vérification de la configuration Firebase
print_message "2. Vérification de la configuration Firebase..."

if [ -f "app/google-services.json" ]; then
    print_success "google-services.json trouvé"
else
    print_failure "google-services.json manquant"
fi

if [ -f "firebase-groups.txt" ]; then
    print_success "firebase-groups.txt trouvé"
    print_message "   Groupes configurés: $(cat firebase-groups.txt)"
else
    print_failure "firebase-groups.txt manquant"
fi

if [ -f "release_notes.txt" ]; then
    print_success "release_notes.txt trouvé"
else
    print_failure "release_notes.txt manquant"
fi

# Test 3: Vérification de la configuration Gradle
print_message "3. Vérification de la configuration Gradle..."

# Vérifier que le plugin Firebase App Distribution est configuré
if grep -q "com.google.firebase.appdistribution" app/build.gradle.kts; then
    print_success "Plugin Firebase App Distribution configuré dans app/build.gradle.kts"
else
    print_failure "Plugin Firebase App Distribution manquant dans app/build.gradle.kts"
fi

# Vérifier que la configuration firebaseAppDistribution est présente
if grep -q "firebaseAppDistribution" app/build.gradle.kts; then
    print_success "Configuration firebaseAppDistribution trouvée"
else
    print_failure "Configuration firebaseAppDistribution manquante"
fi

# Test 4: Vérification des tâches Gradle
print_message "4. Vérification des tâches Gradle..."

# Vérifier que les tâches Firebase sont disponibles
if ./gradlew :app:tasks --all | grep -q "appDistributionUploadDebug"; then
    print_success "Tâche appDistributionUploadDebug disponible"
else
    print_failure "Tâche appDistributionUploadDebug manquante"
fi

if ./gradlew :app:tasks --all | grep -q "appDistributionUploadRelease"; then
    print_success "Tâche appDistributionUploadRelease disponible"
else
    print_failure "Tâche appDistributionUploadRelease manquante"
fi

# Test 5: Test de compilation
print_message "5. Test de compilation..."

if ./gradlew :app:assembleDebug --dry-run > /dev/null 2>&1; then
    print_success "Compilation debug réussie"
else
    print_failure "Compilation debug échouée"
fi

# Test 6: Test de déploiement (dry-run)
print_message "6. Test de déploiement (dry-run)..."

if ./gradlew :app:appDistributionUploadDebug --dry-run > /dev/null 2>&1; then
    print_success "Déploiement debug (dry-run) réussi"
else
    print_failure "Déploiement debug (dry-run) échoué"
fi

# Test 7: Vérification de la documentation
print_message "7. Vérification de la documentation..."

if [ -f "docs/FIREBASE_APP_DISTRIBUTION.md" ]; then
    print_success "Documentation Firebase App Distribution trouvée"
else
    print_failure "Documentation Firebase App Distribution manquante"
fi

if [ -f "docs/CI_CD_WORKFLOWS.md" ]; then
    print_success "Documentation CI/CD trouvée"
else
    print_failure "Documentation CI/CD manquante"
fi

# Test 8: Vérification des permissions
print_message "8. Vérification des permissions..."

if [ -x "scripts/deploy-to-firebase.sh" ]; then
    print_success "Script deploy-to-firebase.sh exécutable"
else
    print_warning "Script deploy-to-firebase.sh non exécutable"
    print_message "   Exécuter: chmod +x scripts/deploy-to-firebase.sh"
fi

if [ -x "scripts/deploy-local.sh" ]; then
    print_success "Script deploy-local.sh exécutable"
else
    print_warning "Script deploy-local.sh non exécutable"
    print_message "   Exécuter: chmod +x scripts/deploy-local.sh"
fi

# Résumé final
print_header "Résumé des Tests"

print_message "Configuration CI/CD Firebase App Distribution vérifiée !"
print_message ""
print_message "📋 Prochaines étapes :"
print_message "1. Configurer le secret GOOGLE_SERVICES_JSON dans GitHub/GitLab"
print_message "2. Tester les workflows avec des branches de test"
print_message "3. Vérifier les déploiements dans Firebase Console"
print_message ""
print_message "🔧 En cas de problème :"
print_message "- Consulter CI_CD_SETUP.md pour la configuration"
print_message "- Utiliser ./scripts/deploy-local.sh pour les tests locaux"
print_message "- Vérifier les logs CI/CD dans votre plateforme"

print_header "Tests Terminés"
