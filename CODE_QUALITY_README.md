# Code Quality Tools

Ce projet utilise **ktlint** et **detekt** pour maintenir la qualité du code Kotlin. Ces outils sont maintenant configurés et fonctionnels !

## ✅ État actuel

- **ktlint** : ✅ Configuré et fonctionnel
- **detekt** : ✅ Configuré et fonctionnel avec baseline
- **Git hooks** : ✅ Configuré (pre-commit)
- **IDE configuration** : ✅ Fichiers de configuration créés

## 🚀 ktlint

ktlint est un linter de code Kotlin qui applique automatiquement le style de code officiel de Kotlin.

### Commandes disponibles

```bash
# Vérifier le style du code
./gradlew ktlintCheck

# Formater automatiquement le code
./gradlew ktlintFormat

# Vérifier et formater en une seule commande
./gradlew ktlint
```

### Configuration

- Le style de code suit les conventions Android Studio
- Longueur maximale de ligne : 120 caractères
- Indentation : 4 espaces
- Configuration dans `.editorconfig`

### Violations actuelles

⚠️ **Note** : Il y a actuellement des violations de style qui ne peuvent pas être corrigées automatiquement :

- Imports wildcard (ex: `androidx.compose.runtime.*`)
- Lignes trop longues (>120 caractères)
- Ordre des imports avec commentaires

Ces violations n'empêchent pas la compilation mais devraient être corrigées progressivement.

## 🔍 detekt

detekt est un analyseur statique de code Kotlin qui détecte les problèmes de code, la complexité et les mauvaises pratiques.

### Commandes disponibles

```bash
# Analyser le code
./gradlew detekt

# Analyser avec rapport HTML
./gradlew detektMain

# Créer une baseline pour ignorer les violations existantes
./gradlew detektBaseline
```

### Configuration

- Fichier de configuration : `config/detekt/detekt.yml`
- Règles personnalisées pour la complexité, le style et les bugs potentiels
- Seuil de complexité : 15 pour les méthodes, 150 pour les classes
- **Baseline activée** : Les violations existantes sont ignorées pour permettre la construction

### Rapports

- Rapports HTML générés dans `app/build/reports/detekt/`
- Analyse détaillée de la complexité, des exceptions, du style et des bugs potentiels
- Estimation de la dette technique : ~1 jour 1h 55min

## 🪝 Git Hooks

Un hook pre-commit est configuré pour exécuter automatiquement ktlint avant chaque commit.

### Installation

```bash
# Le hook est déjà configuré, mais vous pouvez le réinstaller si nécessaire
git config core.hooksPath .githooks
```

### Désactivation temporaire

```bash
# Pour désactiver temporairement le hook
git commit --no-verify -m "Your commit message"
```

## 🛠️ Intégration IDE

### Android Studio / IntelliJ IDEA

1. **ktlint** : Installez le plugin "ktlint" depuis le marketplace
2. **detekt** : Installez le plugin "detekt" depuis le marketplace
3. **Style de code** : Le projet utilise déjà la configuration officielle Kotlin

### VS Code

1. **ktlint** : Installez l'extension "Kotlin" et configurez ktlint
2. **detekt** : Installez l'extension "detekt" si disponible

## 📋 Workflow recommandé

1. **Avant de commiter** : Le hook pre-commit exécute automatiquement ktlint
2. **Formatage automatique** : Utilisez `./gradlew ktlintFormat` pour corriger le style
3. **Analyse de qualité** : Exécutez `./gradlew detekt` régulièrement pour identifier les problèmes
4. **Résolution progressive** : Corrigez les violations détectées par ktlint et detekt au fur et à mesure

## 🔧 Personnalisation

### ktlint

Modifiez le fichier `.editorconfig` pour ajuster les règles de formatage.

### detekt

Modifiez le fichier `config/detekt/detekt.yml` pour :

- Activer/désactiver des règles
- Ajuster les seuils de complexité
- Personnaliser les rapports

## 🚨 Résolution des problèmes courants

### Erreurs ktlint

```bash
# Formater automatiquement
./gradlew ktlintFormat

# Vérifier les violations
./gradlew ktlintCheck
```

### Erreurs detekt

```bash
# Générer un rapport détaillé
./gradlew detektMain

# Créer une baseline pour ignorer les violations existantes
./gradlew detektBaseline
```

### Violations persistantes

Certaines violations ne peuvent pas être corrigées automatiquement :

- **Imports wildcard** : Remplacez `package.*` par des imports spécifiques
- **Lignes trop longues** : Divisez les longues expressions
- **Ordre des imports** : Réorganisez manuellement les imports

## 📊 Métriques de qualité actuelles

- **ktlint** : ~30 violations (principalement imports wildcard et lignes longues)
- **detekt** : 140 violations (complexité, exceptions, style) - **en baseline**
- **Dette technique estimée** : 1 jour 1h 55min

## 🎯 Objectifs de qualité

1. **Court terme** : Corriger les violations ktlint corrigeables automatiquement
2. **Moyen terme** : Réduire la complexité cyclomatique des fonctions
3. **Long terme** : Améliorer la gestion des exceptions et réduire la dette technique

## 📚 Ressources

- [ktlint Documentation](https://ktlint.github.io/)
- [detekt Documentation](https://detekt.dev/)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Code Style](https://source.android.com/setup/develop#code-style)

## 🎉 Félicitations

Votre projet est maintenant équipé d'outils de qualité de code professionnels ! Ces outils vous aideront à maintenir un code propre, lisible et maintenable.
