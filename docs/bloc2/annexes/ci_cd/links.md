# Liens CI/CD - Bloc 2

## Workflows GitHub Actions

### Workflow Principal

- **Fichier**: `.github/workflows/android-ci.yml`
- **Status**: [À configurer]
- **URL**: [Lien vers le workflow]

### Workflow de Release

- **Fichier**: `.github/workflows/android-release-internal.yml`
- **Status**: [À configurer]
- **URL**: [Lien vers le workflow]

## Artefacts de Build

### APK Debug

- **Fichier**: `app-debug.apk`
- **Généré par**: `./gradlew assembleDebug`
- **Emplacement**: `app/build/outputs/apk/debug/`
- **Status**: [À générer]

### Bundle Release

- **Fichier**: `app-release.aab`
- **Généré par**: `./gradlew bundleRelease`
- **Emplacement**: `app/build/outputs/bundle/release/`
- **Status**: [À générer]

## Rapports de Qualité

### Lint

- **Rapport HTML**: `docs/bloc2/annexes/qualite/lint-results-debug.html`
- **Rapport TXT**: `docs/bloc2/annexes/qualite/lint-results-debug.txt`
- **Généré par**: `./gradlew lintDebug`

### Detekt

- **Rapport HTML**: `docs/bloc2/annexes/qualite/detekt.html`
- **Généré par**: `./gradlew detekt`

### KtLint

- **Rapport**: [À générer]
- **Généré par**: `./gradlew ktlintCheck`

## Métriques de Build

### Temps de Build

- **Debug**: [À mesurer]
- **Release**: [À mesurer]

### Taille des Artefacts

- **APK Debug**: [À mesurer]
- **Bundle Release**: [À mesurer]

## Notes

- Les workflows GitHub Actions doivent être configurés
- Les artefacts de build doivent être générés et testés
- Les rapports de qualité doivent être mis à jour régulièrement
- Les métriques de performance doivent être collectées

## Prochaines Étapes

1. Configurer les workflows GitHub Actions
2. Générer les artefacts de build
3. Collecter les métriques de performance
4. Mettre à jour ce fichier avec les vrais liens
