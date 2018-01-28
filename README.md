# Uno (Programmé en Java)
J'ai programmé un Uno en Java/Netbeans (2-4 joueurs) en L2 (S3).

Nous, le joueur réel on peut jouer contre soit 1, 2 ou 3 ordinateurs/intelligence artificielles(IA).

*Dans le projet vous pouvez trouver des commentaires du code qui explique ceci et cela.*
*(Tous les règles du jeu se trouvent sur le bouton Aide dans le jeu)*

- [Le but du projet](#le-but-du-projet)
- [Les classes](#les-classes)
- [L'interface graphique](#linterface-graphique)
- [L'intelligence artificielle](#lintelligence-artificielle)
- [Quelques images du jeu](#quelques-images-du-jeu)


## Le but du projet 


## Les classes
Il fallait construire plusieurs classes et aussi des classes abstraites (par exemple pour la différence entre une carte normale et une carte speciale ou entre un joueur réel et un ordinateur).
On aurait pu faire des énumerations pour par exemple le choix entre les couleurs pour les jokers ou pour le choix avec combien de joueur on veut jouer mais je n'y avais pas pensé.

Il existe les classes suivantes: 
  * **Carte (abstraites)**              
  > pour créer soit une carte normale ou une carte speciale (à effet)
  * EnsembleCartes        
  > pour créer soit une main, pioche ou defausse
  * **Joueur (abstraites)**             
  > pour créer soit un joueur réel ou un ordinateurs (IA) 
  * EnsembleJoueurs       
  > pour créer les joueurs du jeu (entre 2-4)
  * Partie                 
  > pour créer une partie à partir des joueurs (EnsembleJoueurs) et cartes(EnsembleCartes)
  * Fenetre                
  > pour créer l'interface graphique
  * Uno                   
  > pour créer une fenêtre et donc commencer une partie


## L'interface graphique
La fenêtre est basé sur un JPanel. Tous les boutons, images, tableaux (pour le score) sont dans ce JPanel.

Pour l'affichage des popup j'ai utilisé des JOptionPanes, donc pour les popups pour saisir le nom du joueur, le choix avec combien de joueur on va jouer, le choix de la couleur du joker, etc. 

La images, donc les positions des cartes sont calculé avec des algorithmes et variables pour les afficher correctement avec n'importe quelle taille de la fenêtre. Egalement la zone du clic est adapté avec un algorithme. 

Le programme, donc le jeu comprend une interaction par rapport si on a cliqué sur quelque chose, après il fait la chose correspondante. Pour que les ordinateurs puissent jouer et que le JPanel se redessine après chaque carte joué il fallait introduire la notion du Robot qui est inclus dans Java. Ce robot fait egalement interaction de l'appuie sur la fenêtre et car le boolean 'jouerOuPiocher' (qui correspond à ce que le joueur réel avait fait) est false, donc l'IA peut jouer jusqu'à que tous les IAs ont joué. 

Le déroulement de chaque tour se fait par rapport à la priorité du joueur dans la file de priorité de l'ensemble de joueurs de la partie courante. 

Il y a aussi une structure de score pour pouvoir savoir qui a gagné, après chaque partie si on décide de continuer alors le score est affiché (le score se calcule à la base de tous ce que les autres joueurs ont encore en main). Après que quelqu'un a obtenu le score totale de 500 points (ou plus) il a gagné la partie complète. 

## L'intelligence artificielle
J'ai programmé une intelligence artificielle plutôt facile. Au début chaque IA à comparé ses cartes avec la dernière carte joué et construit un Array avec les possibilités des cartes jouables. Après l'IA a joué la première possibilité de ces possibilités.

Donc j'ai changé un peu et maintenant l'IA regarde la pioche et si par exemple cela est un +2 ou un 'Passe ton tour' alors il va chercher si lui aussi il y a une carte comme cela pour pas passer de tour et pour ne pas piocher +2.

Tous cela n'était déjà pas mal mais le problème c'était que si un joueur n'a plus beaucoup de carte alors le joueur d'avant il a simplement joué la première possibilité. Puis j'ai introduit une notion de la 'meilleure carte à jouer' où l'IA analyse aussi combien de carte le joueur suivant a pour sav oir si c'est mieux de jouer un +2 ou +4 ou ... (une carte speciale). 

Dernière chose à faire pour l'IA c'était que si lui-même il n'a plus beaucoup de cartes donc il va essayer de jouer un joker pour pouvoir jouer le reste de ces cartes (l'IA essaie aussi toujours de jouer une des cartes dont la couleur elle a le plus). 


## Quelques images du jeu
![JOptionPaneDuStart](https://image.ibb.co/bNeX3b/start1.png) ![JOptionPaneDeLInterface](https://image.ibb.co/j0P4Gw/uno1.png) 
![JOptionPaneDuScore](https://image.ibb.co/jpj4Gw/uno2.png)
