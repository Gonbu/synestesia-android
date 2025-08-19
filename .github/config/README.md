# Configuration des Secrets et de l'Environnement

## 🏗️ Architecture

Ce dossier contient la configuration pour différents environnements :

### 📁 `ci/` - Configuration CI/CD

- **`local.defaults.properties.ci`** : Valeurs factices pour le build en CI
- **`google-services.json.ci`** : Configuration Firebase factice pour le build

### 📁 `local/` - Templates pour développement

- **`local.defaults.properties.template`** : Template à copier pour le développement local
- **`google-services.json.template`** : Template à copier pour Firebase local

## 🔒 Sécurité

### ❌ Fichiers SENSIBLES (NE PAS COMMITTER)

- `secrets.properties` (racine)
- `local.defaults.properties` (racine)
- `app/google-services.json` (dossier app)

### ✅ Fichiers SÛRS (peuvent être commités)

- `.github/config/ci/` (configuration CI)
- `.github/config/local/` (templates)

## 🚀 Configuration du développement local

### 1. Configuration des secrets

```bash
# Copier le template
cp .github/config/local/local.defaults.properties.template local.defaults.properties

# Éditer avec tes vraies valeurs
nano local.defaults.properties
```

### 2. Configuration Firebase

```bash
# Copier le template
cp .github/config/local/google-services.json.template app/google-services.json

# Éditer avec tes vraies clés Firebase
nano app/google-services.json
```

### 3. Configuration des clés Maps

```bash
# Éditer le fichier existant
nano secrets.properties
```

## 🔄 Workflow CI/CD

1. **Checkout** du code
2. **Copie** des fichiers de config CI vers les emplacements attendus
3. **Build** avec les valeurs factices
4. **Pas de pollution** du code source

## 🛡️ Protection

- Tous les fichiers sensibles sont dans `.gitignore`
- Les templates ne contiennent que des placeholders
- La CI utilise des valeurs factices séparées
