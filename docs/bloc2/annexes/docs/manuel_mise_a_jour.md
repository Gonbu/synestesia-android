# Manuel de Mise à Jour - Projet Synestesia

## Prérequis

### Système

- Android Studio Hedgehog ou plus récent
- JDK 17 ou plus récent
- Gradle 8.13+
- Git

### Dépendances

- Firebase CLI installé et configuré
- Keystore de release configuré

## Processus de Mise à Jour

### 1. Mise à Jour des Dépendances

#### Vérification des Versions

```bash
./gradlew dependencyUpdates
```

#### Mise à Jour Gradle

```bash
./gradlew wrapper --gradle-version=8.13
```

#### Mise à Jour des Plugins

```bash
./gradlew buildEnvironment
```

### 2. Mise à Jour du Code

#### Pull des Dernières Modifications

```bash
git pull origin main
git submodule update --init --recursive
```

#### Nettoyage du Projet

```bash
./gradlew clean
./gradlew cleanBuildCache
```

#### Vérification de la Qualité

```bash
./gradlew lintDebug
./gradlew detekt
./gradlew ktlintCheck
```

### 3. Mise à Jour de la Configuration

#### Variables d'Environnement

- Vérifier `local.properties`
- Mettre à jour `secrets.properties` si nécessaire
- Vérifier `google-services.json`

#### Configuration Gradle

- Vérifier `gradle/libs.versions.toml`
- Mettre à jour `app/build.gradle.kts`

### 4. Tests et Validation

#### Tests Unitaires

```bash
./gradlew test
```

#### Tests d'Intégration

```bash
./gradlew connectedAndroidTest
```

#### Build de Validation

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

## Gestion des Conflits

### Conflits de Dépendances

1. Identifier les conflits avec `./gradlew dependencyInsight`
2. Résoudre les versions dans `libs.versions.toml`
3. Mettre à jour les plugins si nécessaire

### Conflits de Code

1. Utiliser `git mergetool`
2. Vérifier la compilation après résolution
3. Lancer les tests de régression

## Rollback

### En Cas de Problème

```bash
git log --oneline -10
git reset --hard <commit_hash>
./gradlew clean
./gradlew assembleDebug
```

### Restauration des Dépendances

```bash
rm -rf .gradle
rm -rf build
./gradlew --refresh-dependencies
```

## Checklist de Mise à Jour

- [ ] Sauvegarde du projet actuel
- [ ] Pull des dernières modifications
- [ ] Mise à jour des dépendances
- [ ] Vérification de la compilation
- [ ] Lancement des tests
- [ ] Validation de la qualité
- [ ] Test sur device/émulateur
- [ ] Documentation des changements
- [ ] Commit des modifications

## Notes Importantes

- Toujours tester sur un device physique après mise à jour
- Vérifier la compatibilité des plugins Firebase
- Maintenir la cohérence des versions dans tout le projet
- Documenter les changements dans le CHANGELOG
- Créer un tag de version après validation

## Support

En cas de problème :

1. Consulter les logs Gradle
2. Vérifier la compatibilité des versions
3. Consulter la documentation officielle
4. Créer une issue GitHub si nécessaire
