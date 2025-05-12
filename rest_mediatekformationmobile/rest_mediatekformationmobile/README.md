<h1>Présentation de l'API</h1>
Cette API contient, dans sa branche 'master', le code de base pouvant s'adapter à n'importe quel besoin d'API nécessitant l'exploitation d'une BDD au format MySQL, avec possibilité d'accès en authentification basique.<br>
Il est possible de faire les opérations de base de recherches, insertions, modifications et suppressions, sur le contenu des tables de la BDD concernée.<brr>
Dans le cadre de l'exemple, elle permet d'exploiter la BDD Chocolatein dont le script est téléchargeable en racine du projet.L'accès se fait en authentification basique<br>
<strong>IMPORTANT :</strong><br>
Si vous ne voulez pas juste exploiter l'API mais aussi apprendre à la <strong>construire</strong> pas à pas, suivez le guide dans le Wiki :
https://github.com/CNED-SLAM/rest_chocolatein/wiki/Guide-cr%C3%A9ation-API-REST-pas-%C3%A0-pas


<h1>Contenu de l'API</h1>
Cette API contient plusieurs fichiers. <br>
Elle a été construite pour s'adapter à toute application nécessitant l'accès à une BDD MySQL via une API REST.<br>
Elle est facilement adaptable pour accéder à un autre type de SGBDR, en faisant des changements dans la classe Connexion.<br>
Les fichiers PHP qui concernent vraiment l'API et le fichier '.env' des variables d'environnement, sont dans le dossier 'src'.<br>
Normalement, les seuls fichiers sur lesquels il faut intervenir pour une API accédant à une BDD MySQL, sont :<br>
- .env : contenant les données sensibles (authentification pour l'accès à l'API et informations d'accès à la BDD)<br>
- MyAcceesBDD.php : contenant les requêtes à exécuter sur la BDD.<br>
Voici la description des différents fichiers de l'API :<br>

<h2>.htaccess</h2>
C'est le seul fichier qui n'est pas dans le dossier 'src' mais en racine du projet, car son rôle est d'analyser les URL entrantes.<br>
Contient les règles de réécriture pour l'accès à index.php avec les bons paramètres, en précisant à chaque fois la méthode HTTP attendue :<br>
- GET (select), paramètres : nom de table + (optionnel) listes de champs (nom/valeur) au format json
- POST (insert), paramètre : nom de table. Les champs au format json doivent être dans le body
- PUT (update), paramètres : nom de table + (optionnel) id. Les champs au format json doivent être dans le body
- DELETE (delete), paramètres : nom de table + (optionnel) liste de champs (nom/valeur) au format json

<h2>.env</h2>
<strong>Le contenu de ce fichier doit être mis à jour avec les données réelles.</strong>
Contient les informations sensibles (type d'authentification, user/pwd d'authentification si accès 'basic' à l'API, données de connexion à la BDD)

<h2>Index.php</h2>
Point d'entrée de l'api, contrôle l'accès sécurisé, récupère les variables, la méthode d'envoi et transfère les informations à la classe Controle.

<h2>Url.php</h2>
Récupère la méthode HTTP et toutes les variables envoyées et les éventuelles informations d'authentification.<br>
Pour le moment cette classe ne gère que l'authentification basique. Elle peut être complétée pour gérer d'autres types d'authentification. 

<h2>Controle.php</h2>
Suivant les demandes provenant de index.php, appelle la méthode concernée dans la classe MyAccessBDD puis retourne le résultat au format json.<br>
Le résultat contient :<br>
- "code:" suivi du code (200 si "OK", 400 si "requête invalide", 500 si "erreur serveur" donc d'accès à la BDD, 401 si "authentification incorrecte")<br>
- "message:" suivi du message correspondant au code (voir les messages cités au tiret précédent)<br>
- "result:" suivi du résultat (résultat au format tableau associatif pour une requête LID, nombre de lignes impactées pour une requête LMD).

<h2>MyAccessBDD.php</h2>
<strong>C'est dans cette classe qu'il faut travailler.</strong><br>
Elle hérite de AccessBDD. Elle contient déjà les méthodes redéfinies (override) de la classe mère.<br>
Ces méthodes sont à compléter en ajoutant des 'case' dans les différents 'switch' et en y insérant l'appel de nouvelles méthodes à créer.<br>
Ces nouvelles méthodes ajoutées doivent permettre de construire les requêtes SQL nécessaires, de demander de les exécuter (en faisant appel à la classe ConnexionBDD) et de retourner le résultat. Pour savoir construire ces méthodes, s'inspirer de celles déjà présentes dans MyAccessBDD qui réalisent des traitements similaires (construire une requête et l'exécuter).<br> 
<strong>Attention :</strong><br>
Dans les méthodes redéfinies, excepté ajouter des 'case', il ne faut surtout pas toucher au contenu du 'default' qui gère les requêtes de base.

<h2>AccessBDD.php</h2>
Contient :<br>
- l'envoi d'informations nécessaires à la connexion à la BDD<br>
- les méthodes directement appelées par la classe Controle et qui font appel à des méthodes abstraites<br>
- des méthodes abstraites pour forcer MyAccessBDD à les redéfinir afin d'écrire les requêtes spécifiques aux besoins.

<h2>Connexion.php</h2>
Se connecte à la BDD, construit les requêtes en intégrant les paramètres, les exécute et retourne le résultat.


<h1>Installation de l'API en local</h1>
Pour tester l'API REST en local, voici le mode opératoire :<br>
- Suivre les étapes "Préparer l'environnement et la BDD" au début du guide de création de l'API, dont le lien est donné au début de ce readme.<br>
- Télécharger le zip du code de l'API et le dézipper dans le dossier www de wampserver (renommer le dossier en "rest_chocolatein", donc en enlevant "_master").<br>
- Dans une fenêtre de commandes ouverte en mode admin, aller dans le dossier de l'API et taper 'composer install' puis valider pour recréer le vendor.<br>
- Récupérer le script chocolatein.sql en racine du projet puis, avec phpMyAdmin, créer la BDD chocolatein et, dans cette BDD, exécuter le script pour remplir la BDD.<br>
- Ouvrir l'API dans NetBeans pour pouvoir analyser le code et le faire évoluer suivant les besoins.<br>
- Pour tester l'API avec Postman, ne pas oublier de configurer l'authentification (onglet "Authorization", Type "Basic Auth", Username "admin", Password "adminpwd".


<h1>Exploitation de l'API</h1>
Adresse de l'API (en local) : http://localhost/rest_chocolatein/ <br>
Voici les différentes possibilités de sollicitation de l'API, afin d'agir sur la BDD, en ajoutant des informations directement dans l'URL (visible) et éventuellement dans le body (invisible) suivant les besoins : 

<h2>Récupérer un contenu (select)</h2>
Méthode HTTP : <strong>GET</strong><br>
http://localhost/rest_chocolatein/table/champs (champs optionnel)<br>
- 'table' doit être remplacé par un nom de table (caractères acceptés : alphanumériques et '_')<br>
- 'champs' (optionnel) doit être remplacé par la liste des champs (nom/valeur) qui serviront à la recherche (au format json)

<h2>Insérer (insert)</h2>
Méthode HTTP : <strong>POST</strong><br>
http://localhost/rest_chocolatein/table <br>
- 'table' doit être remplacé par un nom de table (caractères acceptés : alphanumériques et '_')<br>
Dans le body (Dans Postman, onglet 'Body', cocher 'x-www-form-urlencoded'), ajouter :<br>
- Key : 'champs'<br>
- Value : liste des champs (nom/valeur) qui serviront à l'insertion (au format json)

<h2>Modifier (update)</h2>
Méthode HTTP : <strong>PUT</strong><br>
http://localhost/rest_chocolatein/table/id (id optionnel)<br>
- 'table' doit être remplacé par un nom de table (caractères acceptés : alphanumériques et '_')<br>
- 'id' (optionnel) doit être remplacé par l'identifiant de la ligne à modifier (caractères acceptés : alphanumériques)<br>
Dans le body (Dans Postman, onglet 'Body', cocher 'x-www-form-urlencoded'), ajouter :<br>
- Key : 'champs'<br>
- Value : liste des champs (nom/valeur) qui serviront à la modification (au format json)

<h2>Supprimer (delete)</h2>
Méthode HTTP : <strong>DELETE</strong><br>
http://localhost/rest_chocolatein/table/champs (champs optionnel)<br>
- 'table' doit être remplacé par un nom de table (caractères acceptés : alphanumériques et '_')<br>
- 'champs' (optionnel) doit être remplacé par la liste des champs (nom/valeur) qui serviront déterminer les lignes à supprimer (au format json)

<h1>Quelques exemples d'utilisation de l'API</h1>
Le but est de comprendre comment ajouter des fonctionnalités spécifiques dans le code existant.<br>
Dans le guide de création de l'API, dont le lien est donné au début de se readme, donne un premier exemple guidé ("Ajout d'un besoin spécifique") qu'il est conseillé de suivre si le guide n'a pas été suivi.<br>
Voici d'autres exemples dont la correction est donnée dans la branche "tests".<br>
Si vous voulez coder par vous-même ces exemples, alors, repartez de votre application si vous l'avez construite en suivant le guide, ou récupérez (download ou clone) l'application de la branche 'master'. Ensuite, il est conseillé de créer une banche 'tests'.<br>
Mais avant de proposer plusieurs exemples, voici la structure de la BDD chocolatein qui sert pour les tests :<br>
<code>produit(id, nom, description, packaging, urlimg, idgamme)
   id : clé primaire
   idgamme : clé étrangère en réf. à id de gamme
gamme(id, libelle, picto)
   id : clé primare
details_produits(idproduit, num, details)
   idproduit, num : clé primaire
   idroduit : clé étrangère en réf. à id de produit
</code><br>
Pour chaque exemple, vous trouverez :<br>
- l'explication en clair de la demande<br>
- la syntaxe pour solliciter l'API : méthode HTTP, URL attendue, éventuellement variables attendues à mettre dans le body<br>
- Un exemple d'URL et de body pour les tests sous Postman<br>
- comment contrôler si le résultat est correct : ce que vous devriez obtenir<br>
Le but est donc de :<br>
- dans l'API, ajouter le code nécessaire pour satisfaire la demande<br>
- dans Postman, tester si, en utilisant la syntaxe de test, le résultat est bien conforme (attention, dans Postman, il faut d'abord configurer l'authentification : onglet 'authorization', type 'Basic Auth', Uername 'admin', Password 'adminpwd')

<h2>Exemple 1 : select sans condition</h2>
<h3>Explication de la demande</h3>
Il serait intéressant de connaître, pour chaque produit, le nombre de variantes possibles. Les variantes se trouvent dans la table 'detail_produits'. Par exemple, le produit 'choc_o_lait' existe dans 4 variantes : lait, noir, noisette, café.<br>
Pour les produits qui ne sont pas présents dans 'détails_produits', ils devront tout de même être présents dans la liste finale obtenue avec 1 comme nombre de variantes. La liste doit contenir l'id et le nom du produit ainsi qu'un champ nommé 'variantes' contenant le nombre attendu.
<h3>Syntaxe pour solliciter l'API</h3>
- méthode HTTP : GET<br>
- URL attendue :  http://localhost/rest_chocolatein/variantes <br>
- body attendu : aucun<br>
- URL test : http://localhost/rest_chocolatein/variantes <br>
- body test : aucun<br>
<h3>Contrôle du  résultat</h3>
25 produits attendus avec, par exemple, 'ballotins' 1 variante, 'coffret_27_tablettes' 9 variantes.

<h2>Exemple 2 : select avec condition</h2>
<h3>Explication de la demande</h3>
Dans le cas d'intolérances, on doit pouvoir lister les produits (id, nom, description, details) qui ne contiennent pas un ingrédient spécifique (dont le nom serait présent dans 'description' ou 'détails' de la table 'details_produits').<br>
Remarque : cette demande est proche de celle montrée en exemple dans le guide.
<h3>Syntaxe pour solliciter l'API</h3>
- méthode HTTP : GET<br>
- URL attendue :  http://localhost/rest_chocolatein/intolerance/champs_format_josn <br>
- body attendu : aucun<br>
- URL test : http://localhost/rest_chocolatein/intolerance/{"ingredient":"lait"} <br>
- body test : aucun<br>
<h3>Contrôle du  résultat</h3>
40 résultats attendus.

<h2>Exemple 3 : insert</h2>
<h3>Explication de la demande</h3>
Lors de la demande d'ajout d'un produit, si la gamme n'existe pas, l'idée est de la créer (juste son id).
<h3>Syntaxe pour solliciter l'API</h3>
- méthode HTTP : POST<br>
- URL attendue :  http://localhost/rest_chocolatein/produit <br>
- body attendu : champs : champs_produits_format_json<br>
- URL test : http://localhost/rest_chocolatein/produit <br>
- body test 1 : champs : {"id":"mars","nom":"mars original","description":"Barre chocolatée au caramel","idgamme":"barre_chocolatée"}<br>
- body test 2 : champs : {"id":"macarons","nom":"macarons","description":"Assortiment de macarons","idgamme":"confiseries"}<br>
<h3>Contrôle du  résultat</h3>
Dan phpMyAdmin, vérifier l'ajout dans 'produit' et, quand la gamme n'existe pas (exmple 1), l'ajout aussi dans 'gamme'.

<h2>Exemple 4 : update</h2>
<h3>Explication de la demande</h3>
Dans la table 'produit', le champ 'urlimg' contient le chemin et le nom du fichier contenant les images. Il doit être possible de déplacer les images dans un autre dossier, donc de préciser l'ancien et le nouveau dossier.<br>
Par exemple, actuellement les images des produits se trouvent dans "vues/images/produits" comme on peut le voir dans la table 'produit'. Si on désire transférer les images cette fois dans le dossier "photos/produits", il faut demander une modification en donnant ces 2 chaînes.
<h3>Syntaxe pour solliciter l'API</h3>
- méthode HTTP : PUT<br>
- URL attendue :  http://localhost/rest_chocolatein/transfert_images <br>
- body attendu : champs : {"ancien":"ancien_chemin","nouveau":"nouveau_chemin"}<br>
- URL test : http://localhost/rest_chocolatein/transfert_images <br>
- body test : champs : {"ancien":"vues/images/produits","nouveau":"photos/produits"}<br>
<h3>Contrôle du  résultat</h3>
Dans phpMyAdmin, vérifier que dans 'produit', champ 'urlimg', tous les débuts de chemins ont été changés.
Pour revenir à l'état initial, refaire le test dans Postman, mais en inversant les valeurs.
<h3>Remarque</h3>
Cet exemple montre qu'un 'update' peut très bien porter sur plusieurs lignes. Il pourrait même porter sur plusieurs tables.

<h2>Exemple 5 : delete</h2>
<h3>Explication de la demande</h3>
Pour nettoyer la table 'gamme', il faut supprimer toutes les lignes qui n'ont pas d'information (libelle et picto vides) et dont l'id n'est pas utilisé par un produit.
<h3>Syntaxe pour solliciter l'API</h3>
- méthode HTTP : DELETE<br>
- URL attendue :  http://localhost/rest_chocolatein/nettoie_gamme <br>
- body attendu : aucun<br>
- URL test : http://localhost/rest_chocolatein/nettoie_gamme <br>
- body test : aucun<br>
<h3>Contrôle du  résultat</h3>
Dans phpMyAdmin, vérifier que la gamme "barre_chocolatée" est toujours présente.<br>
Dans 'produit', supprimer le produit "mars" puis refaire le test précédent dans Postman.<br>
Cette fois, dans phpMyAdmin, vérifier que la gamme "barre_chocolatée" a été supprimée.
<h3>Remarque</h3>
Cet exemple montre qu'il est tout à fait possible de mettre comme nom de table, un nom qui n'est en fait pas une table mais un mot clé qui sera repéré par l'API pour exécuter une requête spécifique.<br><br>

La correction de ces exemples se trouvent dans la branche "tests";
