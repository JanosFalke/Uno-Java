package uno;

/**
 *
 * @author janosfalke
 */
public abstract class Carte
{

    protected String couleur;   //la couleur
    protected String lien;     //lien vers l'image de la carte

    //constructeur d'une carte
    public Carte(String coul, String lien)
    {
        this.couleur = coul;
        this.lien = "imgCartes/" + lien;
    }

    //Getters
    public String getCouleur() {
        return this.couleur;
    }

    public String getLien() {
        return this.lien;
    }

    //Setters
    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    //Affichage
    public abstract void afficher();

    public abstract boolean jokerOuSuperJoker();
    
    public abstract boolean estPrend2();
    
    public abstract boolean estPasseTonTour();
    
    public abstract boolean estInversion();
    
    public abstract boolean estSuperJoker();

}

class CarteNormale extends Carte
{

    //Attributs
    private int valeur;

    //constructeur d'une carte normale
    public CarteNormale(String coul, String lien, int val) 
    {
        super(coul, lien);
        this.valeur = val;
    }

    //Affichage
    public void afficher() {
        System.out.println("Couleur: " + this.couleur + ", Lien: " + this.lien + ", Valeur: " + this.valeur + "");
    }

    //Getters
    public int getValeur() {
        return this.valeur;
    }

    public boolean jokerOuSuperJoker() {
        return false;
    }
    
    public boolean estPrend2() {
        return false;
    }
    
    public boolean estPasseTonTour() {
        return false;
    }
    
    public boolean estInversion() {
        return false;
    }
    
    public boolean estSuperJoker() {
        return false;
    }
}

class CarteSpeciale extends Carte
{

    //Attributs
    private String effet;

    //constructeur d'une carte spéciale
    public CarteSpeciale(String coul, String lien, String eff) {
        super(coul, lien);
        this.effet = eff;
    }

    //Getters
    public String getEffet() {
        return this.effet;
    }

    //Affichage
    public void afficher() {
        System.out.println("Couleur: " + this.couleur + ", Lien: " + this.lien + ", Effet: " + this.effet + "");
    }

    public boolean jokerOuSuperJoker() 
    {
        boolean trouve = false;

        if (this.getEffet().equals("Joker") || this.getEffet().equals("SuperJoker"))
        {
            trouve = true;
        }

        return trouve;
    }
    public boolean estSuperJoker() 
    {
        return this.getEffet().equals("SuperJoker");
    }

    public boolean estPrend2() 
    {
        return this.getEffet().equals("Prend2");
    }
    
    public boolean estPasseTonTour()
    {
        return this.getEffet().equals("PasseTonTour");
    }
    
    public boolean estInversion() 
    {
        return this.getEffet().equals("Inversion");
    }
}
