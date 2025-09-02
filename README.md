# MDD - Monde de Dev - Full Stack Application

## 🏗️ Architecture

Cette application full-stack suit une architecture moderne avec séparation stricte frontend/backend :

- **Backend** : Java 21 LTS + Spring Boot 3.3 avec Spring Security et JWT
- **Frontend** : Angular 18 avec Signals, Standalone Components et Angular Material
- **Base de données** : PostgreSQL avec scripts SQL dédiés
- **Conteneurisation** : Docker avec docker-compose
- **CI/CD** : GitHub Actions

## 🚀 Démarrage rapide avec Docker

```bash
# Cloner le repository
git clone <repository-url>
cd Developpez-une-application-full-stack-complete

# Lancer avec Docker Compose
docker-compose up --build

# Accéder à l'application
# Frontend: http://localhost:4200
# Backend API: http://localhost:8080
# Base de données: localhost:5432
```

## 📋 Prérequis

- **Docker & Docker Compose** (recommandé)
- **Node.js 18+** et npm
- **Java 21** et Maven
- **PostgreSQL 16+**

## 🛠️ Développement local

### Backend (Spring Boot)

```bash
cd back

# Installer les dépendances et compiler
./mvnw clean install

# Lancer en mode développement
./mvnw spring-boot:run

# Tests
./mvnw test
```

### Frontend (Angular)

```bash
cd front

# Installer les dépendances
npm install

# Lancer le serveur de développement
npm start

# Tests
npm test

# Build de production
npm run build
```

## 🔧 Configuration

### Variables d'environnement Backend

```properties
# Base de données
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mdd_db
SPRING_DATASOURCE_USERNAME=mdd_user
SPRING_DATASOURCE_PASSWORD=mdd_password

# JWT
JWT_SECRET=mySecretKey
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

### Configuration Frontend

Les configurations d'environnement se trouvent dans `front/src/environments/`

## 📦 Architecture Frontend (Angular 18)

- **Standalone Components** : Plus de modules NgModule
- **Signals** : Nouvelle API de réactivité d'Angular
- **Angular Material** : Components UI modernes
- **Routing fonctionnel** : Configuration des routes simplifiée

## 🔐 Sécurité

- **JWT avec Refresh Tokens** : Authentification sécurisée
- **Validation côté backend et frontend**
- **Spring Security** : Protection des endpoints API
- **CORS configuré** pour le développement

## 🗄️ Base de données

Le schéma PostgreSQL inclut :
- `users` : Utilisateurs de l'application
- `topics` : Sujets de discussion
- `articles` : Articles des utilisateurs
- `comments` : Commentaires sur les articles
- `user_topic_subscriptions` : Abonnements aux sujets

## 🧪 Tests et CI/CD

GitHub Actions configuré pour :
- Tests automatiques backend et frontend
- Build et vérification Docker
- Déploiement automatique sur main branch

## 📝 Scripts utiles

```bash
# Backend
./mvnw clean test                    # Tests backend
./mvnw spring-boot:run              # Lancer l'API

# Frontend  
npm test                            # Tests frontend
npm run build                       # Build production
npm run lint                        # Vérification du code

# Docker
docker-compose up --build           # Rebuild et lancer
docker-compose down                 # Arrêter les services
docker-compose logs backend         # Logs du backend
```

## 🎯 Fonctionnalités à développer

1. **Authentification** : Inscription, connexion, gestion des tokens
2. **Gestion des topics** : Créer, modifier, supprimer des sujets
3. **Articles** : CRUD complet avec commentaires
4. **Abonnements** : S'abonner/désabonner des topics
5. **Feed personnalisé** : Articles des topics suivis
6. **Profil utilisateur** : Gestion du compte

## 🔗 Ressources

- [Angular Material](https://material.angular.io/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

Bonne chance pour le développement ! 🚀
