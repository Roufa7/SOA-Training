# Rapport de validation — Atelier 5

Date : 2 novembre 2026. Application réellement déployée sur Tomcat 9.0.96,
port local isolé 18089, JVM JetBrains 25 ; sources compilées pour Java 17.

## Résultats HTTP réels

| Scénario | HTTP | Résultat |
|---|---|---|
| Missing token | 401 | PASS |
| Incorrect password | 403 | PASS |
| Missing credentials | 403 | PASS |
| JSON login | 200 | PASS |
| Form login | 200 | PASS |
| JWT HS512 claims and 15-minute lifetime | 200 | PASS |
| Valid token read | 200 | PASS |
| Write without token | 401 | PASS |
| Valid token JSON creation | 201 | PASS |
| Valid token XML creation | 201 | PASS |
| Altered signature | 401 | PASS |
| Malformed token | 401 | PASS |
| Wrong authentication scheme | 401 | PASS |
| Empty bearer | 401 | PASS |
| Case insensitive bearer | 200 | PASS |
| Public with invalid token | 200 | PASS |
| Public without token | 200 | PASS |
| Protected update | 200 | PASS |
| Protected delete | 200 | PASS |

## Expiration et signature

`mvn test` : 3 tests, 0 échec, 0 erreur. Le test d'expiration construit un jeton
signé avec la bonne clé, enregistré dans le registre, dont l'expiration est passée.
Le parseur lève ExpiredJwtException et le filtre renvoie 401 avec WWW-Authenticate: Bearer.
Ce contrôle est déterministe : il ne dépend pas d'une attente de 15 minutes.
La durée normale de 900 secondes est vérifiée dans les claims du jeton obtenu par HTTP.

## Corrections issues des tests

- Jackson databind 2.4.2 transitif de JJWT provoquait une erreur 500 en JSON :
  version explicite 2.8.10 compatible avec Jersey 2.27.
- Déclaration JAXB directe et XmlRootElement pour les requêtes XML.
- Ajout de GET /hello/public pour vérifier les méthodes non sécurisées.

## Reproduire

Configurer SOA_USERNAME et SOA_PASSWORD avant de démarrer Tomcat.
Dans le terminal de test, fournir les mêmes variables et SOA_BASE_URL si nécessaire,
puis lancer `python tests/verify_rest.py`. Le script crée des UE 8101 et 8102 dans
le stockage pédagogique en mémoire. Lancer `mvn test` pour les tests JWT et filtre.
Ne pas enregistrer le mot de passe ou le JWT dans Git.
