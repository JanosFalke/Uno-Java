package uno;


/**
 *
 * @author janosfalke
 */
public abstract class Joueur 
{


    //Attributs
    protected String name;
    protected int prio;              //priorité du joueur
    protected EnsembleCartes main;  //la main du joueur

    public Joueur(String name)
    {
        this.name = name;
        this.prio = 0;
        this.main = new EnsembleCartes();
    }
    
    //Setters
    public void setPrio(int prio) {
        this.prio = prio;
    }

    //Getters
    public int getPrio() {
        return this.prio;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String s){
        this.name = s;
    }

    public EnsembleCartes getMain() {
        return this.main;
    }
}

class JoueurReel extends Joueur 
{
    // Constructeur 
    public JoueurReel(String name) 
    {
        super(name);
    }
}

class IA extends Joueur
{

    // Constructeur
    public IA(String name) 
    {
        super(name);
        //this.choixNbCarte = 3; // Pourra mettre un alea (difficulté aleatoire) 
    }

    public boolean contientCarteSpeciale()
    {
        int i = 0;
        boolean contient = false;
        while (i < this.main.size() && contient == false) 
        {
            if (this.main.getCarte(i) instanceof CarteSpeciale) 
            {
                contient = true;
            }
            i++;
        }
        return contient;
    }

    public boolean contientCarteNormale() 
    {
        int i = 0;
        boolean contient = false;
        while (i < this.main.size() && contient == false) 
        {
            if (this.main.getCarte(i) instanceof CarteNormale) 
            {
                contient = true;
            }
            i++;
        }
        return contient;
    }
    

   // public Carte choixCarte(Joueur j) {
   //     if (j.getMain().size() <= this.choixNbCarte) {                // Si le joueur à trois cartes ou moins (donc qu'il se rapproche d'un Uno

   //     }
   // }

}
