package uno;

import java.io.IOException;

/**
 *
 * @author janosfalke
 */
public class Partie 
{

    //Attributs
    private EnsembleJoueurs players;
    private EnsembleCartes pioche;
    private EnsembleCartes defausse;

    //Constructeur
    Partie(int nbPlayers) throws IOException 
    {
        this.pioche = new EnsembleCartes();
        this.defausse = new EnsembleCartes();
        this.players = new EnsembleJoueurs();

        this.pioche.chargementCarteNormale("fichiersTxt/CartesNormalesUno.txt");
        this.pioche.chargementCarteSpeciale("fichiersTxt/CartesSpecialesUno.txt");
        this.pioche.melanger();

        this.players.add(new JoueurReel("Moi")); //Creation du Joueur

        //Création des (nbPlayer-1) IA
        for (int i = 1; i < nbPlayers; i++)
        {
            this.players.add(new IA("Ordi " + i));
        }

        //Pour ajouter des 7 cartes selon le nombre des joueurs à la main du joueur
        for (int i = 0; i < nbPlayers; i++) 
        {
            for (int j = 0; j < 7; j++) 
            {
                Joueur jou = this.players.get();
                jou.getMain().ajoutCarte(this.pioche.get());

                this.players.add(jou);
            }
        }

        this.defausse.ajoutCarte(this.pioche.get()); //Ajouter la premiere carte de la pioche a la defausse pour pouvoir commencer le jeu

    }

    //Méthodes
    public void reStart(int nbPlayers)  //Réinitilisation de la partie précédente
    { 
        this.pioche = this.recupCarte();
        this.pioche.melanger();

        while (!this.players.estVide())
        {
            this.players.get();
        }

        this.players.add(new JoueurReel("Moi")); //Creation du Joueur

        //Création des (nbPlayer-1) IA
        for (int i = 1; i < nbPlayers; i++)
        {
            this.players.add(new IA("Ordi " + i));
        }
        //Pour ajouter des 7 cartes selon le nombre des joueurs à la main du joueur
        for (int i = 0; i < nbPlayers; i++)
        {
            for (int j = 0; j < 7; j++) 
            {
                this.players.getI(i).getMain().ajoutCarte(this.pioche.get());
            }
        }

        this.defausse.ajoutCarte(this.pioche.get()); //Ajouter la premiere carte de la pioche a la defausse pour pouvoir commencer le jeu

    }

    public EnsembleCartes recupCarte()
    { //Récupération de toutes les cartes du jeu dans un paquet
        EnsembleCartes paquet = new EnsembleCartes();
        for (int i = 0; i < this.players.size(); i++) 
        {
            EnsembleCartes cartePlayer = this.players.get().getMain();
            paquet.concatEC(cartePlayer);
        }
        paquet.concatEC(this.defausse);
        paquet.concatEC(this.pioche);
        return paquet;
    }

    public void jouer(Carte c) 
    {
        //ACTIONS DES CARTES
        if (c instanceof CarteSpeciale) 
        {
            CarteSpeciale cS = (CarteSpeciale) c;
        }

        this.defausse.ajoutCarte(c); //déplacement de la carte jouée de la main du joueur à la defausse
    }

    public void piocher() {
        this.players.getI(0).getMain().ajoutCarte(this.pioche.get());
    }

    public boolean piocheVide() {
        return this.pioche.size() == 0;
    }

    public void melangerDefausseDansPioche()
    {

        //Tester s'il y a au moins 2 cartes dans la defausse pour mettre minimum 1 dans la pioche
        if (this.defausse.size() > 1) 
        {
            Carte premCarteDefausse = this.defausse.getCarte(0); //Récupération de la dernière carte jouée

            //Supprimer la premiere carte de la defausse pour ne pas la mettre dans la pioche
            this.defausse.get();

            //********NOTE******//
            //Carte premCarteDefausse = this.defausse.get() //au lieu de faire 2 get, ça marche je pense, non??
            for (int i = 0; i < this.defausse.size(); i++) 
            {
                this.pioche.ajoutCarte(this.defausse.get());
            }

            //Remettre la derniere carte jouée dans la defausse
            this.defausse.ajoutCarte(premCarteDefausse);

            //Et on melange la nouvelle pioche
            this.pioche.melanger();
        }

    }

    public void inversion(int nbJoueurs)
    {
        if (nbJoueurs == 4) {
            //Changer l'ordre de la priorite
            Joueur j1 = this.getJoueurs().getIRemove(0);
            Joueur j2 = this.getJoueurs().getIRemove(0);
            Joueur j3 = this.getJoueurs().getIRemove(0);
            Joueur j4 = this.getJoueurs().getIRemove(0);

            j1.setPrio(2);
            j2.setPrio(1);
            j3.setPrio(0);
            j4.setPrio(3);

            this.getJoueurs().add(j3);
            this.getJoueurs().add(j2);
            this.getJoueurs().add(j1);
            this.getJoueurs().add(j4);
        }

        if (nbJoueurs == 3)
        {
            //Changer l'ordre de la priorite
            Joueur j1 = this.getJoueurs().getIRemove(0);
            Joueur j2 = this.getJoueurs().getIRemove(0);
            Joueur j3 = this.getJoueurs().getIRemove(0);

            j1.setPrio(1);
            j2.setPrio(0);
            j3.setPrio(2);

            this.getJoueurs().add(j2);
            this.getJoueurs().add(j1);
            this.getJoueurs().add(j3);
        }

        if (nbJoueurs == 2) 
        {
            //Changer l'ordre de la priorite (pouvoir rejouer)
            Joueur j = this.getJoueurs().getIRemove(0);
            j.setPrio(3);
            this.getJoueurs().add(j);
        }
    }

    public void jeuAQuatre()
    {
        Joueur j1 = this.getJoueurs().getIRemove(0);
        Joueur j2 = this.getJoueurs().getIRemove(0);
        Joueur j3 = this.getJoueurs().getIRemove(0);
        Joueur j4 = this.getJoueurs().getIRemove(0);
        this.getJoueurs().add(j1); // joueur réel 
        this.getJoueurs().add(j4); // Ordi 3
        this.getJoueurs().add(j2); // Ordi 1
        this.getJoueurs().add(j3); // Ordi 2
    }

    public Carte meilleurCarteAJouer(EnsembleCartes possib)
    {
        boolean trouve = false;

        if (this.getJou().getMain().size() > 3)
        {

            //Si prochain joueur a plus que 2 cartes ou moins il essaie de joueur un joker
            if (this.players.getI(1).getMain().size() < 2)
            {
                int i = 0;

                while (i < possib.size() && !trouve)
                {
                    if (possib.getCarte(i) instanceof CarteSpeciale)
                    {
                        CarteSpeciale cS = (CarteSpeciale) possib.getCarte(i);
                        if (cS.jokerOuSuperJoker())
                        {
                            trouve = true;
                            return possib.getCarte(i);
                        }
                    }

                    i++;
                }
                
                return possib.getCarte(0);

                //Si prochain joueur a plus que 4 cartes ou moins l'IA va essayer de joueur une carte speciale
            }
            else if (this.players.getI(1).getMain().size() < 5) 
            {
                int i = 0;

                while (i < possib.size() && !trouve) 
                {
                    if (possib.getCarte(i) instanceof CarteSpeciale) 
                    {
                        trouve = true;
                        return possib.getCarte(i);
                    }

                    i++;
                }
                
                return possib.getCarte(0);

                //Sinon l'IA joue une carte avec une couleur dont il a beaucoup (meme couleur)
            } 
            else 
            {
                return this.getJou().getMain().jouerCartePlusCouleur(possib);
            }

            //Si IA a plus que 3 cartes il veut jouer un joker si possible sinon la carte avec plus de couleur
        } 
        else
        {
            int i = 0;
            if (possib.contientJokerOuSuperJoker()) 
            {
                while (i < possib.size() && !trouve) 
                {
                    if (possib.getCarte(i) instanceof CarteSpeciale) 
                    {
                        CarteSpeciale cS = (CarteSpeciale) possib.getCarte(i);
                        if (cS.jokerOuSuperJoker()) 
                        {
                            trouve = true;
                            return possib.getCarte(i);
                        }
                    }
                    i++;
                }
                return possib.getCarte(0);
            } 
            else
            {
                //Essayer de jouer la couleur la plus presente sinon premier possibilite
                return this.getJou().getMain().jouerCartePlusCouleur(possib);
            }
        }

       
    }

    //Getters
    public EnsembleCartes getPioche() {
        return this.pioche;
    }

    public EnsembleCartes getDefausse() {
        return this.defausse;
    }

    public EnsembleJoueurs getJoueurs() {
        return this.players;
    }

    public JoueurReel getJoueur() {
        return (JoueurReel) this.players.getI(this.players.positionJoueurReel());
    }

    public IA getIA(int i) {
        return (IA) this.players.joueursOrdi().getJoueur("Ordi " + i);
    }

    public Joueur getJou() {
        return this.players.peek();
    }

}
