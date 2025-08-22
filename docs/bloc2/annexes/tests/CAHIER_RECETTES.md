# Cahier de Recettes - Synestesia

## 🎯 Objectif

Ce document décrit les scénarios de test et critères d'acceptation pour l'application Synestesia, permettant de valider le bon fonctionnement des fonctionnalités principales.

## 📋 Scénarios de Test

### 1. Création d'un Souvenir Complet

#### **Scénario : Création d'un souvenir avec photo et audio**

```
Étant donné que l'utilisateur est sur la carte principale
Et qu'il a accordé les permissions caméra et audio
Quand il tape sur la carte à un endroit spécifique
Et qu'il remplit le formulaire de création
Et qu'il prend une photo
Et qu'il enregistre un audio
Et qu'il sauvegarde le souvenir
Alors le souvenir est créé avec succès
Et il apparaît sur la carte
Et il est sauvegardé dans Firebase
```

#### **Critères d'Acceptation**

- [ ] Le tap sur la carte ouvre le formulaire de création
- [ ] Le formulaire contient tous les champs requis
- [ ] La prise de photo fonctionne correctement
- [ ] L'enregistrement audio fonctionne correctement
- [ ] La sauvegarde se fait sans erreur
- [ ] Le souvenir apparaît immédiatement sur la carte
- [ ] Les données sont persistées dans Firebase

#### **Résultats Attendus**

- Formulaire de création s'ouvre
- Photo capturée et affichée
- Audio enregistré et jouable
- Souvenir visible sur la carte
- Données sauvegardées en base

---

### 2. Affichage et Navigation des Souvenirs

#### **Scénario : Consultation d'un souvenir existant**

```
Étant donné qu'il existe des souvenirs sur la carte
Quand l'utilisateur tape sur un marqueur de souvenir
Alors les détails du souvenir s'affichent
Et l'utilisateur peut voir la photo
Et l'utilisateur peut écouter l'audio
Et l'utilisateur peut modifier le souvenir
```

#### **Critères d'Acceptation**

- [ ] Les marqueurs de souvenirs sont visibles sur la carte
- [ ] Le tap sur un marqueur ouvre la carte de détail
- [ ] La photo du souvenir s'affiche correctement
- [ ] L'audio se lance au clic sur play
- [ ] Les boutons d'édition sont fonctionnels
- [ ] La navigation retour fonctionne

#### **Résultats Attendus**

- Carte de détail s'ouvre
- Photo affichée en haute qualité
- Lecteur audio fonctionnel
- Boutons d'action disponibles
- Navigation fluide

---

### 3. Gestion des Permissions

#### **Scénario : Demande des permissions nécessaires**

```
Étant donné que l'utilisateur ouvre l'application pour la première fois
Quand il tente d'utiliser la caméra ou l'audio
Alors une demande de permission s'affiche
Et l'utilisateur peut accepter ou refuser
Et l'application gère gracieusement le refus
```

#### **Critères d'Acceptation**

- [ ] Les permissions sont demandées au bon moment
- [ ] Les explications sont claires pour l'utilisateur
- [ ] L'application fonctionne même sans certaines permissions
- [ ] Les permissions refusées sont gérées proprement
- [ ] L'utilisateur peut réactiver les permissions plus tard

#### **Résultats Attendus**

- Demande de permission contextuelle
- Explication claire de l'utilité
- Fonctionnement dégradé si refus
- Accès aux paramètres de permission

---

### 4. Synchronisation et Hors-ligne

#### **Scénario : Synchronisation des données**

```
Étant donné que l'utilisateur crée un souvenir hors-ligne
Et qu'il se reconnecte à Internet
Quand l'application se synchronise
Alors le souvenir est uploadé vers Firebase
Et il apparaît sur tous ses appareils
```

#### **Critères d'Acceptation**

- [ ] Les souvenirs sont créés même hors-ligne
- [ ] La synchronisation se fait automatiquement
- [ ] Les conflits de données sont gérés
- [ ] L'utilisateur est informé du statut de sync
- [ ] Les données sont cohérentes entre appareils

#### **Résultats Attendus**

- Création hors-ligne possible
- Synchronisation automatique
- Gestion des conflits
- Statut de sync visible

---

### 5. Performance et Utilisabilité

#### **Scénario : Performance de l'application**

```
Étant donné que l'utilisateur navigue dans l'application
Quand il ouvre la carte avec de nombreux souvenirs
Alors la carte se charge en moins de 3 secondes
Et la navigation reste fluide
Et l'application ne plante pas
```

#### **Critères d'Acceptation**

- [ ] Temps de chargement initial < 3 secondes
- [ ] Navigation fluide (60 FPS)
- [ ] Pas de crash de l'application
- [ ] Gestion mémoire optimisée
- [ ] Taille APK < 50MB

#### **Résultats Attendus**

- Chargement rapide
- Interface réactive
- Stabilité garantie
- Performance optimale

## 🐛 Plan de Correction des Anomalies

### Tableau de Suivi

| Anomalie Détectée | Priorité | Correctif Prévu | État | Responsable |
|-------------------|----------|-----------------|------|-------------|
| **Crash lors de la prise de photo** | 🔴 Critique | Vérification des permissions caméra | 🔄 En cours | Dev Mobile |
| **Audio qui ne se lance pas** | 🟡 Élevée | Correction du MediaPlayer | ⏳ Planifié | Dev Audio |
| **Carte qui ne se charge pas** | 🔴 Critique | Vérification de l'API Google Maps | ✅ Résolu | Dev Backend |
| **Synchronisation lente** | 🟢 Faible | Optimisation des requêtes Firebase | 📋 Backlog | Dev Backend |
| **Interface qui lag** | 🟡 Élevée | Optimisation des composants Compose | 🔄 En cours | Dev UI |
| **Permissions qui ne se demandent pas** | 🔴 Critique | Correction du système de permissions | ⏳ Planifié | Dev Mobile |
| **Données qui se perdent** | 🔴 Critique | Amélioration de la persistance locale | 🔄 En cours | Dev Data |
| **APK trop volumineux** | 🟢 Faible | Optimisation des ressources | 📋 Backlog | Dev Build |

### Légende des Priorités

- 🔴 **Critique** : Bloque l'utilisation de l'application
- 🟡 **Élevée** : Impact significatif sur l'expérience utilisateur
- 🟢 **Faible** : Amélioration souhaitable mais non bloquante

### Légende des États

- ✅ **Résolu** : Anomalie corrigée et testée
- 🔄 **En cours** : Correction en développement
- ⏳ **Planifié** : Correction planifiée pour la prochaine itération
- 📋 **Backlog** : Ajouté au backlog pour traitement futur

## 📊 Métriques de Qualité

### Critères de Performance

- **Temps de chargement initial** : < 3 secondes
- **Temps de réponse UI** : < 100ms
- **Taille APK** : < 50MB
- **Taux de crash** : < 0.1%

### Critères de Fonctionnalité

- **Taux de succès des opérations** : > 99%
- **Couverture des tests** : > 80%
- **Accessibilité** : Conformité WCAG 2.1 AA
- **Sécurité** : Aucune vulnérabilité critique

## 🧪 Procédures de Test

### Tests Automatisés

```bash
# Lancer tous les tests
./gradlew test

# Tests avec couverture JaCoCo
./gradlew jacocoTestReport

# Tests d'instrumentation
./gradlew connectedAndroidTest
```

### Tests Manuels

1. **Test de création** : Créer un souvenir complet
2. **Test de consultation** : Consulter et modifier des souvenirs
3. **Test des permissions** : Tester tous les cas de permission
4. **Test de performance** : Mesurer les temps de réponse
5. **Test d'accessibilité** : Valider avec TalkBack

### Tests de Régression

- Vérifier que les corrections n'introduisent pas de nouveaux bugs
- Valider que les fonctionnalités existantes fonctionnent toujours
- Tester sur différents appareils et versions Android

## 📝 Notes de Test

### Environnements de Test

- **Développement** : Émulateur API 24-35
- **Staging** : Appareils physiques de test
- **Production** : Tests sur appareils réels

### Données de Test

- Utiliser des données de test non sensibles
- Créer des scénarios de test variés
- Tester avec des données volumineuses

### Reporting

- Documenter tous les bugs trouvés
- Capturer des captures d'écran si nécessaire
- Noter les étapes de reproduction
- Prioriser selon l'impact utilisateur
