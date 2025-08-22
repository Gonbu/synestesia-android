# Synestesia - Application de Souvenirs Géolocalisés

## 📱 Description

Synestesia est une application Android qui permet aux utilisateurs de créer et gérer des souvenirs géolocalisés. L'application utilise Google Maps pour afficher les souvenirs sur une carte interactive et Firebase pour le stockage des données. **Version simplifiée** - Focus sur la carte et les souvenirs.

## ✨ Fonctionnalités Principales

### 🗺️ Carte Interactive

- **Affichage des souvenirs** sur Google Maps avec marqueurs colorés
- **Géolocalisation en temps réel** avec bouton de retour à la position
- **Création de souvenirs** en cliquant sur la carte
- **Visualisation des souvenirs existants** avec navigation entre eux
- **Interface épurée** sans navigation complexe

### 🎵 Fonctionnalités Audio

- **Enregistrement audio** : Capture d'audio directement dans l'application
- **Lecture audio** : Lecteur intégré avec contrôles (play/pause/stop)
- **Stockage cloud** : Sauvegarde automatique des fichiers audio sur Firebase
- **Gestion des permissions** : Demande automatique des permissions audio
- **Prévisualisation** : Écoute des enregistrements avant sauvegarde

### 📸 Capture Photo

- **Prise de photo intégrée** à l'application
- **Gestion des permissions caméra** automatique
- **Stockage cloud automatique** sur Firebase Storage
- **Aperçu des photos** prises dans le formulaire

### 🎨 Gestion des Souvenirs

- **Création simplifiée** : Titre, description, couleur, photo, audio
- **Palette de couleurs** : 30 couleurs prédéfinies pour personnaliser les souvenirs
- **Stockage sécurisé** : Authentification Firebase anonyme
- **Gestion des médias** : Upload automatique des photos et audios
- **Suppression sécurisée** : Vérification des droits utilisateur

### 🔐 Authentification

- **Connexion anonyme Firebase** automatique au démarrage
- **Sécurité des données** : Chaque utilisateur voit uniquement ses souvenirs
- **Gestion des permissions** : Caméra, audio et localisation

## 🏗️ Architecture Technique

### Composants UI

- `MainNavigation` : Composant principal affichant directement la carte
- `MapContent` : Affichage de la carte et gestion des souvenirs
- `SouvenirForm` : Formulaire de création de souvenirs
- `AudioRecorderComponent` : Composant d'enregistrement audio
- `AudioPlayerComponent` : Composant de lecture audio
- `SouvenirDetailCard` : Affichage des détails des souvenirs

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

#### JaCoCo

- **Couverture de code** avec objectif > 80%
- **Rapports détaillés** de couverture
- **Intégration CI/CD** pour validation automatique
- **Métriques** de qualité du code

### Commandes Disponibles

```bash
# Vérification du style de code
./gradlew ktlintCheck

# Formatage automatique
./gradlew ktlintFormat

# Analyse statique avec detekt
./gradlew detekt

# Génération du rapport de couverture
./gradlew jacocoTestReport

# Nettoyage et rafraîchissement
./gradlew clean --refresh-dependencies
```

## 🚀 Installation et Configuration

### Prérequis

- Android Studio
- JDK 11 ou supérieur
- Compte Google Cloud Platform avec Maps API activée
- Projet Firebase configuré

### Configuration

1. **Clonez le repository**

   ```bash
   git clone https://github.com/billie/synestesia.git
   cd synestesia
   ```

2. **Configurez Firebase**
   - Créez un projet Firebase
   - Téléchargez `google-services.json` dans le dossier `app/`
   - Activez Firestore et Firebase Storage

3. **Configurez Google Maps**
   - Générez une clé API Google Maps
   - Créez le fichier `secrets.properties` :

     ```properties
     MAPS_API_KEY=votre_clé_api_ici
     ```

4. **Synchronisez le projet**

   ```bash
   ./gradlew build
   ```

### Compilation

```bash
# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease

# Build avec tests
./gradlew build

# Tests uniquement
./gradlew test
```

## 📁 Structure des Fichiers

```
synestesia/
├── app/                           # Application principale
│   ├── src/
│   │   ├── main/                 # Code source principal
│   │   │   ├── java/com/billie/synestesia/
│   │   │   │   ├── ui/           # Composants UI Compose
│   │   │   │   ├── models/       # Modèles de données
│   │   │   │   ├── location/     # Gestion de la localisation
│   │   │   │   ├── permission/   # Gestion des permissions
│   │   │   │   └── camera/       # Gestion de la caméra
│   │   │   ├── res/              # Ressources (drawables, values)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                 # Tests unitaires
│   │   └── androidTest/          # Tests d'instrumentation
│   ├── build.gradle.kts          # Configuration de l'app
│   └── proguard-rules.pro        # Règles ProGuard
├── config/                        # Configuration des outils
│   └── detekt/                   # Configuration Detekt
├── gradle/                        # Configuration Gradle
├── build.gradle.kts              # Configuration du projet
└── README.md                     # Ce fichier
```

## 🛠️ Technologies Utilisées

- **Kotlin** : Langage de programmation principal
- **Jetpack Compose** : Framework UI moderne
- **Google Maps** : Affichage de la carte
- **Firebase** : Backend et authentification
- **Material Design 3** : Design system
- **Coroutines** : Programmation asynchrone
- **MediaRecorder** : Enregistrement audio natif
- **MediaPlayer** : Lecture audio native

## 🔒 Sécurité et Permissions

### Permissions Requises

- **CAMERA** : Prise de photos
- **RECORD_AUDIO** : Enregistrement audio
- **ACCESS_FINE_LOCATION** : Géolocalisation précise
- **ACCESS_COARSE_LOCATION** : Géolocalisation approximative
- **WRITE_EXTERNAL_STORAGE** : Sauvegarde temporaire (API ≤ 28)
- **READ_EXTERNAL_STORAGE** : Lecture des fichiers (API ≤ 32)

### Mesures de Sécurité

- **Authentification anonyme** Firebase
- **Isolation des données** par utilisateur
- **Vérification des droits** avant suppression
- **Stockage sécurisé** des fichiers sur Firebase

## 📋 Fonctionnalités à Implémenter

### Court terme

- [ ] Sauvegarde locale des paramètres
- [ ] Gestion des erreurs réseau robuste
- [ ] Cache local des données
- [ ] Pagination des souvenirs

### Moyen terme

- [ ] Système de favoris
- [ ] Recherche et filtres
- [ ] Synchronisation hors ligne
- [ ] Export/Import des données

### Long terme

- [ ] Support multi-utilisateurs
- [ ] Système de commentaires
- [ ] Intégration avec les réseaux sociaux
- [ ] Version web

## 🧪 Tests et Qualité

### Tests Disponibles

- **Tests unitaires** : Validation de la logique métier
- **Tests d'intégration** : Validation des services Firebase
- **Tests de qualité** : KtLint, Detekt, JaCoCo

### Objectifs de Qualité

- **Couverture de code** : > 80%
- **Conformité KtLint** : 100%
- **Violations Detekt** : < 5 critiques
- **Performance** : Temps de réponse < 2s

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
- Maintenir la couverture de code > 80%

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## 📞 Contact et Support

### Support Technique

- **GitHub Issues** : [Signaler un problème](https://github.com/billie/synestesia/issues)
- **Discussions** : [Forum de discussion](https://github.com/billie/synestesia/discussions)

### Documentation

- **Documentation complète** : [docs/README.md](docs/README.md)
- **Manuel utilisateur** : [docs/MANUEL_UTILISATION.md](docs/MANUEL_UTILISATION.md)
- **Guide développeur** : [docs/MANUEL_DEPLOIEMENT.md](docs/MANUEL_DEPLOIEMENT.md)

---

**Note** : Cette version de Synestesia est simplifiée et se concentre sur les fonctionnalités essentielles : carte, création de souvenirs, et gestion des médias. L'interface a été épurée pour une expérience utilisateur plus directe et intuitive.
