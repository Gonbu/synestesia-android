# Fonctionnalité Audio - Synestesia

## Vue d'ensemble

La fonctionnalité audio permet aux utilisateurs d'enregistrer et de stocker des fichiers audio (ambiance sonore, voix, etc.) lors de la création de souvenirs, enrichissant ainsi l'expérience mémorielle.

## Fonctionnalités

### 1. Enregistrement Audio
- **Enregistrement en temps réel** : Capture audio via le microphone de l'appareil
- **Contrôles intuitifs** : Bouton d'enregistrement circulaire avec indicateur visuel
- **Timer d'enregistrement** : Affichage du temps d'enregistrement en cours
- **Gestion des permissions** : Demande automatique des permissions audio nécessaires

### 2. Stockage et Gestion
- **Stockage local temporaire** : Fichiers audio stockés localement pendant l'enregistrement
- **Upload Firebase Storage** : Transfert automatique vers Firebase Storage
- **Organisation hiérarchique** : Structure de dossiers organisée par utilisateur et souvenir
- **Intégration Firestore** : URLs audio stockées dans la base de données Firestore

### 3. Lecture Audio
- **Lecteur intégré** : Contrôles de lecture (play/pause/stop) dans l'interface
- **Barre de progression** : Indicateur visuel de la progression de la lecture
- **Affichage du temps** : Temps écoulé et durée totale
- **Interface responsive** : Adaptation automatique à l'état de lecture

## Architecture Technique

### Composants Principaux

#### 1. AudioRecordingService
```kotlin
object AudioRecordingService {
    fun startRecording(context: Context): String?
    fun stopRecording(): String?
    fun isRecording(): Boolean
    fun cleanup()
}
```
- Gestion de l'enregistrement audio via MediaRecorder
- Création automatique de fichiers temporaires
- Gestion des ressources et nettoyage

#### 2. FirebaseStorageService
```kotlin
suspend fun uploadSouvenirAudio(audioFilePath: String, souvenirId: String): String?
```
- Upload des fichiers audio vers Firebase Storage
- Organisation hiérarchique des fichiers
- Gestion des erreurs et validation

#### 3. FirestoreService
```kotlin
suspend fun updateSouvenirAudio(souvenirId: String, audioUrl: String)
```
- Mise à jour des URLs audio dans Firestore
- Vérification des permissions utilisateur
- Gestion des erreurs de base de données

### Composants UI

#### 1. AudioRecorderComponent
- Interface d'enregistrement avec bouton circulaire
- Timer d'enregistrement en temps réel
- Aperçu de l'audio enregistré
- Gestion des permissions

#### 2. AudioPlayerComponent
- Lecteur audio avec contrôles complets
- Barre de progression
- Affichage du temps
- Gestion des états de lecture

## Permissions Requises

### Android Manifest
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />
```

### Gestion des Permissions
- **AudioPermission** : Vérification des permissions audio
- **AudioPermissionLauncher** : Demande de permissions via l'API Compose
- **Gestion automatique** : Demande des permissions au moment de l'utilisation

## Modèle de Données

### SouvenirItem
```kotlin
data class SouvenirItem(
    // ... autres champs
    val audio: String = "", // URL du fichier audio
    // ... autres champs
)
```

### Structure Firebase Storage
```
souvenirs/
├── {userId}/
│   ├── {souvenirId}/
│   │   ├── {photoFileName}
│   │   └── {audioFileName}
│   └── ...
```

## Flux d'Utilisation

### 1. Création d'un Souvenir avec Audio
1. L'utilisateur remplit le formulaire de souvenir
2. Il clique sur le bouton d'enregistrement audio
3. L'application demande la permission audio si nécessaire
4. L'enregistrement démarre avec timer visuel
5. L'utilisateur arrête l'enregistrement
6. L'audio est temporairement stocké localement
7. Le souvenir est créé dans Firestore
8. L'audio est uploadé vers Firebase Storage
9. L'URL audio est mise à jour dans Firestore

### 2. Lecture d'un Souvenir avec Audio
1. L'utilisateur ouvre la carte de détail d'un souvenir
2. Si un audio est disponible, le lecteur audio s'affiche
3. L'utilisateur peut contrôler la lecture (play/pause/stop)
4. La progression est affichée en temps réel

## Gestion des Erreurs

### Types d'Erreurs
- **Permission refusée** : Message d'erreur et redemande de permission
- **Échec d'enregistrement** : Logs détaillés et message utilisateur
- **Échec d'upload** : Gestion gracieuse avec retry possible
- **Échec Firestore** : Rollback et notification utilisateur

### Logs et Debugging
- Utilisation de `LogUtils` pour la traçabilité
- Messages d'erreur localisés et informatifs
- Gestion des exceptions avec contexte détaillé

## Considérations de Performance

### Optimisations
- **Enregistrement en arrière-plan** : Utilisation de coroutines
- **Gestion mémoire** : Libération automatique des ressources MediaRecorder
- **Upload asynchrone** : Non-bloquant pour l'interface utilisateur
- **Cache local** : Fichiers temporaires pour éviter les re-uploads

### Limitations
- Taille maximale des fichiers audio (dépend de Firebase Storage)
- Durée maximale d'enregistrement (dépend de l'espace disque)
- Qualité audio (dépend des capacités de l'appareil)

## Tests et Validation

### Tests Recommandés
- Test des permissions audio sur différents appareils
- Validation de l'enregistrement et de la lecture
- Test de l'upload vers Firebase Storage
- Validation de l'intégration Firestore
- Test de gestion des erreurs

### Scénarios de Test
- Enregistrement avec permission accordée
- Enregistrement avec permission refusée
- Enregistrement long (> 5 minutes)
- Upload avec connexion instable
- Lecture audio avec différents formats

## Maintenance et Évolutions

### Améliorations Futures
- **Compression audio** : Réduction de la taille des fichiers
- **Formats multiples** : Support de différents codecs audio
- **Édition audio** : Découpage et filtres
- **Partage audio** : Export et partage des enregistrements
- **Synchronisation** : Backup et restauration des audios

### Monitoring
- Métriques d'utilisation des fonctionnalités audio
- Surveillance de la qualité des uploads
- Alertes en cas d'échecs répétés
- Analyse des performances d'enregistrement

## Support et Dépannage

### Problèmes Courants
1. **Permission refusée** : Vérifier les paramètres de l'application
2. **Échec d'enregistrement** : Vérifier l'espace disque et les permissions
3. **Upload échoué** : Vérifier la connexion internet et Firebase
4. **Lecture impossible** : Vérifier le format et l'intégrité du fichier

### Ressources de Développement
- Documentation MediaRecorder Android
- Guide Firebase Storage
- API Compose Permissions
- Bonnes pratiques Material Design 3
