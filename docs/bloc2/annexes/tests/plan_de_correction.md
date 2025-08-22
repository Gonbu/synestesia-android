# Plan de Correction - Projet Synestesia

## Erreurs Critiques (à corriger en priorité)

### 1. Erreur Lint - WrongGradleMethod

- **Fichier**: `app/build.gradle.kts:124`
- **Problème**: Import manquant pour `firebaseAppDistribution`
- **Solution**: Ajouter l'import `import com.google.firebase.appdistribution.gradle.firebaseAppDistribution`
- **Priorité**: HAUTE
- **Statut**: À faire

## Avertissements (à traiter en second)

### 2. Avertissements Lint (100 warnings)

- **Fichier**: `app/build/reports/lint-results-debug.html`
- **Problème**: 100 avertissements de qualité de code
- **Solution**: Analyser et corriger les warnings selon les règles définies
- **Priorité**: MOYENNE
- **Statut**: À analyser

### 3. Indices Lint (6 hints)

- **Fichier**: `app/build/reports/lint-results-debug.html`
- **Problème**: 6 suggestions d'amélioration
- **Solution**: Évaluer et appliquer les suggestions pertinentes
- **Priorité**: BASSE
- **Statut**: À évaluer

## Plan d'Action

### Phase 1 - Corrections Critiques (1-2 jours)

1. Corriger l'erreur d'import Firebase
2. Vérifier que le build passe sans erreur

### Phase 2 - Amélioration Qualité (3-5 jours)

1. Analyser les 100 warnings
2. Corriger les warnings prioritaires
3. Évaluer les 6 hints

### Phase 3 - Validation (1 jour)

1. Relancer tous les outils de qualité
2. Vérifier que les rapports sont propres
3. Mettre à jour la baseline si nécessaire

## Métriques de Suivi

- **Erreurs**: 1 → 0
- **Warnings**: 100 → <20
- **Hints**: 6 → <10

## Notes

- Utiliser `./gradlew updateLintBaseline` si nécessaire
- Documenter les corrections dans le CHANGELOG
- Tester après chaque correction majeure
