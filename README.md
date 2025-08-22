# Synestesia - Application de Souvenirs Géolocalisés

## 📱 Description

Synestesia est une application Android qui permet aux utilisateurs de créer et partager des souvenirs géolocalisés. L'application utilise Google Maps pour la géolocalisation et Firebase pour le stockage et la distribution des builds.

## 🚀 Fonctionnalités

- **Géolocalisation** : Intégration complète avec Google Maps
- **Gestion des souvenirs** : Création, édition et partage de souvenirs
- **Interface moderne** : UI construite avec Jetpack Compose
- **Distribution automatisée** : Firebase App Distribution pour les tests
- **CI/CD intégré** : Workflows automatisés pour le déploiement

## 🏗️ Architecture

- **Frontend** : Jetpack Compose + Material 3
- **Backend** : Firebase (Firestore, Storage, Auth, Database)
- **Maps** : Google Maps Platform
- **Build** : Gradle + Android Gradle Plugin
- **Distribution** : Firebase App Distribution
- **CI/CD** : GitHub Actions + GitLab CI

## 🔧 Configuration Firebase App Distribution

### Prérequis

- Projet Firebase configuré
- Google Services JSON configuré
- Groupes de testeurs créés dans Firebase Console

### Installation

1. **Cloner le repository**

   ```bash
   git clone https://github.com/yourusername/synestesia.git
   cd synestesia
   ```

2. **Vérifier la configuration**

   ```bash
   ./scripts/test-ci-cd.sh
   ```

3. **Tester le déploiement local**

   ```bash
   ./scripts/deploy-local.sh debug --dry-run
   ```

## 📦 Déploiement

### Déploiement Local

```bash
# Déploiement debug
./scripts/deploy-local.sh debug

# Déploiement release
./scripts/deploy-local.sh release

# Mode simulation
./scripts/deploy-local.sh debug --dry-run

# Utiliser un APK existant
./scripts/deploy-local.sh debug --skip-build
```

### Déploiement Automatique

- **Push sur `develop`** → Déploiement debug automatique
- **Pull Request sur `main`** → Déploiement debug automatique
- **Tag de version** → Déploiement release automatique
- **Déclenchement manuel** → Choix du type de build

## 🔄 Workflows CI/CD

### GitHub Actions

- **Fichier** : `.github/workflows/firebase-app-distribution.yml`
- **Déclencheurs** : Push, PR, Tags, Manuel
- **Environnements** : Debug, Release

### GitLab CI

- **Fichier** : `.gitlab-ci.yml`
- **Pipeline** : Build → Deploy
- **Environnements** : Development, Production

## 📚 Documentation

- **Firebase App Distribution** : `docs/FIREBASE_APP_DISTRIBUTION.md`
- **Workflows CI/CD** : `docs/CI_CD_WORKFLOWS.md`
- **Configuration CI/CD** : `CI_CD_SETUP.md`

## 🧪 Tests

### Tests Locaux

```bash
# Vérifier la configuration
./scripts/test-ci-cd.sh

# Tester la compilation
./gradlew :app:assembleDebug --dry-run

# Tester le déploiement
./gradlew :app:appDistributionUploadDebug --dry-run
```

### Tests CI/CD

1. **Créer une branche de test**

   ```bash
   git checkout -b test-ci-cd
   echo "# Test CI/CD" >> README.md
   git add README.md
   git commit -m "test: Test CI/CD workflow"
   git push origin test-ci-cd
   ```

2. **Créer une Pull Request**
   - Le workflow se déclenche automatiquement
   - Vérifier les logs dans l'onglet Actions

## 🔐 Configuration des Secrets

### GitHub Actions

1. **Settings** → **Secrets and variables** → **Actions**
2. **New repository secret** : `GOOGLE_SERVICES_JSON`
3. **Valeur** : Contenu base64 de `app/google-services.json`

### GitLab CI

1. **Settings** → **CI/CD** → **Variables**
2. **Add variable** : `GOOGLE_SERVICES_JSON`
3. **Cocher** : Protected, Masked

## 📱 Groupes de Testeurs

L'application est configurée pour distribuer aux groupes suivants :

- **`testers`** : Testeurs généraux
- **`developers`** : Équipe de développement
- **`qa`** : Équipe de test qualité

## 🚨 Résolution des Problèmes

### Erreurs Communes

1. **APK non trouvé** : Vérifier la configuration de build
2. **Authentification Firebase** : Vérifier les credentials
3. **Groupes inexistants** : Créer les groupes dans Firebase Console
4. **APK non signé** : Configurer la signature pour les releases

### Support

- **Logs CI/CD** : Vérifier dans GitHub Actions ou GitLab CI
- **Tests locaux** : Utiliser `./scripts/deploy-local.sh`
- **Documentation** : Consulter les fichiers dans `docs/`

## 📊 Monitoring

### Métriques

- **Temps de build** : Objectif < 5 minutes
- **Taux de succès** : Objectif > 95%
- **Temps de déploiement** : Objectif < 2 minutes

### Notifications

- **Slack** : Webhook configurable
- **Email** : SMTP configurable
- **Discord/Teams** : Webhooks supportés

## 🎯 Prochaines Étapes

1. **Configurer les secrets** dans GitHub/GitLab
2. **Tester les workflows** avec des branches de test
3. **Configurer les notifications** (Slack, email)
4. **Optimiser les performances** (cache, builds parallèles)
5. **Configurer la signature** pour les APKs release de production

## 🤝 Contribution

1. **Fork le repository**
2. **Créer une branche feature** (`git checkout -b feature/amazing-feature`)
3. **Commit les changements** (`git commit -m 'feat: Add amazing feature'`)
4. **Push vers la branche** (`git push origin feature/amazing-feature`)
5. **Créer une Pull Request**

## 📄 Licence

Ce projet est sous licence [MIT](LICENSE).

## 🔗 Liens Utiles

- **Firebase Console** : [synestesia-a0ea7](https://console.firebase.google.com/project/synestesia-a0ea7)
- **App Distribution** : [Releases](https://console.firebase.google.com/project/synestesia-a0ea7/appdistribution)
- **Documentation Firebase** : [App Distribution](https://firebase.google.com/docs/app-distribution)
- **GitHub Actions** : [Documentation](https://docs.github.com/en/actions)
- **GitLab CI** : [Documentation](https://docs.gitlab.com/ee/ci/)

---

**Développé avec ❤️ par l'équipe Synestesia**
