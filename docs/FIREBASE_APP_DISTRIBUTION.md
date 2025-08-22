# Firebase App Distribution

Ce document explique comment utiliser Firebase App Distribution dans le projet Synestesia.

## Configuration

### 1. Plugin Gradle

Le plugin Firebase App Distribution est configuré dans :

- `build.gradle.kts` (racine) : déclaration du plugin
- `app/build.gradle.kts` : application du plugin et configuration

### 2. Configuration actuelle

```kotlin
firebaseAppDistribution {
    artifactType = "APK"
    releaseNotesFile = "release_notes.txt"
    groups = "testers"
    serviceAccountFile = "firebase-service-account.json"
}
```

## Utilisation

### Déploiement automatique

Utilisez le script de déploiement :

```bash
# Déployer une version debug
./scripts/deploy-to-firebase.sh debug

# Déployer une version release
./scripts/deploy-to-firebase.sh release
```

### Déploiement manuel

```bash
# Construire et déployer une version debug
./gradlew appDistributionUploadDebug

# Construire et déployer une version release
./gradlew appDistributionUploadRelease
```

## Configuration des groupes de testeurs

### 1. Dans Firebase Console

1. Allez sur [Firebase Console](https://console.firebase.google.com)
2. Sélectionnez votre projet
3. Dans le menu, cliquez sur "App Distribution"
4. Allez dans l'onglet "Testers & groups"
5. Créez des groupes (ex: "developers", "qa", "testers")

### 2. Dans le code

Modifiez la configuration dans `app/build.gradle.kts` :

```kotlin
firebaseAppDistribution {
    groups = "developers,qa,testers" // Séparés par des virgules
}
```

## Notes de version

Les notes de version sont stockées dans `app/release_notes.txt`. Modifiez ce fichier avant chaque déploiement pour informer vos testeurs des changements.

## Authentification

### Option 1 : Compte de service (recommandé pour CI/CD)

1. Créez un compte de service dans Firebase Console
2. Téléchargez le fichier JSON
3. Placez-le dans `app/firebase-service-account.json`
4. Décommentez la ligne dans la configuration :

```kotlin
serviceAccountFile = "firebase-service-account.json"
```

### Option 2 : Authentification interactive

Laissez la ligne `serviceAccountFile` commentée. Gradle vous demandera de vous connecter lors du premier déploiement.

## Intégration CI/CD

### GitHub Actions

```yaml
- name: Deploy to Firebase App Distribution
  run: |
    ./gradlew appDistributionUploadRelease
  env:
    FIREBASE_TOKEN: ${{ secrets.FIREBASE_TOKEN }}
```

### GitLab CI

```yaml
deploy:
  script:
    - ./gradlew appDistributionUploadRelease
  environment:
    name: production
```

## Dépannage

### Erreur d'authentification

```bash
# Vérifiez que vous êtes connecté à Firebase
firebase login

# Ou utilisez un compte de service
export GOOGLE_APPLICATION_CREDENTIALS="path/to/service-account.json"
```

### Erreur de build

```bash
# Nettoyez le projet
./gradlew clean

# Vérifiez la configuration
./gradlew tasks --group="firebase"
```

### Erreur de déploiement

1. Vérifiez que l'APK a été construit
2. Vérifiez les permissions Firebase
3. Vérifiez la configuration des groupes

## Commandes utiles

```bash
# Lister toutes les tâches Firebase
./gradlew tasks --group="firebase"

# Vérifier la configuration
./gradlew appDistributionUploadDebug --dry-run

# Nettoyer et reconstruire
./gradlew clean assembleDebug appDistributionUploadDebug
```

## Ressources

- [Documentation officielle Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
- [Plugin Gradle Firebase App Distribution](https://github.com/firebase/firebase-appdistribution-gradle-plugin)
- [Configuration des groupes de testeurs](https://firebase.google.com/docs/app-distribution/android/distribute-console#manage-testers)
