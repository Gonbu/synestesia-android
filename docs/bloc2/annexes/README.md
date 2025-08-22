# Annexes - Bloc 2 : Développement et Qualité

## Structure des Annexes

```
docs/bloc2/annexes/
├── README.md                 # Ce fichier
├── workflows/               # Workflows CI/CD
├── qualite/                # Rapports de qualité (Lint, Detekt, KtLint)
├── couverture/             # Rapports de couverture de code
│   └── jacoco/            # Rapports JaCoCo
├── tests/                  # Tests et recettes
├── captures/               # Captures d'écran
├── docs/                   # Documentation
├── ci_cd/                  # Liens CI/CD
│   └── links.md           # Liens vers les artefacts
└── CHANGELOG.md            # Historique des versions
```

## Contenu des Annexes

### A. Workflows CI/CD

- **Fichiers**: Workflows GitHub Actions complets
- **Emplacement**: `workflows/`
- **Statut**: [À configurer]

### B. Rapport Android Lint

- **Fichiers**: `lint-results-debug.html`, `lint-results-debug.txt`
- **Emplacement**: `qualite/`
- **Statut**: ✅ Généré

### C. Rapport Detekt

- **Fichiers**: `detekt.html`, `detekt.xml`
- **Emplacement**: `qualite/`
- **Statut**: ✅ Généré

### D. Rapport KtLint

- **Fichiers**: [À générer]
- **Emplacement**: `qualite/`
- **Statut**: [À générer]

### E. Couverture JaCoCo

- **Fichiers**: [À configurer]
- **Emplacement**: `couverture/jacoco/`
- **Statut**: [À configurer]

### F. Captures CI

- **Fichiers**: [À capturer]
- **Emplacement**: `captures/`
- **Statut**: [À faire]

### G. Captures de l'App

- **Fichiers**: [À capturer]
- **Emplacement**: `captures/`
- **Statut**: [À faire]

### H. Cahier de Recettes

- **Fichier**: `CAHIER_RECETTES.md`
- **Emplacement**: `tests/`
- **Statut**: ✅ Copié

### I. Plan de Correction

- **Fichier**: `plan_de_correction.md`
- **Emplacement**: `tests/`
- **Statut**: ✅ Créé

### J. Manuel de Déploiement

- **Fichier**: `MANUEL_DEPLOIEMENT.md`
- **Emplacement**: `docs/`
- **Statut**: ✅ Copié

### K. Manuel d'Utilisation

- **Fichier**: `MANUEL_UTILISATION.md`
- **Emplacement**: `docs/`
- **Statut**: ✅ Copié

### L. Manuel de Mise à Jour

- **Fichier**: `manuel_mise_a_jour.md`
- **Emplacement**: `docs/`
- **Statut**: ✅ Créé

### M. CHANGELOG

- **Fichier**: `CHANGELOG.md`
- **Emplacement**: `annexes/`
- **Statut**: ✅ Copié

### N. Bundle/APK Release

- **Fichiers**: [À générer]
- **Emplacement**: `ci_cd/links.md`
- **Statut**: [À générer]

## Génération des Annexes

### Commandes à exécuter

```bash
# Rapports de qualité
./gradlew lintDebug detekt ktlintCheck

# Build de release
./gradlew bundleRelease

# Vérification
./gradlew tasks
```

### Fichiers à capturer

- Screenshots des workflows GitHub Actions
- Screenshots de l'application sur device/émulateur
- Captures des rapports de qualité

## Prochaines Étapes

1. **Configurer JaCoCo** pour la couverture de code
2. **Générer les artefacts de build** (APK/Bundle)
3. **Capturer les screenshots** des workflows et de l'app
4. **Configurer les workflows GitHub Actions**
5. **Créer l'archive ZIP** des annexes

## Notes

- Tous les rapports de qualité sont générés automatiquement
- Les captures d'écran doivent être faites manuellement
- Les workflows CI/CD doivent être configurés dans `.github/workflows/`
- L'archive finale sera créée avec `zip -r docs/bloc2/annexes.zip docs/bloc2/annexes/`
