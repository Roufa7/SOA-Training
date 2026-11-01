# Atelier 5 — Sécurité JAX-RS

Cette mise à jour conserve les ressources et la logique métier de workshop-security.
Configurer SOA_USERNAME et SOA_PASSWORD dans l'environnement de Tomcat avant le démarrage.
Construire avec `mvn package`, puis déployer le WAR sur Tomcat 9 (JDK 17).

Base : http://localhost:8080/Gestion_UE_VF-1.0-SNAPSHOT/api
POST /authentification accepte un formulaire username/password ou le JSON correspondant.
Identifiants corrects : 200 et jeton en texte. Incorrects : 403. Configuration absente : 503.
Le JWT HS512 contient sub, iss, iat, exp et jti et expire après 15 minutes.
La clé aléatoire de 64 octets et les jetons sont conservés en mémoire. Un redémarrage invalide les jetons.

Les écritures UE et modules exigent Authorization: Bearer <jeton>.
POST /ue/add accepte JSON ou XML. Exemple JSON :
{"code":101,"domaine":"SOA","responsable":"Enseignant","credits":6,"semestre":1}
Sans jeton, avec jeton falsifié ou expiré : 401 et WWW-Authenticate: Bearer.
Avec jeton valide et données acceptées par le métier : 201.
GET /ue/list conserve sa protection existante.
Les autres lectures restent publiques ; un jeton invalide ne les protège pas automatiquement.

Validation à réaliser sur le serveur : connexion formulaire et JSON, mauvais mot de passe,
création sans jeton, avec jeton valide, altéré, puis expiré après 15 minutes.
Vérifier une lecture publique avec jeton invalide : elle doit rester accessible.
Ne pas publier de mots de passe ni de jetons réels. Les deux premiers segments du JWT
peuvent être décodés localement en Base64URL : le contenu est lisible, pas chiffré.
