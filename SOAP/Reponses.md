# Prosit SOAP — Réponses

## Partie 1 — Analyse
1. Il faut connaître l'adresse de l'endpoint, l'opération exacte, le namespace, les paramètres et leurs types, la structure XML, la version SOAP, le transport et l'action SOAP.
2. Ces informations se trouvent dans le WSDL et les schémas XSD associés. Ici le schéma est intégré au WSDL.
3. `?wsdl` demande la description du service, et non l'exécution d'une opération.
4. Le WSDL décrit le contrat nécessaire pour construire l'appel. Il faut aussi un client HTTP/SOAP, des valeurs d'entrée et un service accessible. Il ne prouve ni la disponibilité ni le bon fonctionnement du serveur.

## Partie 2 — Contrat

| Information | Disponible | Emplacement |
|---|---|---|
| Nom du service : Calculator | Oui | wsdl:service/@name |
| Add, Subtract, Multiply, Divide | Oui | wsdl:portType/wsdl:operation |
| intA et intB | Oui | wsdl:types, éléments XSD des opérations |
| Entrées et résultats : xsd:int | Oui | attribut type des éléments XSD |
| http://www.dneonline.com/calculator.asmx | Oui | wsdl:service/wsdl:port/soap:address |
| XML document/literal, SOAP 1.1 et 1.2 | Oui | wsdl:binding, soap:binding et soap:body |
| http://tempuri.org/ | Oui | targetNamespace et schéma |

Le contrat téléchargé est conservé dans `SOAP/calculator.wsdl`.

## Partie 3 — Add(15,20)
Le message complet se trouve dans `Add.xml`. Le nom exact est `Add` (majuscule), avec `intA=15` et `intB=20`. Envelope contient un Header vide (facultatif ici) et le Body contenant l'opération qualifiée par son namespace.

Envoyer un POST à l'endpoint sans `?wsdl`, avec `Content-Type: text/xml; charset=utf-8` et `SOAPAction: "http://tempuri.org/Add"`. Le résultat attendu est `<AddResponse xmlns="http://tempuri.org/"><AddResult>35</AddResult></AddResponse>` dans le Body de l'Envelope SOAP 1.1.

## Partie 4 — Discussion
Non. Le contrat explique comment construire des messages valides ; l'inspection des messages réellement envoyés et reçus permet de vérifier les valeurs, namespaces, en-têtes, erreurs SOAP et réponses. Une réponse HTTP réussie ne suffit pas à elle seule : il faut contrôler son contenu.

## Partie 5 — SoapUI
Créer un projet SOAP et importer le WSDL local ou son URL. SoapUI doit proposer CalculatorSoap et CalculatorSoap12, avec les quatre opérations. Pour Add, ouvrir la requête générée et remplacer les deux valeurs par 15 et 20, puis envoyer.

SoapUI récupère les opérations, endpoints, bindings, actions, namespaces et types. Il génère l'Envelope, le Body, le wrapper d'opération et les paramètres ; un Header vide peut aussi être présent. Les quatre opérations doivent correspondre au tableau de la partie 2. Vérifier HTTP 200, absence de Fault et AddResult=35. Répéter avec Subtract(15,20)=-5, Multiply(15,20)=300 et Divide(20,5)=4.

Le WSDL a été récupéré pendant la réalisation. Un POST SOAP 1.1 réel Add(15,20) a renvoyé HTTP 200 et AddResult=35 ; la réponse est conservée dans `Add-response.xml`. Les manipulations graphiques dans SoapUI restent à exécuter ; elles ne sont pas présentées comme des observations effectuées.
