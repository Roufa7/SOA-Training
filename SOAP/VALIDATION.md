# Validation expérimentale SoapUI

Date : 2 novembre 2026. SoapUI Open Source 5.10.0 installé localement.

Le WSDL Calculator a été importé dans l'interface graphique de SoapUI avec la génération
des requêtes activée. Observation : deux bindings CalculatorSoap et CalculatorSoap12 ;
chacun expose Add, Divide, Multiply et Subtract. La requête Add générée contient
Envelope, Header vide, Body, namespace tempuri.org et deux paramètres intA/intB
initialement remplacés par des points d'interrogation.

Le projet enregistré est Calculator-Prosit-soapui-project.xml. Les entrées de ses
requêtes ont été remplies et huit tests ajoutés, puis exécutés avec le moteur SoapUI
en ligne de commande contre le véritable endpoint HTTP public.

| Opération | Entrées | Résultat obtenu SOAP 1.1 | Résultat obtenu SOAP 1.2 |
|---|---|---|---|
| Add | 15, 20 | 35 | 35 |
| Subtract | 15, 20 | -5 | -5 |
| Multiply | 15, 20 | 300 | 300 |
| Divide | 20, 5 | 4 | 4 |

8 cas réussis, 8 requêtes, 16 assertions, 0 échec. Pour chaque appel : assertion
SOAP Response et comparaison XPath du résultat. Rapport original :
results/TEST-Calculator_validation.xml.

Pour reproduire : importer Calculator-Prosit-soapui-project.xml dans SoapUI,
ouvrir la suite Calculator validation et cliquer sur Run. Garder calculator.wsdl
dans le même dossier ; le projet utilise ${projectDir}/calculator.wsdl.
Le contrat contient aussi une copie embarquée du WSDL.
