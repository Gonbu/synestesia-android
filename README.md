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

### Configuration des Éditeurs

#### Android Studio / IntelliJ IDEA

- Plugin KtLint installé automatiquement
- Formatage automatique à la sauvegarde
- Intégration avec l'éditeur

#### VS Code

- Configuration dans `.vscode/settings.json`
- Extensions recommandées pour Kotlin

## 🚀 CI/CD et Déploiement

### Pipeline GitHub Actions

- **Build automatique** : Compilation continue
- **Tests automatiques** : Validation de la qualité
- **Analyse de sécurité** : Scan Trivy automatique
- **Déploiement automatique** : Firebase App Distribution
- **Cache Gradle** : Optimisation des performances

### Environnements

- **Développement** : Tests locaux et intégration
- **Staging** : Tests de validation et tests utilisateur
- **Production** : Déploiement final et monitoring

### Métriques de Déploiement

- **Temps de build** : < 10 minutes
- **Taux de succès** : > 99%
- **Temps de déploiement** : < 5 minutes
- **Rollback** : < 10 minutes

## 🧪 Tests et Qualité

### Tests Unitaires

- **JUnit 5** : Framework de tests moderne
- **MockK** : Mocking des dépendances
- **Coroutines Test** : Tests des opérations asynchrones
- **Turbine** : Tests des flows et channels
- **Couverture** : Objectif > 80%

### Tests d'Intégration

- **Tests Firebase** : Validation des services cloud
- **Tests de composants** : Validation des interactions
- **Tests de navigation** : Validation des parcours utilisateur

### Tests de Qualité

- **Tests de performance** : Validation des temps de réponse
- **Tests d'accessibilité** : Validation WCAG 2.1 AA
- **Tests de sécurité** : Validation des mesures de sécurité

## 🔒 Sécurité et Accessibilité

### Mesures de Sécurité

- **OWASP Top 10 Mobile** : Conformité complète
- **Authentification Firebase** : Gestion sécurisée des sessions
- **Chiffrement TLS 1.3** : Communications sécurisées
- **Permissions minimales** : Principe du moindre privilège
- **Scan automatique** : Détection des vulnérabilités

### Accessibilité

- **WCAG 2.1 AA** : Conformité aux standards internationaux
- **TalkBack** : Support complet du lecteur d'écran
- **Navigation clavier** : Support complet des alternatives
- **Contraste** : Ratio minimum 4.5:1
- **Tailles cibles** : Minimum 48dp x 48dp

## 📚 Documentation Complète

### 📖 Documentation Technique

- **[Manuel de Déploiement](docs/MANUEL_DEPLOIEMENT.md)** - Guide complet du processus de build et déploiement
- **[Protocole de Déploiement](docs/PROTOCOLE_DEPLOIEMENT.md)** - Procédures et règles de déploiement
- **[Critères de Qualité](docs/CRITERES_QUALITE.md)** - Métriques et objectifs de qualité

### 🧪 Documentation de Test

- **[Cahier de Recettes](docs/CAHIER_RECETTES.md)** - Scénarios de test et critères d'acceptation
- **[Guide de Test](docs/README.md)** - Vue d'ensemble de la documentation

### 🔒 Documentation de Sécurité

- **[Guide de Sécurité](docs/SECURITY.md)** - Mesures de sécurité OWASP Top 10 Mobile
- **[Guide d'Accessibilité](docs/ACCESSIBILITY.md)** - Conformité WCAG 2.1 AA

### 👥 Documentation Utilisateur

- **[Manuel d'Utilisation](docs/MANUEL_UTILISATION.md)** - Guide complet pour les utilisateurs finaux
- **[Changelog](CHANGELOG.md)** - Historique des versions et changements

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
# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease

# Build avec tests
./gradlew build

# Tests uniquement
./gradlew test
```

### Installation

```bash
# Installer debug
./gradlew installDebug

# Installer release
./gradlew installRelease
```

## 📁 Structure des Fichiers

```
synestesia/
├── app/                           # Application principale
│   ├── src/
│   │   ├── main/                 # Code source principal
│   │   ├── test/                 # Tests unitaires
│   │   └── androidTest/          # Tests d'instrumentation
│   ├── build.gradle.kts          # Configuration de l'app
│   └── proguard-rules.pro        # Règles ProGuard
├── docs/                          # Documentation complète
│   ├── README.md                 # Index de documentation
│   ├── MANUEL_DEPLOIEMENT.md     # Guide de déploiement
│   ├── PROTOCOLE_DEPLOIEMENT.md  # Protocole de déploiement
│   ├── CRITERES_QUALITE.md       # Critères de qualité
│   ├── CAHIER_RECETTES.md        # Scénarios de test
│   ├── SECURITY.md               # Guide de sécurité
│   ├── ACCESSIBILITY.md          # Guide d'accessibilité
│   └── MANUEL_UTILISATION.md     # Manuel utilisateur
├── .github/                       # Configuration GitHub
│   └── workflows/                # Pipelines CI/CD
├── config/                        # Configuration des outils
│   └── detekt/                   # Configuration Detekt
├── gradle/                        # Configuration Gradle
├── build.gradle.kts              # Configuration du projet
├── CHANGELOG.md                  # Historique des versions
└── README.md                     # Ce fichier
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
- [JaCoCo](https://www.jacoco.org/jacoco/)
- [EditorConfig](https://editorconfig.org/)

### Standards et Références

- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)

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

### Processus de Contribution

- **Issues** : Signaler les bugs et proposer des améliorations
- **Pull Requests** : Soumettre des contributions
- **Code Review** : Validation par l'équipe
- **Tests** : Validation automatique et manuelle
- **Documentation** : Mise à jour de la documentation

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## 📞 Contact et Support

### Support Technique

- **GitHub Issues** : [Signaler un problème](https://github.com/billie/synestesia/issues)
- **Discussions** : [Forum de discussion](https://github.com/billie/synestesia/discussions)
- **Email** : <support@synestesia.app>

### Documentation

- **Documentation complète** : [docs/README.md](docs/README.md)
- **Manuel utilisateur** : [docs/MANUEL_UTILISATION.md](docs/MANUEL_UTILISATION.md)
- **Guide développeur** : [docs/MANUEL_DEPLOIEMENT.md](docs/MANUEL_DEPLOIEMENT.md)

### Communauté

- **Discord** : [Rejoindre la communauté](https://discord.gg/synestesia)
- **Twitter** : [@synestesia_app](https://twitter.com/synestesia_app)
- **Blog** : [blog.synestesia.app](https://blog.synestesia.app)

---

**Note** : Ce README combine toutes les informations des différents fichiers de documentation du projet pour une meilleure lisibilité et maintenance. Pour une documentation complète et détaillée, consultez le dossier [docs/](docs/README.md).
