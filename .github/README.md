# GitHub Actions Workflows

Ce dossier contient les workflows GitHub Actions pour automatiser le développement de l'application Synestesia.

## Workflows disponibles

### 1. Android CI/CD (`android.yml`)

**Déclencheurs :**

- Push sur les branches `main` et `develop`
- Pull Request vers `main` et `develop`

**Jobs :**

#### Quality Check

- Vérification du code avec **ktlint** (formatage Kotlin)
- Analyse statique avec **detekt** (détection de code smell)

#### Test

- Exécution des tests unitaires (`./gradlew test`)
- Exécution des tests d'intégration (`./gradlew connectedAndroidTest`)

#### Build

- Construction des APK debug et release
- Upload des artefacts pour téléchargement

#### Security Check

- Scan de vulnérabilités avec Trivy
- Intégration avec GitHub Security tab

### 2. Release (`release.yml`)

**Déclencheur :**

- Push d'un tag commençant par `v*` (ex: `v1.0.0`)

**Actions :**

- Build automatique de l'APK release
- Création d'une release GitHub avec l'APK
- Upload des artefacts

## Utilisation

### Pour déclencher une release

```bash
git tag v1.0.0
git push origin v1.0.0
```

### Pour vérifier le statut des workflows

1. Aller dans l'onglet "Actions" de ton repository
2. Sélectionner le workflow souhaité
3. Voir les logs et résultats

### Pour télécharger les APK

1. Aller dans l'onglet "Actions"
2. Sélectionner un workflow "Build APK" terminé
3. Télécharger les artefacts "app-debug" ou "app-release"

## Configuration requise

### Secrets GitHub (optionnel)

- `ANDROID_HOME` : Pour les tests d'intégration
- `GITHUB_TOKEN` : Automatiquement fourni par GitHub

### Branches protégées

Il est recommandé de configurer les branches `main` et `develop` comme protégées :

- Requérir le passage des checks CI/CD
- Requérir une review avant merge
- Bloquer les pushes directs

## Personnalisation

### Modifier les branches déclenchées

Éditer le fichier `android.yml` :

```yaml
on:
  push:
    branches: [ main, develop, feature/* ]
  pull_request:
    branches: [ main, develop ]
```

### Ajouter des jobs personnalisés

Ajouter de nouveaux jobs dans `android.yml` selon tes besoins :

- Déploiement automatique
- Notifications Slack/Discord
- Tests de performance
- Build pour différents variants

## Dépannage

### Erreurs courantes

1. **Échec des tests d'intégration** : Vérifier que `ANDROID_HOME` est configuré
2. **Échec du build** : Vérifier les dépendances et la configuration Gradle
3. **Échec de ktlint/detekt** : Corriger les problèmes de formatage et de qualité du code

### Logs détaillés

Tous les workflows génèrent des logs détaillés dans l'onglet Actions de GitHub.
