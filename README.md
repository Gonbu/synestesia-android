# Synestesia - Application de Souvenirs Géolocalisés

## 📱 Description

Synestesia est une application Android qui permet aux utilisateurs de créer, gérer et partager des souvenirs géolocalisés. L'application utilise Google Maps pour afficher les souvenirs sur une carte interactive et Firebase pour le stockage des données.

## ✨ Fonctionnalités Principales

### 🗺️ Carte Interactive
- Affichage des souvenirs sur Google Maps
- Géolocalisation en temps réel
- Création de souvenirs en cliquant sur la carte
- Navigation vers les souvenirs existants
- Bouton de retour à la position actuelle

### 🎵 Fonctionnalités Audio
- **Enregistrement audio** : Capture d'audio directement dans l'application
- **Lecture audio** : Lecteur intégré pour écouter les souvenirs audio
- **Stockage cloud** : Sauvegarde automatique des fichiers audio sur Firebase
- **Gestion des permissions** : Demande automatique des permissions audio

### 📸 Capture Photo
- Prise de photo intégrée à l'application
- Gestion des permissions caméra
- Stockage cloud automatique
- Aperçu des photos prises

### ⚙️ Écran de Paramètres
- **Gestion des notifications** : Activer/désactiver les notifications
- **Apparence** : Mode sombre (à implémenter)
- **Confidentialité** : Gestion du partage de localisation
- **À propos** : Informations sur la version

### 👤 Écran de Profil
- **Informations utilisateur** : Nom d'utilisateur et email
- **Édition du profil** : Modification du nom d'utilisateur
- **Statistiques** : Nombre de souvenirs, favoris, partages
- **Actions du compte** : Sécurité, données, aide et support
- **Zone de danger** : Déconnexion et suppression de compte

### 🧭 Navigation
- **Barre de navigation en bas** avec 3 onglets :
  - Carte (icône de localisation)
  - Paramètres (icône d'engrenage)
  - Profil (icône de personne)
- Navigation fluide entre les écrans

## 🏗️ Architecture Technique

### Composants UI
- `MainNavigation` : Composant principal gérant la navigation
- `MapContent` : Affichage de la carte et gestion des souvenirs
- `SettingsScreen` : Écran des paramètres
- `ProfileScreen` : Écran de gestion du profil
- `BottomNavigation` : Barre de navigation en bas
- `AudioRecorderComponent` : Composant d'enregistrement audio
- `AudioPlayerComponent` : Composant de lecture audio
- `SouvenirForm` : Formulaire de création de souvenirs

### Services
- `FirestoreService` : Gestion des données Firebase
- `LocationManager` : Gestion de la géolocalisation
- `CameraManager` : Gestion de l'appareil photo
- `FirebaseStorageService` : Stockage des fichiers
- `AudioRecordingService` : Service d'enregistrement audio
- `AudioPlaybackService` : Service de lecture audio
- `AudioMetadataService` : Gestion des métadonnées audio

### Modèles
- `SouvenirItem` : Structure des données des souvenirs

### Gestion des Permissions
- `AudioPermission` : Gestion des permissions audio
- `CameraPermission` : Gestion des permissions caméra
- `LocationPermission` : Gestion des permissions de localisation

## 🎯 Qualité du Code

### Outils de Qualité Intégrés

#### KtLint
- **Formatage automatique** du code Kotlin
- **Règles de style** cohérentes
- **Intégration Git** avec hook pre-commit
- **Configuration** : `.editorconfig` et règles Android

#### Detekt
- **Analyse statique** du code Kotlin
- **Détection de bugs** et problèmes potentiels
- **Configuration personnalisée** : `config/detekt/detekt.yml`
- **Baseline** pour ignorer les violations existantes

### Commandes Disponibles

```bash
# Vérification du style de code
./gradlew ktlintCheck

# Formatage automatique
./gradlew ktlintFormat

# Analyse statique avec detekt
./gradlew detekt

# Nettoyage et rafraîchissement
./gradlew clean --refresh-dependencies
```

### Configuration des Éditeurs

#### Android Studio / IntelliJ IDEA
- Plugin KtLint installé automatiquement
- Formatage automatique à la sauvegarde
- Intégration avec l'éditeur

#### VS Code
- Configuration dans `.vscode/settings.json`
- Extensions recommandées pour Kotlin

## 🚀 Installation et Configuration

### Prérequis
- Android Studio
- JDK 11 ou supérieur
- Compte Google Cloud Platform avec Maps API activée
- Projet Firebase configuré

### Configuration
1. Clonez le repository
2. Ouvrez le projet dans Android Studio
3. Configurez votre fichier `google-services.json` Firebase
4. Ajoutez votre clé API Google Maps dans `local.properties`
5. Synchronisez le projet Gradle

### Compilation
```bash
./gradlew assembleDebug
```

### Installation
```bash
./gradlew installDebug
```

## 📁 Structure des Fichiers

```
app/src/main/java/com/billie/synestesia/
├── MainActivity.kt                 # Point d'entrée de l'application
├── SouvenirMap.kt                  # Composant principal de la carte
├── ui/
│   ├── MainNavigation.kt          # Navigation principale
│   ├── MapComponents.kt           # Composants de la carte
│   ├── SettingsScreen.kt          # Écran des paramètres
│   ├── ProfileScreen.kt           # Écran du profil
│   ├── BottomNavigation.kt        # Barre de navigation
│   ├── AudioRecorderComponent.kt  # Enregistrement audio
│   ├── AudioPlayerComponent.kt    # Lecture audio
│   ├── SouvenirForm.kt            # Formulaire de création
│   └── SouvenirDetailCard.kt      # Affichage des détails
├── models/
│   └── SouvenirItem.kt            # Modèle de données
├── services/
│   ├── FirestoreService.kt        # Service Firebase
│   ├── LocationManager.kt         # Gestion de la localisation
│   ├── CameraManager.kt           # Gestion de l'appareil photo
│   ├── FirebaseStorageService.kt  # Stockage des fichiers
│   ├── AudioRecordingService.kt   # Enregistrement audio
│   ├── AudioPlaybackService.kt    # Lecture audio
│   └── AudioMetadataService.kt    # Métadonnées audio
├── permission/
│   ├── AudioPermission.kt         # Permissions audio
│   ├── CameraPermission.kt        # Permissions caméra
│   └── LocationPermission.kt      # Permissions localisation
├── camera/
│   └── CameraManager.kt           # Gestion de l'appareil photo
├── location/
│   └── LocationManager.kt         # Gestion de la localisation
├── utils/
│   ├── AudioConstants.kt          # Constantes audio
│   ├── LogUtils.kt                # Utilitaires de logging
│   ├── MessageConstants.kt        # Messages constants
│   └── PermissionConstants.kt     # Constantes de permissions
└── theme/
    ├── AppColors.kt               # Palette de couleurs
    ├── Color.kt                   # Couleurs du thème
    ├── ColorUtils.kt              # Utilitaires de couleurs
    ├── Theme.kt                   # Configuration du thème
    └── Type.kt                    # Typographie
```

## 🎵 Fonctionnalités Audio Détaillées

### Enregistrement Audio
- **Format** : MP3 avec qualité optimisée
- **Durée** : Illimitée (selon l'espace disponible)
- **Permissions** : Demande automatique des permissions
- **Interface** : Boutons d'enregistrement, pause et arrêt
- **Feedback visuel** : Indicateurs d'état et niveau sonore

### Lecture Audio
- **Contrôles** : Lecture, pause, arrêt
- **Navigation** : Barre de progression
- **Interface** : Design Material 3 cohérent
- **Gestion des erreurs** : Messages d'erreur informatifs

### Stockage et Synchronisation
- **Stockage local** : Fichiers temporaires pendant l'enregistrement
- **Upload automatique** : Vers Firebase Storage lors de la sauvegarde
- **Métadonnées** : Informations sur la durée, la date, etc.
- **Gestion des erreurs** : Retry automatique en cas d'échec

## 🔧 Configuration de la Qualité du Code

### Fichiers de Configuration

#### `.editorconfig`
```ini
root = true

[*.{kt,kts}]
indent_style = space
indent_size = 4
max_line_length = 120
```

#### `config/detekt/detekt.yml`
- Règles personnalisées pour l'analyse statique
- Seuils configurables pour les violations
- Baseline pour ignorer les problèmes existants

#### `.githooks/pre-commit`
- Vérification automatique avant chaque commit
- Blocage du commit si des violations sont détectées
- Messages d'aide pour corriger les problèmes

### Intégration Continue
- **Git hooks** : Vérification automatique à chaque commit
- **Gradle tasks** : Intégration dans le processus de build
- **Rapports** : Génération automatique de rapports de qualité

## 📋 Fonctionnalités à Implémenter

### Court terme
- [ ] Sauvegarde des paramètres utilisateur
- [ ] Mode sombre fonctionnel
- [ ] Gestion des notifications push
- [ ] Sauvegarde des modifications du profil
- [ ] Amélioration de la qualité audio

### Moyen terme
- [ ] Système de favoris
- [ ] Partage de souvenirs
- [ ] Recherche et filtres
- [ ] Synchronisation hors ligne
- [ ] Édition audio (coupe, effets)

### Long terme
- [ ] Support multi-utilisateurs
- [ ] Système de commentaires
- [ ] Intégration avec les réseaux sociaux
- [ ] Version web
- [ ] Reconnaissance vocale

## 🛠️ Technologies Utilisées

- **Kotlin** : Langage de programmation principal
- **Jetpack Compose** : Framework UI moderne
- **Google Maps** : Affichage de la carte
- **Firebase** : Backend et authentification
- **Material Design 3** : Design system
- **Coroutines** : Programmation asynchrone
- **MediaRecorder** : Enregistrement audio natif
- **MediaPlayer** : Lecture audio native
- **ExoPlayer** : Lecteur audio avancé (optionnel)

## 📚 Ressources et Documentation

### Documentation Officielle
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Google Maps Platform](https://developers.google.com/maps)
- [Firebase Documentation](https://firebase.google.com/docs)

### Outils de Qualité
- [KtLint](https://ktlint.github.io/)
- [Detekt](https://detekt.dev/)
- [EditorConfig](https://editorconfig.org/)

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commiter vos changements
4. Pousser vers la branche
5. Ouvrir une Pull Request

### Standards de Code
- Respecter les règles KtLint
- Passer les vérifications Detekt
- Suivre les conventions Kotlin
- Ajouter des tests unitaires

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## 📞 Contact

Pour toute question ou suggestion, n'hésitez pas à ouvrir une issue sur GitHub.

---

**Note** : Ce README combine toutes les informations des différents fichiers de documentation du projet pour une meilleure lisibilité et maintenance.
