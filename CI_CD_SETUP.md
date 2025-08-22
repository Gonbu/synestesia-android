# 🔧 Configuration des Workflows CI/CD - Guide Étape par Étape

## 📋 Prérequis

✅ **Firebase App Distribution** configuré et fonctionnel  
✅ **Scripts de déploiement** créés et testés  
✅ **Workflows CI/CD** créés dans le projet  

## 🚀 Configuration GitHub Actions

### Étape 1 : Ajouter le Secret GOOGLE_SERVICES_JSON

1. **Aller dans votre repository GitHub**
2. **Settings** → **Secrets and variables** → **Actions**
3. **New repository secret**
4. **Nom** : `GOOGLE_SERVICES_JSON`
5. **Valeur** : Coller le contenu base64 ci-dessous

```
ewogICJwcm9qZWN0X2luZm8iOiB7CiAgICAicHJvamVjdF9udW1iZXIiOiAiMzg5MjY5NDM0MTQ1IiwKICAgICJwcm9qZWN0X2lkIjogInN5bmVzdGVzaWEtYTBlYTciLAogICAgInN0b3JhZ2VfYnVja2V0IjogInN5bmVzdGVzaWEtYTBlYTcuZmlyZWJhc2VzdG9yYWdlLmFwcCIKICB9LAogICJjbGllbnQiOiBbCiAgICB7CiAgICAgICJjbGllbnRfaW5mbyI6IHsKICAgICAgICAibW9iaWxlc2RrX2FwcF9pZCI6ICIxOjM4OTI2OTQzNDE0NTphbmRyb2lkOjBlNDlhZTg4MGVkMjVjOGM2ZWQ4NjAiLAogICAgICAgICJhbmRyb2lkX2NsaWVudF9pbmZvIjogewogICAgICAgICAgInBhY2thZ2VfbmFtZSI6ICJjb20uYmlsbGllLnN5bmVzdGVzaWEiCiAgICAgICAgfQogICAgICB9LAogICAgICAib2F1dGhfY2xpZW50IjogW10sCiAgICAgICJhcGlfa2V5IjogWwogICAgICAgIHsKICAgICAgICAgICJjdXJyZW50X2tleSI6ICJBSXphU3lEcDJFZnUweDY0b0xZOUk0MkV0UzNJeS1VOS12NnozQ28iCiAgICAgICAgfQogICAgICBdLAogICAgICAic2VydmljZXMiOiB7CiAgICAgICAgImFwcGludml0ZV9zZXJ2aWNlIjogewogICAgICAgICAgIm90aGVyX3BsYXRmb3JtX29hdXRoX2NsaWVudCI6IFtdCiAgICAgICAgfQogICAgICB9CiAgICB9CiAgXSwKICAiY29uZmlndXJhdGlvbl92ZXJzaW9uIjogIjEiCn0K
```

### Étape 2 : Tester le Workflow

1. **Aller dans l'onglet Actions** de votre repository
2. **Firebase App Distribution** workflow devrait être visible
3. **Run workflow** → Choisir **debug** → **Run workflow**

## 🔧 Configuration GitLab CI

### Étape 1 : Ajouter la Variable GOOGLE_SERVICES_JSON

1. **Aller dans votre repository GitLab**
2. **Settings** → **CI/CD** → **Variables**
3. **Add variable**
4. **Key** : `GOOGLE_SERVICES_JSON`
5. **Value** : Coller le contenu base64 ci-dessus
6. **Cocher** : Protected, Masked

### Étape 2 : Tester le Pipeline

1. **Faire un push sur la branche `develop`**
2. **Vérifier l'onglet CI/CD**
3. **Le pipeline devrait se déclencher automatiquement**

## 🧪 Test des Workflows

### Test 1 : Déclenchement automatique

```bash
# Créer une branche de test
git checkout -b test-ci-cd

# Faire une modification
echo "# Test CI/CD" >> README.md

# Commit et push
git add README.md
git commit -m "test: Test CI/CD workflow"
git push origin test-ci-cd

# Créer une PR sur main
# Le workflow devrait se déclencher automatiquement
```

### Test 2 : Déclenchement manuel (GitHub Actions)

1. **Actions** → **Firebase App Distribution**
2. **Run workflow**
3. **Choisir le type de build**
4. **Run workflow**

### Test 3 : Tag de release

```bash
# Créer un tag
git tag v1.0.0-test

# Push le tag
git push origin v1.0.0-test

# Le workflow release devrait se déclencher
```

## 📱 Vérification des Déploiements

### Firebase Console

1. **Aller sur** [Firebase Console](https://console.firebase.google.com/project/synestesia-a0ea7/appdistribution)
2. **App Distribution** → **Releases**
3. **Vérifier que les nouveaux builds apparaissent**

### Logs CI/CD

1. **GitHub** : Actions → Firebase App Distribution → Voir les logs
2. **GitLab** : CI/CD → Pipelines → Voir les logs

## 🚨 Résolution des Problèmes

### Erreur : "Failed to read file google-services.json"

- ✅ Vérifier que le secret est bien configuré
- ✅ Vérifier que le nom du secret est exact : `GOOGLE_SERVICES_JSON`

### Erreur : "App Distribution halted because it had a problem adding testers/groups"

- ✅ Vérifier que les groupes existent dans Firebase Console
- ✅ Vérifier que le fichier `firebase-groups.txt` est correct

### Erreur : "Build failed"

- ✅ Vérifier les logs de build dans CI/CD
- ✅ Vérifier que le projet compile localement

## 🔄 Workflow de Développement Recommandé

### Développement quotidien

```bash
# 1. Travailler sur une branche feature
git checkout -b feature/nouvelle-fonctionnalite

# 2. Développer et tester localement
./scripts/deploy-local.sh debug --dry-run

# 3. Commit et push
git add .
git commit -m "feat: Nouvelle fonctionnalité"
git push origin feature/nouvelle-fonctionnalite

# 4. Créer une PR sur develop
# → Déploiement debug automatique
# → Tests par l'équipe
```

### Release

```bash
# 1. Merge sur main
git checkout main
git merge develop

# 2. Tag de version
git tag v1.0.0
git push origin v1.0.0

# → Déploiement release automatique
# → Distribution aux testeurs finaux
```

## 📊 Monitoring

### Métriques à surveiller

- **Temps de build** : Objectif < 5 minutes
- **Taux de succès** : Objectif > 95%
- **Temps de déploiement** : Objectif < 2 minutes

### Alertes

- **Builds qui échouent** : Notifications automatiques
- **Déploiements échoués** : Rollback automatique si possible
- **Performance** : Alertes si le temps de build augmente

## 🎯 Prochaines étapes

1. **Configurer les secrets** dans GitHub/GitLab
2. **Tester les workflows** avec des branches de test
3. **Configurer les notifications** (Slack, email)
4. **Optimiser les performances** (cache, builds parallèles)
5. **Configurer la signature** pour les APKs release de production

## 📞 Support

En cas de problème :

1. **Vérifier les logs CI/CD**
2. **Tester localement** avec `./scripts/deploy-local.sh`
3. **Vérifier la configuration Firebase**
4. **Consulter la documentation** dans `docs/CI_CD_WORKFLOWS.md`
