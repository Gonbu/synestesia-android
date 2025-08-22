#!/bin/bash

# Script de génération des annexes pour le projet Synestesia
# Usage: ./scripts/generate-annexes.sh [bloc2|bloc4|all]

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction d'affichage
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Vérification des prérequis
check_prerequisites() {
    print_status "Vérification des prérequis..."
    
    if ! command -v gradle &> /dev/null && ! [ -f "./gradlew" ]; then
        print_error "Gradle n'est pas installé et gradlew n'est pas présent"
        exit 1
    fi
    
    if ! command -v zip &> /dev/null; then
        print_error "zip n'est pas installé"
        exit 1
    fi
    
    print_success "Prérequis vérifiés"
}

# Génération des rapports de qualité
generate_quality_reports() {
    print_status "Génération des rapports de qualité..."
    
    if [ -f "./gradlew" ]; then
        print_status "Lancement de Lint..."
        ./gradlew lintDebug || print_warning "Lint a échoué, mais le rapport peut être partiel"
        
        print_status "Lancement de Detekt..."
        ./gradlew detekt || print_warning "Detekt a échoué, mais le rapport peut être partiel"
        
        print_status "Lancement de KtLint..."
        ./gradlew ktlintCheck || print_warning "KtLint a échoué, mais le rapport peut être partiel"
        
        print_success "Rapports de qualité générés"
    else
        print_warning "gradlew non trouvé, utilisation de gradle"
        gradle lintDebug detekt ktlintCheck || print_warning "Certains rapports ont échoué"
    fi
}

# Copie des rapports générés
copy_reports() {
    print_status "Copie des rapports générés..."
    
    # Création des dossiers si nécessaire
    mkdir -p docs/bloc2/annexes/qualite
    mkdir -p docs/bloc4/annexes/monitoring
    
    # Copie des rapports Lint
    if [ -f "app/build/reports/lint-results-debug.html" ]; then
        cp app/build/reports/lint-results-debug.html docs/bloc2/annexes/qualite/
        print_success "Rapport Lint copié"
    fi
    
    if [ -f "app/build/intermediates/lint_intermediate_text_report/debug/lintReportDebug/lint-results-debug.txt" ]; then
        cp app/build/intermediates/lint_intermediate_text_report/debug/lintReportDebug/lint-results-debug.txt docs/bloc2/annexes/qualite/
        print_success "Rapport Lint TXT copié"
    fi
    
    # Copie des rapports Detekt
    if [ -d "app/build/reports/detekt" ]; then
        cp -r app/build/reports/detekt/* docs/bloc2/annexes/qualite/
        print_success "Rapports Detekt copiés"
    fi
    
    print_success "Rapports copiés"
}

# Génération des artefacts de build
generate_build_artifacts() {
    print_status "Génération des artefacts de build..."
    
    if [ -f "./gradlew" ]; then
        print_status "Build debug..."
        ./gradlew assembleDebug || print_warning "Build debug échoué"
        
        print_status "Build release..."
        ./gradlew bundleRelease || print_warning "Build release échoué"
        
        print_success "Artefacts de build générés"
    else
        print_warning "gradlew non trouvé, utilisation de gradle"
        gradle assembleDebug bundleRelease || print_warning "Certains builds ont échoué"
    fi
}

# Création des archives ZIP
create_archives() {
    print_status "Création des archives ZIP..."
    
    if [ -d "docs/bloc2/annexes" ]; then
        cd docs/bloc2
        zip -r annexes.zip annexes/ > /dev/null
        cd ../..
        print_success "Archive Bloc 2 créée: docs/bloc2/annexes.zip"
    fi
    
    if [ -d "docs/bloc4/annexes" ]; then
        cd docs/bloc4
        zip -r annexes.zip annexes/ > /dev/null
        cd ../..
        print_success "Archive Bloc 4 créée: docs/bloc4/annexes.zip"
    fi
}

# Affichage du résumé
show_summary() {
    print_status "Résumé de la génération des annexes..."
    
    echo ""
    echo "📁 Structure des annexes:"
    echo "├── Bloc 2: docs/bloc2/annexes/"
    echo "└── Bloc 4: docs/bloc4/annexes/"
    
    echo ""
    echo "📦 Archives créées:"
    if [ -f "docs/bloc2/annexes.zip" ]; then
        echo "├── Bloc 2: $(ls -lh docs/bloc2/annexes.zip | awk '{print $5}')"
    fi
    if [ -f "docs/bloc4/annexes.zip" ]; then
        echo "└── Bloc 4: $(ls -lh docs/bloc4/annexes.zip | awk '{print $5}')"
    fi
    
    echo ""
    echo "📋 Prochaines étapes:"
    echo "1. Capturer les screenshots des workflows GitHub Actions"
    echo "2. Capturer les screenshots de l'application"
    echo "3. Capturer les métriques Firebase (Crashlytics, Performance)"
    echo "4. Configurer JaCoCo pour la couverture de code"
    echo "5. Configurer les workflows GitHub Actions"
    
    echo ""
    print_success "Génération des annexes terminée !"
}

# Fonction principale
main() {
    local bloc=${1:-"all"}
    
    echo "🚀 Générateur d'Annexes - Projet Synestesia"
    echo "============================================="
    echo ""
    
    check_prerequisites
    
    case $bloc in
        "bloc2")
            print_status "Génération des annexes du Bloc 2..."
            generate_quality_reports
            copy_reports
            generate_build_artifacts
            create_archives
            ;;
        "bloc4")
            print_status "Génération des annexes du Bloc 4..."
            create_archives
            ;;
        "all"|*)
            print_status "Génération de toutes les annexes..."
            generate_quality_reports
            copy_reports
            generate_build_artifacts
            create_archives
            ;;
    esac
    
    show_summary
}

# Exécution du script
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
