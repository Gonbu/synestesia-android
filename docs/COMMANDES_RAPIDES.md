# Commandes Rapides - Gestion des Annexes Synestesia

## 🚀 Génération Automatique

### Générer toutes les annexes

```bash
./scripts/generate-annexes.sh
```

### Générer uniquement le Bloc 2

```bash
./scripts/generate-annexes.sh bloc2
```

### Générer uniquement le Bloc 4

```bash
./scripts/generate-annexes.sh bloc4
```

## 🔧 Rapports de Qualité

### Lint (Vérification du code)

```bash
./gradlew lintDebug
```

### Detekt (Analyse statique)

```bash
./gradlew detekt
```

### KtLint (Formatage Kotlin)

```bash
./gradlew ktlintCheck
```

### JaCoCo (Couverture de code)

```bash
./gradlew jacocoTestReport
```

## 🏗️ Build et Artefacts

### Build Debug

```bash
./gradlew assembleDebug
```

### Build Release

```bash
./gradlew bundleRelease
```

### Clean du projet

```bash
./gradlew clean
```

## 📦 Gestion des Archives

### Créer l'archive Bloc 2

```bash
cd docs/bloc2 && zip -r annexes.zip annexes/ && cd ../..
```

### Créer l'archive Bloc 4

```bash
cd docs/bloc4 && zip -r annexes.zip annexes/ && cd ../..
```

### Créer toutes les archives

```bash
./scripts/generate-annexes.sh
```

## 📁 Navigation et Vérification

### Voir la structure des annexes

```bash
tree docs/bloc*/annexes/ || find docs/bloc*/annexes/ -type d
```

### Vérifier les tailles des archives

```bash
ls -lh docs/bloc*/annexes.zip
```

### Lister les rapports générés

```bash
find docs/bloc*/annexes/ -name "*.html" -o -name "*.xml" -o -name "*.txt"
```

## 🐛 Dépannage

### Erreur Lint - Créer une baseline

```bash
./gradlew updateLintBaseline
```

### Erreur Gradle - Nettoyer et relancer

```bash
./gradlew clean
./gradlew build
```

### Problème de cache - Nettoyer le cache

```bash
./gradlew cleanBuildCache
```

## 📋 Checklist Rapide

### Avant de générer les annexes

- [ ] Projet compilé sans erreur
- [ ] Tests unitaires passent
- [ ] Configuration JaCoCo active

### Après génération

- [ ] Vérifier les tailles des archives
- [ ] Tester l'ouverture des rapports HTML
- [ ] Valider la structure des dossiers

## 🔍 Vérification des Rapports

### Ouvrir les rapports dans le navigateur

```bash
# Lint
open docs/bloc2/annexes/qualite/lint-results-debug.html

# Detekt
open docs/bloc2/annexes/qualite/detekt.html

# JaCoCo
open docs/bloc2/annexes/couverture/jacoco/jacocoTestReport/html/index.html
```

### Vérifier les logs de build

```bash
# Voir les logs détaillés
./gradlew lintDebug --info

# Voir les logs de debug
./gradlew lintDebug --debug
```

## 📊 Métriques de Qualité

### Vérifier la couverture JaCoCo

```bash
./gradlew jacocoTestCoverageVerification
```

### Voir les dépendances obsolètes

```bash
./gradlew dependencyUpdates
```

### Analyser les conflits de dépendances

```bash
./gradlew dependencyInsight --dependency androidx.core:core-ktx
```

## 🚨 Erreurs Communes

### Erreur "SigningConfig not found"

- Vérifier que `keystore/release.keystore` existe
- Vérifier `secrets.properties`

### Erreur "Firebase App Distribution"

- Ajouter l'import manquant dans `build.gradle.kts`
- Vérifier `google-services.json`

### Erreur "JaCoCo not found"

- Vérifier que le plugin est activé
- Relancer `./gradlew clean`

## 💡 Astuces

### Génération incrémentale

- Utiliser `./gradlew` au lieu de `./gradlew clean build`
- Les rapports sont mis à jour automatiquement

### Optimisation des performances

- Utiliser le daemon Gradle (activé par défaut)
- Éviter de relancer `clean` inutilement

### Sauvegarde des rapports

- Les archives ZIP contiennent tout
- Garder une copie des rapports importants

---

**Note**: Toutes ces commandes doivent être exécutées depuis la racine du projet Synestesia.
