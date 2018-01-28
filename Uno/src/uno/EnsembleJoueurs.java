package uno;

import java.util.ArrayList;

/**
 *
 * @author Yazan
 */
public class EnsembleJoueurs 
{
    //Attributs

    private ArrayList<Joueur> joueurs;

    //Constructeur
    EnsembleJoueurs() {
        this.joueurs = new ArrayList();
    }

    public Joueur getEnd() {
        return joueurs.remove(this.joueurs.size() - 1);
    }

    public int getPrioTete() {
        return joueurs.get(0).getPrio();
    }

    public boolean estVide() {
        return this.joueurs.isEmpty();
    }

    public Joueur getJoueur(String s) 
    {
        Joueur j = this.joueurs.get(0);
        int i = 0;
        boolean trouve = false;

        while (i < this.joueurs.size() && !trouve) 
        {
            if (s.equals(this.joueurs.get(i).getName())) 
            {
                trouve = true;
                j = this.joueurs.get(i);
            }
            i++;
        }
        return j;
    }

    public Joueur peek() {
        return this.joueurs.get(0);
    }
    
    public Joueur getI(int i) {
        return this.joueurs.get(i);
    }
    
    public Joueur getIRemove(int i) {
        return this.joueurs.remove(i);
    }

    public Joueur get() 
    {
        if (!estVide()) 
        {
            return this.joueurs.remove(0);
        }
        return null;
    }

    public int size() {
        return this.joueurs.size();
    }

    public void add(Joueur p) 
    {
        int i = 0;
            while ((i<joueurs.size()) && (joueurs.get(i).getPrio() <= p.getPrio() ))
            {
                    i++;
            }
            joueurs.add(i,p);


    }

    public int positionJoueurReel() 
    {
        boolean trouve = false;
        int position = 0;

        for (int i = 0; i < this.joueurs.size(); i++) 
        {
            if (this.joueurs.get(i) instanceof JoueurReel) 
            {
                trouve = true;
                position = i;
            }
        }
        return position;
    }

    public EnsembleJoueurs joueursOrdi() 
    {
        EnsembleJoueurs e = new EnsembleJoueurs();

        for (int i = 0; i < this.joueurs.size(); i++) 
        {
            if (this.joueurs.get(i) instanceof IA) 
            {
                e.add(this.joueurs.get(i));
            }
        }

        return e;
    }
    
    
    public int nbIAaJouer()
    {
        int compt = 0;
        int i = 0;
        boolean stop = false;
        
        while(i < this.joueurs.size() && !stop) 
        {
            if(this.joueurs.get(i) instanceof IA)
            {
                compt++;
            } 
            else 
            {
                stop = true;
            }
                    
            i++;
        }
        
        return compt;
    }
}
