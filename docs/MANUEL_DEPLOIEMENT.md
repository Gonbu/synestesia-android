# Manuel de Déploiement - Synestesia

## 🚀 Vue d'Ensemble

Ce document décrit le processus complet de déploiement de l'application Synestesia, depuis la compilation jusqu'à la distribution aux utilisateurs finaux.

## 🏗️ Processus de Build

### Prérequis

- Android Studio ou ligne de commande
- JDK 11 ou supérieur
- Variables d'environnement configurées
- Clés de signature configurées

### Configuration des Variables d'Environnement

```bash
# Ajouter dans ~/.bashrc ou ~/.zshrc
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/build-tools
```

### Build Debug

```bash
# Nettoyer le projet
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# APK généré dans : app/build/outputs/apk/debug/app-debug.apk
```

### Build Release

```bash
# Build release APK
./gradlew assembleRelease

# APK généré dans : app/build/outputs/apk/release/app-release.apk
```

### Build Release Bundle (Recommandé pour Play Store)

```bash
# Build release bundle
./gradlew bundleRelease

# Bundle généré dans : app/build/outputs/bundle/release/app-release.aab
```

## 🔐 Processus de Signature

### Génération des Clés de Signature

#### Clé Debug (automatique)

```bash
# Android Studio génère automatiquement une clé debug
# Stockée dans : ~/.android/debug.keystore
# Mot de passe : android
# Alias : androiddebugkey
```

#### Clé Release (production)

```bash
# Générer une nouvelle clé de signature
keytool -genkey -v -keystore synestesia-release.keystore \
  -alias synestesia-key \
  -keyalg RSA -keysize 2048 \
  -validity 10000 \
  -storepass "votre_mot_de_passe" \
  -keypass "votre_mot_de_passe"
```

### Configuration de la Signature

#### Fichier keystore.properties (non versionné)

```properties
storeFile=synestesia-release.keystore
storePassword=votre_mot_de_passe
keyAlias=synestesia-key
keyPassword=votre_mot_de_passe
```

#### Configuration build.gradle.kts

```kotlin
android {
    signingConfigs {
        create("release") {
            val keystoreProperties = Properties()
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }
            
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storeFile = keystoreProperties["storeFile"]?.let { file(it) }
            storePassword = keystoreProperties["storePassword"] as String?
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## 📱 Déploiement

### Déploiement Local

```bash
# Installer sur appareil connecté
./gradlew installDebug

# Installer version release
./gradlew installRelease
```

### Déploiement via ADB

```bash
# Lister les appareils
adb devices

# Installer APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Installer APK release
adb install app/build/outputs/apk/release/app-release.apk
```

### Déploiement Firebase App Distribution

#### Configuration Firebase

```bash
# Installer Firebase CLI
npm install -g firebase-tools

# Se connecter à Firebase
firebase login

# Initialiser le projet
firebase init
```

#### Déploiement Automatique

```bash
# Distribuer aux testeurs
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app $FIREBASE_APP_ID \
  --groups "testers" \
  --release-notes "Build from commit $(git rev-parse --short HEAD)"
```

### Déploiement Google Play Store

#### Préparation

1. Créer un compte développeur Google Play
2. Configurer l'application dans la console
3. Préparer les métadonnées (description, captures d'écran)
4. Configurer la politique de confidentialité

#### Upload

```bash
# Via Google Play Console
# 1. Aller dans "Production" > "Créer une nouvelle version"
# 2. Uploader le fichier .aab
# 3. Remplir les notes de version
# 4. Sauvegarder et examiner
# 5. Déployer en production
```

## 🔄 Pipeline CI/CD

### Déclenchement Automatique

- **Branche develop** : Build et tests automatiques
- **Branche main** : Build, tests, et déploiement automatique
- **Pull Request** : Validation de qualité et tests

### Workflow GitHub Actions

```yaml
# Déclenchement sur push vers main
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Build APK
        run: ./gradlew assembleRelease
        
      - name: Deploy to Firebase
        run: |
          firebase appdistribution:distribute \
            app/build/outputs/apk/release/app-release.apk \
            --app ${{ secrets.FIREBASE_APP_ID }} \
            --groups "testers"
```

### Secrets Requis

```yaml
# Dans GitHub Secrets
FIREBASE_TOKEN: Token d'authentification Firebase
FIREBASE_APP_ID: ID de l'application Firebase
GOOGLE_PLAY_SERVICE_ACCOUNT: Compte de service Google Play
```

## 📊 Monitoring et Validation

### Vérifications Post-Déploiement

- [ ] L'application se lance sans crash
- [ ] Toutes les fonctionnalités principales fonctionnent
- [ ] Les permissions sont demandées correctement
- [ ] La synchronisation Firebase fonctionne
- [ ] Les performances sont acceptables

### Métriques de Déploiement

- **Temps de build** : < 10 minutes
- **Taille APK** : < 50MB
- **Taux de succès** : > 99%
- **Temps de déploiement** : < 5 minutes

### Rollback

```bash
# En cas de problème, revenir à la version précédente
# 1. Identifier la version stable précédente
# 2. Re-déployer cette version
# 3. Analyser le problème sur la version défaillante
# 4. Corriger et re-déployer
```

## 🛠️ Outils et Ressources

### Outils de Build

- **Gradle** : Build system principal
- **Android SDK** : Outils de développement Android
- **ProGuard/R8** : Obfuscation et optimisation

### Outils de Déploiement

- **Firebase CLI** : Déploiement Firebase
- **Google Play Console** : Déploiement Play Store
- **ADB** : Déploiement local et debug

### Outils de Monitoring

- **Firebase Crashlytics** : Monitoring des crashes
- **Firebase Analytics** : Métriques d'utilisation
- **Google Play Console** : Statistiques de téléchargement

## 📝 Checklist de Déploiement

### Avant le Déploiement

- [ ] Tous les tests passent
- [ ] La couverture de code est > 80%
- [ ] Les tests de sécurité sont passés
- [ ] L'APK est signé correctement
- [ ] Les métadonnées sont à jour

### Pendant le Déploiement

- [ ] Suivre le pipeline CI/CD
- [ ] Vérifier les artefacts générés
- [ ] Valider la signature
- [ ] Tester l'installation

### Après le Déploiement

- [ ] Vérifier le bon fonctionnement
- [ ] Monitorer les métriques
- [ ] Surveiller les crash reports
- [ ] Collecter les retours utilisateurs

## 🚨 Gestion des Incidents

### Types d'Incidents

1. **Build échoue** : Vérifier la configuration et les dépendances
2. **Signature échoue** : Vérifier les clés et mots de passe
3. **Déploiement échoue** : Vérifier les permissions et la configuration
4. **Application ne fonctionne pas** : Analyser les logs et crash reports

### Procédure d'Urgence

1. Arrêter le déploiement en cours
2. Identifier la cause du problème
3. Corriger le problème
4. Re-déployer après validation
5. Documenter l'incident

## 📚 Ressources Supplémentaires

- [Documentation Android Build](https://developer.android.com/studio/build)
- [Guide de Signature Android](https://developer.android.com/studio/publish/app-signing)
- [Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
- [Google Play Console](https://play.google.com/console)
- [GitHub Actions](https://docs.github.com/en/actions)
