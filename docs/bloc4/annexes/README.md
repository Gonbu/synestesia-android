# Annexes - Bloc 4 : Monitoring et Maintenance

## Structure des Annexes

```
docs/bloc4/annexes/
├── README.md                 # Ce fichier
├── deps/                    # Configuration mise à jour dépendances
│   ├── renovate.json       # Configuration Renovate
│   └── dependabot.yml      # Configuration Dependabot
├── monitoring/              # Monitoring et métriques
├── incidents/               # Gestion des incidents
│   ├── template_fiche_incident.md
│   └── INC-2025-01-17-001.md
├── ci_cd/                   # Liens CI/CD
│   └── links.md            # Liens vers les services
└── CHANGELOG.md             # Historique des versions
```

## Contenu des Annexes

### A. Config MAJ Dépendances

- **Fichiers**: `renovate.json`, `dependabot.yml`
- **Emplacement**: `deps/`
- **Statut**: ✅ Créés

### B. Captures Crashlytics

- **Fichiers**: [À capturer]
- **Emplacement**: `monitoring/`
- **Statut**: [À faire]

### C. Captures Performance

- **Fichiers**: [À capturer]
- **Emplacement**: `monitoring/`
- **Statut**: [À faire]

### D. Template Fiche Incident

- **Fichier**: `template_fiche_incident.md`
- **Emplacement**: `incidents/`
- **Statut**: ✅ Créé

### E. Fiche Incident Réelle

- **Fichier**: `INC-2025-01-17-001.md`
- **Emplacement**: `incidents/`
- **Statut**: ✅ Créé

### F. Liens CI/CD

- **Fichier**: `links.md`
- **Emplacement**: `ci_cd/`
- **Statut**: ✅ Créé

### G. CHANGELOG

- **Fichier**: `CHANGELOG.md`
- **Emplacement**: `annexes/`
- **Statut**: [À copier]

### H. Distribution Testeurs

- **Fichiers**: [À capturer]
- **Emplacement**: `monitoring/`
- **Statut**: [À faire]

## Services à Configurer

### Firebase Console

- **App Distribution**: [À configurer]
- **Crashlytics**: [À configurer]
- **Performance**: [À configurer]

### GitHub Actions

- **Workflow Release**: [À configurer]
- **Workflow Monitoring**: [À créer]

### Outils de Mise à Jour

- **Renovate**: [À activer]
- **Dependabot**: [À activer]

## Captures à Faire

### Crashlytics

1. Aller sur Firebase Console → Projet → Build → Crashlytics
2. Capturer l'état avant optimisation
3. Capturer l'état après optimisation

### Performance

1. Aller sur Firebase Console → Projet → Build → Performance
2. Capturer les métriques "App start time"
3. Capturer les traces custom (open_map, souvenir_creation, audio_playback)

### App Distribution

1. Aller sur Firebase Console → Projet → Release & Monitor → App Distribution
2. Capturer l'écran des releases internes

## Métriques à Collecter

### App Start Time

- **Avant optimisation**: [À mesurer]
- **Après optimisation**: [À mesurer]
- **Amélioration**: [À calculer]

### Cold Start

- **Avant optimisation**: [À mesurer]
- **Après optimisation**: [À mesurer]
- **Amélioration**: [À calculer]

### Custom Traces

- **open_map**: [À mesurer]
- **souvenir_creation**: [À mesurer]
- **audio_playback**: [À mesurer]

## Prochaines Étapes

1. **Configurer Firebase** (App Distribution, Crashlytics, Performance)
2. **Activer Renovate/Dependabot** sur GitHub
3. **Capturer les métriques** de performance
4. **Documenter les processus** de gestion d'incident
5. **Créer l'archive ZIP** des annexes

## Notes

- Les configurations Renovate et Dependabot sont prêtes
- Les templates d'incident sont créés
- Les captures Firebase doivent être faites manuellement
- L'archive finale sera créée avec `zip -r docs/bloc4/annexes.zip docs/bloc4/annexes/`

## Liens Utiles

- [Firebase Console](https://console.firebase.google.com/)
- [GitHub Actions](https://github.com/features/actions)
- [Renovate Documentation](https://docs.renovatebot.com/)
- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
