# Workflows CI/CD pour Firebase App Distribution

Ce document décrit comment configurer et utiliser les workflows CI/CD pour automatiser le déploiement vers Firebase App Distribution.

## 🚀 Workflows disponibles

### 1. GitHub Actions

**Fichier** : `.github/workflows/firebase-app-distribution.yml`

**Déclencheurs** :

- Push sur `main` et `develop`
- Tags commençant par `v*` (ex: `v1.0.0`)
- Pull requests sur `main`
- Déclenchement manuel avec choix du type de build

**Fonctionnalités** :

- Build automatique des APKs debug et release
- Déploiement automatique vers Firebase App Distribution
- Cache Gradle pour optimiser les performances
- Support JDK 21 et Android SDK

**Configuration requise** :

```yaml
# Dans GitHub Secrets
GOOGLE_SERVICES_JSON: "base64_encoded_google_services.json"
```

### 2. GitLab CI

**Fichier** : `.gitlab-ci.yml`

**Déclencheurs** :

- Push sur `develop` et `merge_requests` → Build et déploiement debug
- Push sur `main` et tags → Build et déploiement release

**Fonctionnalités** :

- Pipeline en 2 étapes : build puis deploy
- Cache Gradle et build
- Environnements séparés (development/production)
- Artifacts avec expiration

**Configuration requise** :

```yaml
# Dans GitLab Variables
GOOGLE_SERVICES_JSON: "base64_encoded_google_services.json"
```

### 3. Script Local Avancé

**Fichier** : `scripts/deploy-local.sh`

**Fonctionnalités** :

- Déploiement local avec options avancées
- Mode dry-run pour simulation
- Skip build pour utiliser un APK existant
- Vérifications de configuration
- Notifications optionnelles

**Usage** :

```bash
# Déploiement debug normal
./scripts/deploy-local.sh debug

# Simulation d'un déploiement release
./scripts/deploy-local.sh release --dry-run

# Déploiement d'un APK existant
./scripts/deploy-local.sh debug --skip-build

# Déploiement avec notification
./scripts/deploy-local.sh release --notify

# Aide
./scripts/deploy-local.sh --help
```

## 🔧 Configuration des Secrets

### GitHub Actions

1. **Encoder le fichier google-services.json** :

   ```bash
   base64 -i app/google-services.json
   ```

2. **Ajouter le secret dans GitHub** :
   - Repository → Settings → Secrets and variables → Actions
   - Nouveau repository secret : `GOOGLE_SERVICES_JSON`
   - Coller la valeur base64

### GitLab CI

1. **Encoder le fichier google-services.json** :

   ```bash
   base64 -i app/google-services.json
   ```

2. **Ajouter la variable dans GitLab** :
   - Repository → Settings → CI/CD → Variables
   - Nouvelle variable : `GOOGLE_SERVICES_JSON`
   - Coller la valeur base64
   - Cocher "Protected" et "Masked"

## 📱 Types de Déploiement

### Debug Builds

- **Déclencheurs** : Push sur develop, PRs, tags
- **Utilisation** : Tests de développement, validation de fonctionnalités
- **Groupes** : developers, testers

### Release Builds

- **Déclencheurs** : Tags, push sur main
- **Utilisation** : Tests de production, validation finale
- **Groupes** : testers, qa
- **Note** : Nécessite une configuration de signature

## 🔐 Authentification Firebase

### Option 1 : Authentification automatique (recommandée)

- Les workflows utilisent les credentials Firebase CLI
- Première exécution : authentification interactive
- Credentials mis en cache automatiquement

### Option 2 : Compte de service

- Créer un compte de service dans Firebase Console
- Télécharger le fichier JSON
- Ajouter dans les secrets CI/CD

## 📊 Monitoring et Notifications

### GitHub Actions

- Notifications automatiques dans l'interface GitHub
- Status checks sur les PRs
- Logs détaillés dans l'onglet Actions

### GitLab CI

- Notifications dans l'interface GitLab
- Status dans les merge requests
- Logs détaillés dans l'onglet CI/CD

### Notifications personnalisées

- Slack webhooks
- Email notifications
- Webhook personnalisés

## 🚨 Gestion des Erreurs

### Erreurs communes

1. **APK non trouvé** : Vérifier la configuration de build
2. **Authentification Firebase** : Vérifier les credentials
3. **Groupes inexistants** : Créer les groupes dans Firebase Console
4. **APK non signé** : Configurer la signature pour les releases

### Récupération automatique

- Retry automatique en cas d'échec réseau
- Fallback sur les builds précédents
- Notifications d'échec

## 🔄 Workflow de Développement Recommandé

### 1. Développement quotidien

```bash
# Push sur develop → Déploiement debug automatique
git push origin develop
```

### 2. Validation de fonctionnalités

```bash
# PR sur main → Déploiement debug automatique
# Test de l'APK par l'équipe
```

### 3. Release

```bash
# Tag de version → Déploiement release automatique
git tag v1.0.0
git push origin v1.0.0
```

### 4. Déploiement manuel

```bash
# GitHub Actions → Run workflow → Firebase App Distribution
# Choisir le type de build
```

## 📈 Optimisations

### Performance

- Cache Gradle entre les builds
- Cache des dépendances Android
- Builds parallèles si possible

### Sécurité

- Secrets chiffrés
- Variables masquées
- Accès restreint aux environnements

### Monitoring

- Métriques de build
- Temps de déploiement
- Taux de succès

## 🆘 Support et Dépannage

### Logs utiles

- Logs de build Gradle
- Logs Firebase App Distribution
- Logs CI/CD

### Commandes de diagnostic

```bash
# Vérifier la configuration locale
./gradlew :app:appDistributionUploadDebug --dry-run

# Vérifier les tâches disponibles
./gradlew :app:tasks --group="firebase"

# Test du script local
./scripts/deploy-local.sh debug --dry-run
```

### Ressources

- [Documentation Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitLab CI Documentation](https://docs.gitlab.com/ee/ci/)
