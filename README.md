# Synestesia - Application de Souvenirs Géolocalisés

## Description

Synestesia est une application Android qui permet aux utilisateurs de créer, gérer et partager des souvenirs géolocalisés. L'application utilise Google Maps pour afficher les souvenirs sur une carte interactive et Firebase pour le stockage des données.

## Fonctionnalités Principales

### 🗺️ Carte Interactive
- Affichage des souvenirs sur Google Maps
- Géolocalisation en temps réel
- Création de souvenirs en cliquant sur la carte
- Navigation vers les souvenirs existants
- Bouton de retour à la position actuelle

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

## Architecture Technique

### Composants UI
- `MainNavigation` : Composant principal gérant la navigation
- `MapContent` : Affichage de la carte et gestion des souvenirs
- `SettingsScreen` : Écran des paramètres
- `ProfileScreen` : Écran de gestion du profil
- `BottomNavigation` : Barre de navigation en bas

### Services
- `FirestoreService` : Gestion des données Firebase
- `LocationManager` : Gestion de la géolocalisation
- `CameraManager` : Gestion de l'appareil photo
- `FirebaseStorageService` : Stockage des fichiers

### Modèles
- `SouvenirItem` : Structure des données des souvenirs

## Installation et Configuration

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

## Structure des Fichiers

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
│   └── ...                        # Autres composants UI
├── models/
│   └── SouvenirItem.kt            # Modèle de données
├── services/
│   ├── FirestoreService.kt        # Service Firebase
│   ├── LocationManager.kt         # Gestion de la localisation
│   └── ...                        # Autres services
└── ...
```

## Fonctionnalités à Implémenter

### Court terme
- [ ] Sauvegarde des paramètres utilisateur
- [ ] Mode sombre fonctionnel
- [ ] Gestion des notifications push
- [ ] Sauvegarde des modifications du profil

### Moyen terme
- [ ] Système de favoris
- [ ] Partage de souvenirs
- [ ] Recherche et filtres
- [ ] Synchronisation hors ligne

### Long terme
- [ ] Support multi-utilisateurs
- [ ] Système de commentaires
- [ ] Intégration avec les réseaux sociaux
- [ ] Version web

## Technologies Utilisées

- **Kotlin** : Langage de programmation principal
- **Jetpack Compose** : Framework UI moderne
- **Google Maps** : Affichage de la carte
- **Firebase** : Backend et authentification
- **Material Design 3** : Design system
- **Coroutines** : Programmation asynchrone

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commiter vos changements
4. Pousser vers la branche
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## Contact

Pour toute question ou suggestion, n'hésitez pas à ouvrir une issue sur GitHub.
