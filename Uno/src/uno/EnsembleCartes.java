package uno;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author janosfalke
 */
public class EnsembleCartes 
{

    //Attributs
    private ArrayList<Carte> cartes;

    //Constructeur
    EnsembleCartes() 
    {
        this.cartes = new ArrayList();
    }

    //Méthodes
    public boolean estVide() {
        return this.cartes.isEmpty();
    }

    public int size() {
        return this.cartes.size();
    }

    public void ajoutCarte(Carte c) {
        this.cartes.add(c);
    }

    public void remove(Carte c) {
        this.cartes.remove(c);
    }

    public void melanger() {
        Collections.shuffle(this.cartes); //mélanger les éléments de l'ArrayList cartes
    }
    
    public void concatEC(EnsembleCartes eC2){
        while(!eC2.estVide()){
            this.ajoutCarte(eC2.get());
        }
    }

    //Getters
    public Carte getCarte(int i) {
        return this.cartes.get(i);

    }

    public Carte jouerI(int i)
    {
        if (!estVide()) 
        {
            return this.cartes.remove(i);
        }
        return null;

    }

    public Carte get() 
    {
        if (!estVide()) 
        {
            return this.cartes.remove(0);
        }
        return null;
    }

    public boolean peutJouerCarte(Carte c) 
    {
        //Hypothese qu'on ne puisse pas jouer la carte
        boolean peutJouer = false;

        //On travaille seulement avec la derniere carte de la defausse (this)
        Carte cDefausse = this.getCarte(this.size() - 1);

        //Si la defausse est une CarteNormale
        if (cDefausse instanceof CarteNormale) 
        {
            CarteNormale cD = (CarteNormale) cDefausse;

            //Si la carte de la main est une CarteNormale
            if (c instanceof CarteNormale) 
            {
                CarteNormale cN = (CarteNormale) c;

                if (cN.getCouleur().equals(cD.getCouleur()) || cN.getValeur() == cD.getValeur()) 
                {
                    peutJouer = true;
                }

                //Si la carte de la main est une CarteSpeciale
            }
            else 
            {
                CarteSpeciale cS = (CarteSpeciale) c;

                if (cS.getCouleur().equals(cD.getCouleur()) || cS.getEffet().equals("Joker") || cS.getEffet().equals("SuperJoker")) 
                {
                    peutJouer = true;
                }
            }

            
        } 
        else //Si la defausse est une CarteSpeciale
        {
            CarteSpeciale cD = (CarteSpeciale) cDefausse;

            //Si la carte de la main est une CarteNormale
            if (c instanceof CarteNormale) 
            {
                CarteNormale cN = (CarteNormale) c;

                if (cN.getCouleur().equals(cD.getCouleur())) 
                {
                    peutJouer = true;
                }

                //Si la carte de la main est une CarteSpeciale    
            } 
            else 
            {
                CarteSpeciale cS = (CarteSpeciale) c;

                //Si la defausse est un SuperJoker
                if (cD.getEffet().equals("SuperJoker")) 
                {

                    if ((cS.getEffet().equals("Joker")) || (cS.getCouleur().equals(cD.getCouleur()) && !cS.getEffet().equals("SuperJoker"))) 
                    {
                        peutJouer = true;
                    }

                }
                else 
                {

                    if (cS.getCouleur().equals(cD.getCouleur()) || cS.getEffet().equals(cD.getEffet()) || cS.getEffet().equals("Joker") || cS.getEffet().equals("SuperJoker")) 
                    {
                        peutJouer = true;
                    }
                }
            }
        }
        return peutJouer;
    }

    public EnsembleCartes possibilitesJouerIA(EnsembleCartes defausse) 
    {
        EnsembleCartes possibilites = new EnsembleCartes();
        Carte cDefausse = defausse.getCarte(defausse.size() - 1);

        for (int i = 0; i < this.size(); i++) 
        {

            //Si la defausse est une CarteNormale
            if (cDefausse instanceof CarteNormale) 
            {
                CarteNormale cD = (CarteNormale) cDefausse;

                //Si la carte de la main est une CarteNormale
                if (this.getCarte(i) instanceof CarteNormale)
                {
                    CarteNormale cN = (CarteNormale) this.getCarte(i);

                    if (cN.getCouleur().equals(cD.getCouleur()) || cN.getValeur() == cD.getValeur()) 
                    {
                        possibilites.ajoutCarte(cN);
                    }

                    
                } 
                else    //Si la carte de la main est une CarteSpeciale
                {
                    CarteSpeciale cS = (CarteSpeciale) this.getCarte(i);

                    if (cS.getCouleur().equals(cD.getCouleur()) || cS.getEffet().equals("Joker") || cS.getEffet().equals("SuperJoker")) 
                    {
                        possibilites.ajoutCarte(cS);
                    }
                }

                
            } 
            else //Si la defausse est une carte speciale
            {
                CarteSpeciale cD = (CarteSpeciale) cDefausse;

                //Si la carte de la main est une CarteNormale
                if (this.getCarte(i) instanceof CarteNormale) 
                {
                    CarteNormale cN = (CarteNormale) this.getCarte(i);

                    if (cN.getCouleur().equals(cD.getCouleur())) 
                    {
                        possibilites.ajoutCarte(cN);
                    }

                    
                } 
                else    //Si la carte de la main est une CarteSpeciale
                {
                    CarteSpeciale cS = (CarteSpeciale) this.getCarte(i);

                    //Si la defausse est un SuperJoker
                    if (cD.getEffet().equals("SuperJoker")) 
                    {

                        if ((cS.getEffet().equals("Joker")) || (cS.getCouleur().equals(cD.getCouleur()) && !cS.getEffet().equals("SuperJoker"))) 
                        {
                            possibilites.ajoutCarte(cS);
                        }
                    } 
                    else 
                    {

                        if (cS.getCouleur().equals(cD.getCouleur()) || cS.getEffet().equals(cD.getEffet()) || cS.getEffet().equals("SuperJoker") || cS.getEffet().equals("Joker")) 
                        {
                            possibilites.ajoutCarte(cS);
                        }
                    }
                }
            }
        }
        
        //On renvoie l'ensemble de cartes avec les cartes qu'IA peut jouer (si 0 donc l'ensemble est vide)
        return possibilites;
    }
    
    


    // IA : choix de couleure 
    public String choisirCoulJokerIA() 
    {
        int comptRouge = 0;
        int comptBleue = 0;
        int comptVert = 0;
        int comptJaune = 0;

        for (int i = 0; i < this.cartes.size(); i++) 
        {
            switch (this.cartes.get(i).getCouleur()) 
            {
                case "Bleue":
                    comptBleue++;
                break;
                
                case "Rouge":
                    comptRouge++;
                break;
                
                case "Vert":
                    comptVert++;
                break;
                
                case "Jaune":
                    comptJaune++;
                break;
            }
        }

        if (comptRouge >= comptBleue) 
        {
            if (comptRouge >= comptVert) 
            {
                if (comptRouge >= comptJaune) 
                {
                    return "Rouge";
                } 
                else 
                {
                    return "Jaune";
                }
            } 
            else 
            {
                if (comptVert >= comptJaune) 
                {
                    return "Vert";
                } 
                else 
                {
                    return "Jaune";
                }
            }
        } 
        else
        {
            if (comptBleue >= comptJaune) 
            {
                if (comptBleue >= comptVert)
                {
                    return "Bleue";
                } 
                else 
                {
                    return "Vert";
                }
            } 
            else 
            {
                if (comptJaune >= comptVert) 
                {
                    return "Jaune";
                }
                else 
                {
                    return "Vert";
                }
            }
        }
    }
    
    public boolean contientPasseTonTour()
    {
        boolean contient = false;
        int i = 0;
        
        while(i < this.cartes.size() && !contient) 
        {
            if(this.cartes.get(i).estPasseTonTour())
            {
                contient = true;
            }
            i++;
        }
        
        return contient;
    }
    
    public Carte renvoie1stPasseTonTour()
    {
        boolean trouve = false;
        Carte c = this.cartes.get(0);
        int i = 0;
        
        while(i < this.cartes.size() && !trouve)
        {
            if(this.cartes.get(i).estPasseTonTour())
            {
                c = this.cartes.get(i);
                trouve = true;
            }
            i++;
        }
        
        return c;
    }
    
    public boolean contientPrend2()
    {
        boolean contient = false;
        int i = 0;
        
        while(i < this.cartes.size() && !contient)
        {
            if(this.cartes.get(i).estPrend2())
            {
                contient = true;
            }
            i++;
        }
        
        return contient;
    }
    
    public boolean contientJokerOuSuperJoker()
    {
        boolean contient = false;
        int i = 0;
        
        while(i < this.cartes.size() && !contient)
        {
            if(this.cartes.get(i).jokerOuSuperJoker())
            {
                contient = true;
            }
            i++;
        }
        
        return contient;
    }
    
    public Carte renvoie1stPrend2()
    {
        boolean trouve = false;
        Carte c = this.cartes.get(0);
        int i = 0;
        
        while(i < this.cartes.size() && !trouve)
        {
            if(this.cartes.get(i).estPrend2())
            {
                c = this.cartes.get(i);
                trouve = true;
            }
            i++;
        }
        
        return c;
    }
    
    
    public Carte jouerCartePlusCouleur(EnsembleCartes possib)
    {
         switch(this.choisirCoulJokerIA())
         {
                case "Bleue":
                    for (int j = 0; j < possib.size(); j++)
                    {
                        if(possib.getCarte(j).getCouleur().equals("Bleue"))
                        {
                            return possib.getCarte(j);
                        }
                    }
                break;
                
                case "Rouge":
                    for (int j = 0; j < possib.size(); j++)
                    {
                        if(possib.getCarte(j).getCouleur().equals("Rouge"))
                        {
                            return possib.getCarte(j);
                        }
                    }
                break;
                
                case "Vert":
                    for (int j = 0; j < possib.size(); j++)
                    {
                        if(possib.getCarte(j).getCouleur().equals("Vert"))
                        {
                            return possib.getCarte(j);
                        }
                    }
                break;
                
                case "Jaune":
                    for (int j = 0; j < possib.size(); j++) 
                    {
                        if(possib.getCarte(j).getCouleur().equals("Jaune"))
                        {
                            return possib.getCarte(j);
                        }
                    }
                break;
        }
                
                return possib.getCarte(0);
    }
   

    //Affichage
    public void afficher() 
    {
        for (int i = 0; i < this.cartes.size(); i++) 
        {
            this.cartes.get(i).afficher();
        }
    }

    public void chargementCarteNormale(String NomFichier) throws IOException 
    {
        String Couleur = "", Lien = "";
        int Valeur = -1;  // recuperation temporaire de ces infos
        Carte uneCarte;

        FileReader fic = new FileReader(NomFichier);  		//ouverture du fichier (CartesNormalesUno.txt)
        StreamTokenizer entree = new StreamTokenizer(fic);     // intermediaire avec FileReader
        entree.quoteChar('"');

// lecture des donnees dans le fichier connaissant le format-----------------------------------------------------
        int i = 0;
        entree.nextToken();                    // on passe a l'element suivant
        while (entree.ttype != entree.TT_EOF) // c'est la fin du fichier ou pb ?
        {
            Couleur = entree.sval;
            entree.nextToken();
            Lien = entree.sval;
            entree.nextToken();
            Valeur = (int) entree.nval;

            uneCarte = new CarteNormale(Couleur, Lien, Valeur); // nouvelle carte
            cartes.add(uneCarte); //ajouter la carte dans l'ensemble de cartes											

            i++;

            entree.nextToken();
        }  // fin while

        fic.close(); //fermeture du fichier
    }

    public void chargementCarteSpeciale(String NomFichier) throws IOException 
    {
        String Couleur = "", Lien = "";
        String Effet = "";  // recuperation temporaire de ces infos
        Carte uneCarte;

        FileReader fic = new FileReader(NomFichier);  		//ouverture du fichier (CartesSpecialesUno.txt)
        StreamTokenizer entree = new StreamTokenizer(fic);	// intermediaire avec FileReader
        entree.quoteChar('"');

// lecture des donnees dans le fichier connaissant le format-----------------------------------------------------
        int i = 0;
        entree.nextToken();							// on passe a l'element suivant
        while (entree.ttype != entree.TT_EOF) // c'est la fin du fichier ou pb ?
        {
            Couleur = entree.sval;
            entree.nextToken();
            Lien = entree.sval;
            entree.nextToken();
            Effet = entree.sval;
            entree.nextToken();

            uneCarte = new CarteSpeciale(Couleur, Lien, Effet); // nouvelle carte
            cartes.add(uneCarte);   // ajouter la carte dans l'ensemble de cartes											

            i++;

            entree.nextToken();
        }  // fin while

        fic.close();
    }

}
