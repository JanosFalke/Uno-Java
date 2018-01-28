package uno;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import sun.audio.*;

public class Fenetre extends JFrame implements MouseListener {

    private JPanel zoneDessin;            // zone de dessin central ou on va dessiner
    private Robot r;

    private JButton regles;             //bouton "Aide" pour afficher les régles du jeu 

    private JButton scoreButton;        //bouton "Score" pour afficher les scores des joueurs
    private JTable score;               //tableau des scores des joueurs
    private JScrollPane menuScore;      //menu pour le score

    private Partie partie;                  //la partie qui contient la pioche, defausse et tous les joueurs, leurs mains
    private Image imDefausse;               //l'image pour la defausse (derniere carte joué)
    private Image imPioche;                 //l'image pour la pioche (toujours la meme carte)
    private ArrayList<Image> imJoueur;      //Pour stocker tous les images des cartes du joueur
    private ArrayList<Image> imOrdi1;        //Pour stocker tous les images des cartes de l'IA numero 1
    private ArrayList<Image> imOrdi2;        //Pour stocker tous les images des cartes de l'IA numero 2
    private ArrayList<Image> imOrdi3;        //Pour stocker tous les images des cartes de l'IA numero 3

    private int startXPhoto;                //Point de depart X de la premiere image des cartes du joueur
    private int startXPhotoOrdi1;           //Point de depart X de la premiere image des cartes de l'ordi 1
    private int startYPhotoOrdi2;           //Point de depart y de la premiere image des cartes de l'ordi 2
    private int startYPhotoOrdi3;           //Point de depart y de la premiere image des cartes de l'ordi 3

    private final int hauteurImage = 170;   //Hauteur des images
    private final int longueurImage = 100;  //Longueur des images

    private int curseur;                    // Le numéro de la carte "actif" (selectionné)
    private int nbJoueurs;                 // Le nombre de joueurs
    private int nbAPiocher;                 // Nombre de cartes a piocher
    private boolean aJoue;                  // Le joueur a joue
    private final int scoreFinal = 500;     // le score maximal qu'on peut avoir (après la partie est términée)
    private boolean gagne;                  // le jeu est gagne ou pas
    private int compteurPrend2;             //compteur des +2
    private int dessineNbPrend2;            //dessiner le compteur des nb +2
    private boolean passeTonTour;           //passe ton tour (vrai ou faux)
    private boolean superjokerJoue;         //super joker joue ou pas
    private boolean dessinePasse;           //dessiner le passe ton tour
    private boolean prend2;                 //prend 2 joue ou pas
    private final int hauteur;              //hauteur de la fenetre
    private final int longueur;             //longueur de la fenetre
    private boolean jouerOuPiocher;         //le joueur a joué ou pioché (ou pas)
    private boolean jokerJoue;              //un joker a ete joué
    private String couleurJoker;            //la couleur souhaite avec le joker
    private int comptJokerJoue;             //compteur des joker joué
    private String nomJoueur;               //le nom du joueur (initialisé au debut avec l'optionpane)

    AudioStream welcome;                //FOR FUN (musique dans l'arriere plan)

    private String gagnant;             // nom du gagnant
    ImageIcon icon;                     // image de fin de partie : "gagnant" ou "perdu"

    // CONSTRUCTEUR 
    public Fenetre(int largeur, int hauteur, boolean firstGame) throws IOException
    {

        super("UNO");

        String gongFile = "Sounds/welcome.wav";
        InputStream in = null;

        try
        {
            in = new FileInputStream(gongFile);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        // create an audiostream from the inputstream
        welcome = null;
        try
        {
            welcome = new AudioStream(in);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.hauteur = hauteur;     // hauteur de la fenêtre
        this.longueur = largeur;    // largeur de la fenêtre

        //Mettre l'arriere plan en blanc
        UIManager UI = new UIManager();
        UI.put("OptionPane.background", Color.white);
        UI.put("Panel.background", Color.white);

        //Charger un icon pour le joptionpane
        ImageIcon icon = new ImageIcon("imgMenu/commencer.jpg");

        AudioPlayer.player.start(welcome);

        Object[] commencer = {"Commencer la partie"};

        JOptionPane.showOptionDialog(zoneDessin, "", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, commencer, "Commencer la partie");

        ImageIcon uno = new ImageIcon("imgMenu/uno.png");

        JOptionPane.showMessageDialog(zoneDessin, "<html><u><b>+++++++++++++++ Bienvenue a notre UNO +++++++++++++++</b></u></html>"
                + "\n\n\n\n \t***************************** Comment jouer *****************************"
                + "\n\n   _______________On peut jouer simplement avec la souris_______________ "
                + "\n<html><ul><li>Une 1. fois appuyer sur les cartes pour les selectionner</li>"
                + "<li>Une 2. fois appuyer sur les cartes pour les jouer</li>"
                + "<li>Cliquer sur la pioche pour piocher</li>"
                + "<li>Les règles sont expliqué dans le bouton AIDE</li></ul></html>", "- Yazan, Louis, Alexis, Janos -", JOptionPane.YES_OPTION, uno);

        // Demander au joueur de saisir son nom
        demanderNom();

        //Charger un icon pour le joptionpane
        ImageIcon icon2 = new ImageIcon("imgMenu/joueurs.jpg");

        //Tableau du nombre des joueurs possible
        String[] nombreJoueurs = {"2", "3", "4"};

        //Sortie du string choisi et le mettre en int puis initialiser le nombre de joueur au nombre choisi
        int num = JOptionPane.showOptionDialog(zoneDessin, "Nombre de joueurs", "Nombre de Joueurs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon2, nombreJoueurs, "4");

        // Initialisation du nombre de joueurs par raport au nombre choisi par l'utilisateur
        switch (num)
        {
            case 0: // [0] => 2
                this.nbJoueurs = 2;
            break;
            
            case 1: // [1] => 3
                this.nbJoueurs = 3;
            break;
            
            case 2: // [2] => 4
                this.nbJoueurs = 4;
            break;
            
            default:
                quitter();
        }

        //Ici on va initialiser la fenetre du jeu de UNO 
        getContentPane().setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        mise_en_page(largeur, hauteur);    // on place les boutons et la zone de dessin

        addMouseListener(this);

        setFocusable(true);

        initArrayListsImages();

        // Initialisation du tableau de scores
        initTableScore();

        //On lance la fonction init() pour initialiser le jeu
        init(largeur, hauteur, firstGame);

        AudioPlayer.player.stop(welcome);

        setLocationRelativeTo(null);
        setVisible(true);

        repaint();

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// MISE EN PAGE ////////////// MISE EN PAGE ////////////// MISE EN PAGE  ////////////// MISE EN PAGE //////////////////// 
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// la fenetre est constituée de trois parties Panel Nord : boutons ; Sud : boutons; Centre: zone de zoneDessin
    public void mise_en_page(int maxX, int maxY)
    {
        //--------------------------------------------------------------------

        //--------------------------------------------------------------------
        //--------------------------------------------------------------------
        this.regles = new JButton();  // bouton Aide
        this.regles.setLayout(null);

        this.scoreButton = new JButton(); // bouton Score
        this.scoreButton.setLayout(null);

        // zone de dessin       
        this.zoneDessin = new JPanel();

        this.zoneDessin.setLayout(null);
        this.zoneDessin.setSize(maxX, maxY);
        this.zoneDessin.setPreferredSize(new Dimension(maxX, maxY));

        getContentPane().add(this.zoneDessin, "Center");  // panel pour zoneDessiner au milieu

        //--------------------------------------------------------------------
        this.scoreButton.addActionListener(new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {

                afficherScore(); // Affichage du score
            }
        });

        this.regles.addActionListener(new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {

                //Charger un icon pour le joptionpane
                ImageIcon icon = new ImageIcon("imgMenu/regles.jpg");
                String reglesString = "";

                if (nbJoueurs == 2) // si 2 joueurs
                {
                    try
                    {
                        reglesString = readFile("fichiersTxt/regles2.txt"); // les regles pour 2 joueurs
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    JOptionPane.showMessageDialog(zoneDessin, reglesString, "Les règles du UNO", JOptionPane.YES_OPTION, icon);

                }
                else // si 3 ou 4 joueurs
                {
                    try
                    {
                        reglesString = readFile("fichiersTxt/regles34.txt"); // les regles pour 3 ou 4 joueurs
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    JOptionPane.showMessageDialog(zoneDessin, reglesString, "Les règles du UNO", JOptionPane.YES_OPTION, icon);
                }

            }
        });

        pack();

    }

    // Accesseur au zoneDessin de la fenêtre
    public Graphics getzoneDessin() {
        return this.zoneDessin.getGraphics();
    }

    // Ajout du bouton Score
    void ajouterScoreButton()
    {
        this.zoneDessin.add(scoreButton);

        // postioning
        this.scoreButton.setText("Score");
        this.scoreButton.setBounds(0, 5, 80, 20);
    }

    // Ajout du bouton Aide
    void ajouterAide()
    {
        this.zoneDessin.add(regles);

        // postioning
        this.regles.setText("Aide");
        this.regles.setBounds(longueur - 80, 5, 80, 20);
    }

    // Demander au joueur de saisir son nom
    void demanderNom()
    {
        ImageIcon iconName = new ImageIcon("imgMenu/name.png");

        String name = (String) JOptionPane.showInputDialog(zoneDessin, "Saisissez votre nom: ", "", JOptionPane.WARNING_MESSAGE, iconName, null, null);

        if (name == null)
        {
            quitter();
        }

        if (name.isEmpty())
        {
            demanderNom();
        } 
        else 
        {
            this.nomJoueur = name;
        }
    }

    // Affichage du score
    void afficherScore() 
    {
        ImageIcon icon = new ImageIcon("imgMenu/score.png");
        JOptionPane.showMessageDialog(zoneDessin, menuScore, "LES SCORES", JOptionPane.YES_OPTION, icon);
    }

    // méthode pour afficher une fenêtre de choix de couleur quand le joueur joue un joker ou un super joker
    void afficherChoixCouleur(Carte c)
    {
        CarteSpeciale cS = (CarteSpeciale) c;

        ImageIcon icon = new ImageIcon("imgMenu/choixCouleurs.jpg");

        //Tableau du nombre des joueurs possible
        Object[] couleurs = {"Bleue", "Vert", "Rouge", "Jaune"};

        //Sortie du string choisi et le mettre en int puis initialiser le nombre de joueur au nombre choisi
        int resultat = JOptionPane.showOptionDialog(zoneDessin, "Choissisez votre couleur", "Choisir couleur", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, couleurs, null);

        jokerJoue = true;

        switch (resultat)
        {
            case 0:
                cS.setCouleur("Bleue");
            break;
            
            case 1:
                cS.setCouleur("Vert");
            break;
            
            case 2:
                cS.setCouleur("Rouge");
            break;
            
            case 3:
                cS.setCouleur("Jaune");
            break;
            
            default:
                afficherChoixCouleur(c);

        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// INITIALISATION DES VARIABLES ////////////// INITIALISATION DES VARIABLES ////////////// INITIALISATION DES VARIABLES// 
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void init(int largeur, int hauteur, boolean firstGame) throws IOException
    {

        initArrayListsImages();

        this.gagne = false;
        this.couleurJoker = "";
        this.aJoue = false;
        this.gagne = false;
        this.superjokerJoue = false;
        this.compteurPrend2 = 0;
        this.dessineNbPrend2 = 0;
        this.passeTonTour = false;
        this.prend2 = false;
        this.jouerOuPiocher = false;
        this.jokerJoue = false;
        this.dessinePasse = false;
        this.comptJokerJoue = 0;
        this.imDefausse = null;
        this.imPioche = null;

        if (firstGame)
        {
            //On cree une partie avec this.nbJoueurs joueurs
            this.partie = new Partie(this.nbJoueurs);
        } 
        else 
        {
            //On réinitiallise la partie précédente
            this.partie.reStart(this.nbJoueurs);
        }

        try 
        {
            //On defint l'image de la defausse et de la pioche
            this.imDefausse = ImageIO.read(new File(this.partie.getDefausse().getCarte(0).getLien()));

            //Si la premiere carte est un superJoker ou un Joker donc on va donner une couleur par defaut
            if (this.partie.getDefausse().getCarte(0).jokerOuSuperJoker()) 
            {
                this.partie.getDefausse().getCarte(0).setCouleur("Bleue");
                recupererIndiceCouleur();
                jokerJoue = true;
            }

            this.imPioche = ImageIO.read(new File("imgCartes/pioche.png"));

            //On ajoute tous les cartes de la mains du joueur a l'arraylist des images de la main du joueur
            for (int i = 0; i < this.partie.getJoueur().getMain().size(); i++) 
            {
                this.imJoueur.add(ImageIO.read(new File(this.partie.getJoueur().getMain().getCarte(i).getLien())));
            }

            //On ajoute tous les cartes de la main de l'IA a l'arraylist des images de la main de l'IA
            switch (this.nbJoueurs) 
            {
                case 2:

                    //images pour Ordi 1
                    for (int i = 0; i < this.partie.getIA(0).getMain().size(); i++)
                    {
                        this.imOrdi1.add(ImageIO.read(new File("imgCartesOrdi/cartesNord.png")));
                    }

                    //Calculer la longueur des images du Ordi 1
                    int sizeImagesOrdi1 = (this.imOrdi1.size() * longueurImage / 2) + 150;

                    //Point de depart de X de la premiere image pour Ordi 1
                    this.startXPhotoOrdi1 = (largeur - sizeImagesOrdi1) / 2;

                break;

                case 3:

                    //images pour Ordi 1
                    for (int i = 0; i < this.partie.getIA(0).getMain().size(); i++)
                    {
                        this.imOrdi1.add(ImageIO.read(new File("imgCartesOrdi/cartesNord.png")));
                    }

                    //images pour Ordi 2
                    for (int i = 0; i < this.partie.getIA(1).getMain().size(); i++)
                    {
                        this.imOrdi2.add(ImageIO.read(new File("imgCartesOrdi/cartesEst.png")));
                    }

                    sizeImagesOrdi1 = (this.imOrdi1.size() * longueurImage / 2) + 150;  //Calculer la longueur des images du Ordi 1
                    this.startXPhotoOrdi1 = (largeur - sizeImagesOrdi1) / 2;            //Point de depart de X de la premiere image pour Ordi 1

                    int sizeImagesOrdi2 = (this.imOrdi2.size() * longueurImage / 2) + longueurImage;    //Calculer la longueur des images du Ordi 2
                    this.startYPhotoOrdi2 = ((hauteur + 50) - sizeImagesOrdi2) / 2;                     //Point de depart de X de la premiere image pour Ordi 2

                break;

                case 4:

                    //images pour Ordi 1
                    for (int i = 0; i < this.partie.getIA(0).getMain().size(); i++) 
                    {
                        this.imOrdi1.add(ImageIO.read(new File("imgCartesOrdi/cartesNord.png")));
                    }

                    //images pour Ordi 2
                    for (int i = 0; i < this.partie.getIA(1).getMain().size(); i++) 
                    {
                        this.imOrdi2.add(ImageIO.read(new File("imgCartesOrdi/cartesOuest.png")));
                    }

                    //images pour Ordi 3
                    for (int i = 0; i < this.partie.getIA(2).getMain().size(); i++)
                    {
                        this.imOrdi3.add(ImageIO.read(new File("imgCartesOrdi/cartesEst.png")));
                    }

                    sizeImagesOrdi1 = (this.imOrdi1.size() * 50) + 150;         //Calculer la longueur des images du Ordi 1
                    this.startXPhotoOrdi1 = (largeur - sizeImagesOrdi1) / 2;    //Point de depart de X de la premiere image pour Ordi 1

                    sizeImagesOrdi2 = (this.imOrdi2.size() * longueurImage / 2) + longueurImage;    //Calculer la longueur des images du Ordi 2
                    this.startYPhotoOrdi2 = (hauteur - sizeImagesOrdi2) / 2;                        //Point de depart de X de la premiere image pour Ordi 2

                    int sizeImagesOrdi3 = (this.imOrdi3.size() * longueurImage / 2) + longueurImage;    //Calculer la longueur des images du Ordi "
                    this.startYPhotoOrdi3 = (hauteur - sizeImagesOrdi3) / 2;                            //Point de depart de X de la premiere image pour Ordi 3

                break;
            }
        }
        catch (IOException ex) {}

        //On initialise la carte selectionné a la moitié des cartes du joueur
        this.curseur = this.imJoueur.size() / 2;

        //Calculer la longueur des images du joueur
        int sizeImagesJoueur = (this.imJoueur.size() * longueurImage / 2) + longueurImage;

        //Point de depart de X de la premiere image 
        this.startXPhoto = (largeur - sizeImagesJoueur) / 2;

        this.partie.getJoueur().setName(nomJoueur);

        //changer priorite de Ordi 3 (car il est à gauche) donc le premier qui joue après les joueur réel
        if (nbJoueurs == 4) 
        {
            this.partie.jeuAQuatre();
        }

        ajouterAide();
        ajouterScoreButton();
        repaint();

        String gongFile = "Sounds/shuffle.wav";
        InputStream in = null;

        try
        {
            in = new FileInputStream(gongFile);
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        // create an audiostream from the inputstream
        AudioStream audioStream = null;
        try 
        {
            audioStream = new AudioStream(in);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        AudioPlayer.player.start(audioStream);

    }

    // les images des cartes des joueurs
    void initArrayListsImages() 
    {
        this.imJoueur = new ArrayList();
        this.imOrdi1 = new ArrayList();
        this.imOrdi2 = new ArrayList();
        this.imOrdi3 = new ArrayList();
    }

    // tableau du scores des joueurs
    void initTableScore() 
    {
        String nom = this.nomJoueur;

        // on intialise le tableau par raport au nombre de joueurs
        switch (nbJoueurs) 
        {
            case 2: // pour 2 joueurs
                this.score = new JTable(new DefaultTableModel(new Object[]{"<html><b>" + nom + "</b></html>", "<html><b>Ordi 1</b></html>"}, 0)); //initialisation du tableau du score
                DefaultTableModel model = (DefaultTableModel) this.score.getModel();

                model.addRow(new Object[]{"0", "0"});
            break;

            case 3: // pour 3 joueurs
                this.score = new JTable(new DefaultTableModel(new Object[]{nom, "Ordi 1", "Ordi 2"}, 0)); //initialisation du tableau du score
                model = (DefaultTableModel) this.score.getModel();

                model.addRow(new Object[]{"0", "0", "0"});
            break;

            case 4: //pour 4 joueurs
                this.score = new JTable(new DefaultTableModel(new Object[]{nom, "Ordi 1", "Ordi 2", "Ordi 3"}, 0)); //initialisation du tableau du score
                model = (DefaultTableModel) this.score.getModel();

                model.addRow(new Object[]{"0", "0", "0", "0"});
            break;
        }

        this.score.setDefaultEditor(Object.class, null);
        this.menuScore = new JScrollPane(this.score);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// DESSINER lA FENETRE ////////////// DESSINER lA FENETRE ////////////// DESSINER lA FENETRE ////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics g) // dessin de la fenêtre générale
    {
        if (this.nbJoueurs > 1 && this.nbJoueurs < 5) 
        {
            g = this.zoneDessin.getGraphics(); // on redessine dans le panel de dessin
            effacer();

            paintComponent(g);
        }
    }

    public void paintComponent(Graphics g)
    {
        int xPhoto = this.startXPhoto;

        // On dessine une premiere carte (la defausse) et on dessine la pioche 
        g.drawImage(imDefausse, (getWidth() / 2), (getHeight() / 2 - (hauteurImage / 2)) - 25, longueurImage, hauteurImage, zoneDessin);
        g.drawImage(imPioche, ((getWidth() / 2) - longueurImage), (getHeight() / 2 - (hauteurImage / 2)) - 25, longueurImage, hauteurImage, zoneDessin);

        if (jokerJoue)
        {
            dessinerCouleurJoker(g);

            if (comptJokerJoue == 1)
            {
                jokerJoue = false;
                comptJokerJoue = 0;
            }
        }

        g.setColor(Color.BLACK);

        //Dessiner les images des cartes de l'ordinateur (IA) et les noms des joueurs
        dessinerCartesIA(g);

        //Dessiner les images des cartes du joueur (avec le curseur donc la photo selectionné)
        for (int i = 0; i < this.imJoueur.size(); i++)
        {
            xPhoto += 50;
            if (this.curseur == i)
            {
                g.drawImage(this.imJoueur.get(i), xPhoto, getHeight() - 150, longueurImage, hauteurImage, zoneDessin);
            }
            else 
            {
                g.drawImage(this.imJoueur.get(i), xPhoto, getHeight() - 130, longueurImage, hauteurImage, zoneDessin);
            }
        }

        dessinerSpecialite(g);
        this.regles.paintImmediately(0, 0, 80, 20);
        this.scoreButton.paintImmediately(0, 0, 80, 20);
    }

    // Dessiner un rectangle pour indiquer la couleur choisie quand on joue un joker/superjoker
    void dessinerCouleurJoker(Graphics g) 
    {
        int x1 = getWidth() / 2;
        int y1 = (getHeight() / 2 - (hauteurImage / 2)) - 25;
        int x2 = x1 + longueurImage;
        int y2 = y1 + hauteurImage;

        switch (this.couleurJoker)
        {
            case "Bleue":
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(5));
                g.setColor(new Color(0, 178, 220));
                g.drawLine(x1, y1, x2, y1);
                g.drawLine(x2, y1, x2, y2);
                g.drawLine(x2, y2, x1, y2);
                g.drawLine(x1, y2, x1, y1);
            break;
            
            case "Rouge":
                g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(5));
                g.setColor(new Color(239, 65, 56));
                g.drawLine(x1, y1, x2, y1);
                g.drawLine(x2, y1, x2, y2);
                g.drawLine(x2, y2, x1, y2);
                g.drawLine(x1, y2, x1, y1);
            break;
            
            case "Vert":
                g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(5));
                g.setColor(new Color(7, 171, 85));
                g.drawLine(x1, y1, x2, y1);
                g.drawLine(x2, y1, x2, y2);
                g.drawLine(x2, y2, x1, y2);
                g.drawLine(x1, y2, x1, y1);
            break;
            
            case "Jaune":
                g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(5));
                g.setColor(new Color(235, 233, 52));
                g.drawLine(x1, y1, x2, y1);
                g.drawLine(x2, y1, x2, y2);
                g.drawLine(x2, y2, x1, y2);
                g.drawLine(x1, y2, x1, y1);
            break;
        }
    }

    // Affichage des cartes des IAs et les noms des joueurs
    void dessinerCartesIA(Graphics g)
    {
        int xPhotoOrdi1 = this.startXPhotoOrdi1;
        int yPhotoOrdi2 = this.startYPhotoOrdi2;
        int yPhotoOrdi3 = this.startYPhotoOrdi3;

        switch (this.nbJoueurs)
        {
            case 2: // partie à 2
                for (int i = 0; i < this.imOrdi1.size(); i++)
                {
                    xPhotoOrdi1 += 50;
                    g.drawImage(this.imOrdi1.get(i), xPhotoOrdi1, -60, longueurImage, hauteurImage, zoneDessin); //images des cartes de l'IA numéro 1
                }

                //Dessiner les noms des 2 joueurs
                if (this.partie.getJou().getName().equals(this.nomJoueur))
                {
                    dessinerMoi(g);
                }
                else if ((this.partie.getJou().getName().equals("Ordi 1"))) 
                {
                    dessinerOrdi1(g);
                }

            break;

            case 3: //partie à 3
                for (int i = 0; i < this.imOrdi1.size(); i++)
                {
                    xPhotoOrdi1 += 50;
                    g.drawImage(this.imOrdi1.get(i), xPhotoOrdi1, -60, longueurImage, hauteurImage, zoneDessin);   //images des cartes de l'IA numéro 1
                }

                for (int i = 0; i < this.imOrdi2.size(); i++) {
                    yPhotoOrdi2 += 50;
                    g.drawImage(this.imOrdi2.get(i), getWidth() - 120, yPhotoOrdi2, hauteurImage, longueurImage, zoneDessin);  //images des cartes de l'IA numéro 2
                }

                //Dessiner les noms des 3 joueurs
                if (this.partie.getJou().getName().equals(this.nomJoueur)) 
                {
                    dessinerMoi(g);
                } 
                else if ((this.partie.getJou().getName().equals("Ordi 1")))
                {
                    dessinerOrdi1(g);
                } 
                else if ((this.partie.getJou().getName().equals("Ordi 2")))
                {
                    dessinerOrdi2(g);
                }

            break;

            case 4:
                for (int i = 0; i < this.imOrdi1.size(); i++)
                {
                    xPhotoOrdi1 += 50;
                    g.drawImage(this.imOrdi1.get(i), xPhotoOrdi1, -60, longueurImage, hauteurImage, zoneDessin);   //images des cartes de l'IA numéro 1
                }

                for (int i = 0; i < this.imOrdi2.size(); i++)
                {
                    yPhotoOrdi2 += 50;
                    g.drawImage(this.imOrdi2.get(i), getWidth() - 120, yPhotoOrdi2, hauteurImage, longueurImage, zoneDessin);  //images des cartes de l'IA numéro 2
                }

                for (int i = 0; i < this.imOrdi3.size(); i++)
                {
                    yPhotoOrdi3 += 50;
                    g.drawImage(this.imOrdi3.get(i), -60, yPhotoOrdi3, hauteurImage, longueurImage, zoneDessin);  //images des cartes de l'IA numéro 3
                }

                //Dessiner les noms des 4 joueurs
                if (this.partie.getJou().getName().equals(this.nomJoueur))
                {
                    dessinerMoi(g);
                } 
                else if ((this.partie.getJou().getName().equals("Ordi 1")))
                {
                    dessinerOrdi1(g);
                }
                else if ((this.partie.getJou().getName().equals("Ordi 2")))
                {
                    dessinerOrdi2(g);
                }
                else if ((this.partie.getJou().getName().equals("Ordi 3")))
                {
                    dessinerOrdi3(g);
                }

        }
    }

    // Affichage du nom de l'effet de la carte spéciale jouée
    void dessinerSpecialite(Graphics g)
    {
        if (this.partie != null) {
            if (this.partie.getDefausse().getCarte(this.partie.getDefausse().size() - 1) instanceof CarteSpeciale
                    && this.partie.getDefausse().size() > 1) 
            {
                CarteSpeciale cS = (CarteSpeciale) this.partie.getDefausse().getCarte(this.partie.getDefausse().size() - 1);

                g.setFont(new Font("default", Font.ITALIC, 18));

                int height = (getHeight() / 2) - 130;
                int widthPrendEtJoker = (getWidth() / 2) - 20;
                int widthInversion = (getWidth() / 2) - 80;
                int widthPasse = (getWidth() / 2) - 60;

                if (this.dessinePasse) 
                {
                    g.drawString("Passe Ton Tour", widthPasse, height);
                    this.dessinePasse = false;

                } 
                else if (this.dessineNbPrend2 > 0) 
                {
                    g.drawString("Pioche " + this.dessineNbPrend2, widthPrendEtJoker, height);
                    this.dessineNbPrend2 = 0;

                } 
                else if (cS.estInversion()) 
                {
                    g.drawString("Changement de sens", widthInversion, height);

                } 
                else if (this.superjokerJoue) 
                {
                    g.drawString("Prend 4", widthPrendEtJoker, height);
                    this.superjokerJoue = false;
                }
            }
        }
    }

    
    void dessinerMoi(Graphics g)
    {
        if (nbJoueurs >= 2)
        {
            int nbLettreNom = this.partie.getJoueur().getName().length();

            g.setFont(new Font("default", Font.BOLD, 18));
            g.drawString("" + this.partie.getJoueur().getName(), (getWidth() / 2) - nbLettreNom * 2, getHeight() - hauteurImage - 10);    //nom du joueur EN GRAS

            g.setFont(new Font("default", Font.PLAIN, 16));
            g.drawString("" + this.partie.getIA(1).getName(), getWidth() / 2, hauteurImage - 30);    // nom de l'IA numéro 1

            if (nbJoueurs >= 3)
            {
                g.drawString("" + this.partie.getIA(2).getName(), getWidth() - hauteurImage - 20, getHeight() / 2);    // nom de l'IA numéro 2
                if (nbJoueurs >= 4)
                {
                    g.drawString("" + this.partie.getIA(3).getName(), hauteurImage - 30, getHeight() / 2);    // nom de l'IA numéro 3
                }
            }
        }
    }

    
    void dessinerOrdi1(Graphics g)
    {
        if (nbJoueurs >= 2)
        {
            int nbLettreNom = this.partie.getJoueur().getName().length();

            g.setFont(new Font("default", Font.PLAIN, 16));
            g.drawString("" + this.partie.getJoueur().getName(), (getWidth() / 2) - nbLettreNom * 2, getHeight() - hauteurImage);    //nom du joueur

            g.setFont(new Font("default", Font.BOLD, 18));
            g.drawString("" + this.partie.getIA(1).getName(), getWidth() / 2, hauteurImage - 30);    // nom de l'IA numéro 1 EN GRAS

            if (nbJoueurs >= 3)
            {
                g.setFont(new Font("default", Font.PLAIN, 16));
                g.drawString("" + this.partie.getIA(2).getName(), getWidth() - hauteurImage - 20, getHeight() / 2);    // nom de l'IA numéro 2
                if (nbJoueurs >= 4)
                {
                    g.drawString("" + this.partie.getIA(3).getName(), hauteurImage - 30, getHeight() / 2);    // nom de l'IA numéro 3
                }
            }
        }
    }

    void dessinerOrdi2(Graphics g) {
        if (nbJoueurs >= 2) {
            int nbLettreNom = this.partie.getJoueur().getName().length();

            g.setFont(new Font("default", Font.PLAIN, 16));
            g.drawString("" + this.partie.getJoueur().getName(), (getWidth() / 2) - nbLettreNom * 2, getHeight() - hauteurImage);    //nom du joueur
            g.drawString("" + this.partie.getIA(1).getName(), getWidth() / 2, hauteurImage - 30);    // nom de l'IA numéro 1

            if (nbJoueurs >= 3) {
                g.setFont(new Font("default", Font.BOLD, 18));
                g.drawString("" + this.partie.getIA(2).getName(), getWidth() - hauteurImage - 20, getHeight() / 2);    // nom de l'IA numéro 2 EN GRAS
                if (nbJoueurs >= 4) {
                    g.setFont(new Font("default", Font.PLAIN, 16));
                    g.drawString("" + this.partie.getIA(3).getName(), hauteurImage - 30, getHeight() / 2);    // nom de l'IA numéro 3
                }
            }
        }
    }

    void dessinerOrdi3(Graphics g) {
        if (nbJoueurs >= 2) {
            int nbLettreNom = this.partie.getJoueur().getName().length();

            g.setFont(new Font("default", Font.PLAIN, 16));
            g.drawString("" + this.partie.getJoueur().getName(), (getWidth() / 2) - nbLettreNom * 2, getHeight() - hauteurImage);    //nom du joueur
            g.drawString("" + this.partie.getIA(1).getName(), getWidth() / 2, hauteurImage - 30);    // nom de l'IA numéro 1
            if (nbJoueurs >= 3) {
                g.drawString("" + this.partie.getIA(2).getName(), getWidth() - hauteurImage - 20, getHeight() / 2);    // nom de l'IA numéro 2

                if (nbJoueurs >= 4) {
                    g.setFont(new Font("default", Font.BOLD, 18));
                    g.drawString("" + this.partie.getIA(3).getName(), hauteurImage - 30, getHeight() / 2);    // nom de l'IA numéro 3 EN GRAS
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// METHODES ////////////// METHODES ////////////// METHODES ////////////// METHODES ////////////// METHODES /////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Procédure d'arrêt
    void quitter() {
        System.exit(0);
    }

    void effacer() {
        Graphics g = this.zoneDessin.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// METHODE JOUER ////////////// METHODE JOUER ////////////// METHODE JOUER ////////////// METHODE JOUER /////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    /*
     * **********************************************************************************************************************
     * **********POUR LE JOUEUR *********** **********POUR LE JOUEUR
     * *********** **********POUR LE JOUEUR ***********
     * **********************************************************************************************************************
     */
    void Joueurjouer(Carte cAJouer) 
    {
        try 
        {
            //On joue la carte et passe le parametre a joué (true)
            this.imJoueur = jouerCarte(cAJouer);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void jouerJoueur(ArrayList<Image> vide) throws IOException
    {
        //On ajoute tous les cartes de la mains du joueur a l'arraylist des images de la main du joueur
        for (int i = 0; i < this.partie.getJoueur().getMain().size(); i++) 
        {
            vide.add(ImageIO.read(new File(this.partie.getJou().getMain().getCarte(i).getLien())));
        }
    }

    /*
     * *******************************************************************************************************************************************
     * ********** POUR L'IA*********** **********POUR L'IA ***********
     * **********POUR L'IA *********** **********POUR L'IA ***********
     * *******************************************************************************************************************************************
     */
    void traitementIA()
    {
        try
        {
            IAjouer(); // méthode suivante
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void IAjouer() throws IOException
    {
        EnsembleCartes possibilites = this.partie.getJou().getMain().possibilitesJouerIA(this.partie.getDefausse()); // ensemble de toutes les cartes qu'il peut jouer 

        if (this.passeTonTour && possibilites.size() > 0)
        {
            switch (this.partie.getJou().getName()) 
            {
                case "Ordi 1":
                    this.imOrdi1 = jouerCarte(possibilites.renvoie1stPasseTonTour()); // essayer de jouer une carte d'effet PTT
                break;
                
                case "Ordi 2":
                    this.imOrdi2 = jouerCarte(possibilites.renvoie1stPasseTonTour()); // essayer de jouer une carte d'effet PTT
                break;
                
                case "Ordi 3":
                    this.imOrdi3 = jouerCarte(possibilites.renvoie1stPasseTonTour()); // essayer de jouer une carte d'effet PTT
                break;
            }
            changerPrio();

            if (possibilites.renvoie1stPasseTonTour() instanceof CarteSpeciale)
            {
                faireSpecialites((CarteSpeciale) possibilites.renvoie1stPasseTonTour()); // faire les changements necessaires
            }

        } 
        else if (this.prend2 && possibilites.size() > 0) 
        {
            switch (this.partie.getJou().getName()) 
            {
                case "Ordi 1":
                    this.imOrdi1 = jouerCarte(possibilites.renvoie1stPrend2()); // essayer de jouer une carte d'effet prend2
                break;
                
                case "Ordi 2":
                    this.imOrdi2 = jouerCarte(possibilites.renvoie1stPrend2()); // essayer de jouer une carte d'effet prend2
                break;
                
                case "Ordi 3":
                    this.imOrdi3 = jouerCarte(possibilites.renvoie1stPrend2()); // essayer de jouer une carte d'effet prend2
                break;
            }
            changerPrio();

            if (possibilites.renvoie1stPrend2() instanceof CarteSpeciale)
            {
                faireSpecialites((CarteSpeciale) possibilites.renvoie1stPrend2());  // faire les changements necessaires
            }

        }
        else 
        {
            if (possibilites.size() > 0) 
            {
                Carte carteAJouer = this.partie.meilleurCarteAJouer(possibilites); // recuperation de la meilleure carte possible à jouer
                
                switch (this.partie.getJou().getName()) 
                {
                    case "Ordi 1":
                        this.imOrdi1 = jouerCarte(carteAJouer); //jouer la meilleure carte possible
                    break;
                    
                    case "Ordi 2":
                        this.imOrdi2 = jouerCarte(carteAJouer); //jouer la meilleure carte possible
                    break;
                    
                    case "Ordi 3":
                        this.imOrdi3 = jouerCarte(carteAJouer); //jouer la meilleure carte possible
                    break;
                }
                
                changerPrio();

                //ce qu'on va faire pour les cartes speciales joués
                if (carteAJouer instanceof CarteSpeciale) 
                {
                    CarteSpeciale cS = (CarteSpeciale) carteAJouer;

                    faireSpecialites(cS);
                }

            } 
            else 
            {
                piocher();
                changerPrio();
            }
        }
    }

    void jouerIA(ArrayList<Image> vide) throws IOException
    {
        switch (this.partie.getJou().getName())
        {
            case "Ordi 1":
                for (int i = 0; i < this.partie.getJou().getMain().size(); i++)
                {
                    vide.add(ImageIO.read(new File("imgCartesOrdi/cartesNord.png")));
                }
            break;
            
            case "Ordi 2":
                for (int i = 0; i < this.partie.getJou().getMain().size(); i++)
                {
                    vide.add(ImageIO.read(new File("imgCartesOrdi/cartesOuest.png")));
                }
            break;
            
            case "Ordi 3":
                for (int i = 0; i < this.partie.getJou().getMain().size(); i++) 
                {
                    vide.add(ImageIO.read(new File("imgCartesOrdi/cartesEst.png")));
                }
            break;

        }
    }

    void choixOrdiCoul(Carte c) 
    {
        CarteSpeciale cS = (CarteSpeciale) c;
        String coulChoisi = this.partie.getJou().getMain().choisirCoulJokerIA(); 

        cS.setCouleur(coulChoisi);
        jokerJoue = true;
    }

    /*
     * ***************************************************************************************************************************
     * ********** JOUER UNE CARTE *********** **********JOUER UNE CARTE
     * *********** ********** JOUER UNE CARTE ***********
     * ***************************************************************************************************************************
     */
    ArrayList<Image> jouerCarte(Carte c) throws IOException 
    {

        this.partie.jouer(c);

        if (jokerJoue && !this.partie.getDefausse().getCarte(this.partie.getDefausse().size() - 1).jokerOuSuperJoker()) 
        {
            this.comptJokerJoue++;
        }

        if (this.partie.getJou() instanceof JoueurReel)
        {
            if (c.jokerOuSuperJoker())
            {
                afficherChoixCouleur(c);
                recupererIndiceCouleur();
            }

            //Remettre bien le point de depart X des images
            this.startXPhoto += 25;

            //Si le curseur est placé sur la derniere carte et on joue cette carte donc 
            //  le curseur va se mettre sur la derniere carte restant
            if (imJoueur.size() - 1 < curseur) {
                curseur -= 1;
            } 
            else
            {
                this.curseur = this.imJoueur.size() / 2;
            }

        } 
        else // si c'est un IA
        {
            if (c.jokerOuSuperJoker()) 
            {
                choixOrdiCoul(c);
                recupererIndiceCouleur();
            }

            switch (this.partie.getJou().getName()) 
            {
                case "Ordi 1":
                    this.startXPhotoOrdi1 += 25;
                break;
                    
                case "Ordi 2":
                    this.startYPhotoOrdi2 += 25;
                break;
                    
                case "Ordi 3":
                    this.startYPhotoOrdi3 += 25;
                break;
            }
        }

        //Supprimer la carte jouer de la main du joueur
        this.partie.getJou().getMain().remove(c);

        ArrayList<Image> vide = new ArrayList();

        if (this.partie.getJou() instanceof JoueurReel) 
        {
            jouerJoueur(vide);
        } 
        else 
        {
            jouerIA(vide);
        }

        //Dessine la carte jouer sur la defausse
        try 
        {
            //Mettre la photo de la defausse mise a jour
            this.imDefausse = ImageIO.read(new File(this.partie.getDefausse().getCarte(this.partie.getDefausse().size() - 1).getLien()));

        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        testergagne();

        return vide;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// METHODE PIOCHER ////////////// METHODE PIOCHER ////////////// METHODE PIOCHER ////////////// METHODE PIOCHER /////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void piocher() 
    {
        //Si la pioche est 0 (vide) donc il faut melanger la defausse dans la pioche
        if (this.partie.piocheVide())
        {
            this.partie.melangerDefausseDansPioche();
        }

        //Si la pioche au moins 1 carte donc on peut piocher
        if (this.partie.getPioche().size() > 0)
        {
            this.partie.piocher();

            if (this.partie.getJou() instanceof JoueurReel) 
            {
                piocherJoueur();
            } 
            else 
            {
                piocherIA();
            }
        }

        repaint();
    }

    void piocherN(int nb)
    {
        if (nb > 0) 
        {
            this.piocher();
            this.piocherN(nb - 1);
        }

    }

    
    
    /*
     * **********************************************************************************************************************
     * **********POUR LE JOUEUR *********** **********POUR LE JOUEUR
     * *********** **********POUR LE JOUEUR ***********
     * **********************************************************************************************************************
     */
    void piocherJoueur() 
    {
        try 
        {
            this.imJoueur.add(ImageIO.read(new File(this.partie.getJou().getMain().getCarte(this.partie.getJou().getMain().size() - 1).getLien())));
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Remettre les images au milieu 
        int sizeImagesJoueur = (this.imJoueur.size() * longueurImage / 2) + 100;

        this.startXPhoto = (getWidth() - sizeImagesJoueur) / 2;

        this.curseur = this.imJoueur.size() / 2;

    }

    
    
    /*
     * *******************************************************************************************************************************************
     * ********** POUR L'IA*********** **********POUR L'IA ***********
     * **********POUR L'IA *********** **********POUR L'IA ***********
     * *******************************************************************************************************************************************
     */
    void piocherIA() 
    {
        switch (this.partie.getJou().getName())
        {
            case "Ordi 1":
                try 
                {
                    this.imOrdi1.add(ImageIO.read(new File("imgCartesOrdi/cartesNord.png")));
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                }   
                
                //Calcul pour mettres les photos, pareil que pour le joueur a part le Y qui est fixe
                int sizeImagesOrdi1 = (this.imOrdi1.size() * longueurImage / 2) + 150;
                this.startXPhotoOrdi1 = (getWidth() - sizeImagesOrdi1) / 2;
            break;
            
            case "Ordi 2":
                try 
                {
                    this.imOrdi2.add(ImageIO.read(new File("imgCartesOrdi/cartesOuest.png")));
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                int sizeImagesOrdi2 = (this.imOrdi2.size() * longueurImage / 2) + 200;
                this.startYPhotoOrdi2 = ((getHeight() + 50) - sizeImagesOrdi2) / 2;
            break;
            
            case "Ordi 3": 
                try
                {
                    this.imOrdi3.add(ImageIO.read(new File("imgCartesOrdi/cartesEst.png")));
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                int sizeImagesOrdi3 = (this.imOrdi3.size() * longueurImage / 2) + 200;
                this.startYPhotoOrdi3 = ((getHeight() + 50) - sizeImagesOrdi3) / 2;
            break;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// AUTRES METHODES ////////////// AUTRES METHODES ////////////// AUTRES METHODES ////////////// AUTRES METHODES /////////////////
    /////////////////////RECUPERATION INDICE COULEUR///////////////////////////CHANGEMENT PRIORITE/////////////////////////////////////////////// 
    ///////////////////////////////////////////////FAIRE SPECIALITES CARTES//////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void recupererIndiceCouleur()
    {
        this.couleurJoker = "" + this.partie.getDefausse().getCarte(this.partie.getDefausse().size() - 1).getCouleur();
    }

    void changerPrio()
    {
        //Changer l'ordre de la priorite
        Joueur j = this.partie.getJoueurs().getIRemove(0);
        j.setPrio(3);
        this.partie.getJoueurs().add(j);
    }

    void faireSpecialites(CarteSpeciale cS)
    {
        if (cS.getEffet().equals("SuperJoker")) 
        {
            this.superjokerJoue = true;
            piocherN(4);
            changerPrio();
        }

        if (cS.getEffet().equals("Prend2")) 
        {
            this.compteurPrend2 += 2;

            if (!this.partie.getJou().getMain().contientPrend2())
            {
                this.dessineNbPrend2 = compteurPrend2;
                piocherN(this.compteurPrend2);
                this.prend2 = false;
                this.compteurPrend2 = 0;
                changerPrio();
            } 
            else 
            {
                this.prend2 = true;
            }
        }

        if (cS.getEffet().equals("Inversion")) 
        {
            this.partie.inversion(nbJoueurs);
        }

        if (cS.getEffet().equals("PasseTonTour")) 
        {
            if (!this.partie.getJou().getMain().contientPasseTonTour()) 
            {
                this.passeTonTour = false;
                this.dessinePasse = true;
                changerPrio();
            } 
            else 
            {
                this.passeTonTour = true;
            }
        }

    }

    private String readFile(String file) throws IOException 
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try 
        {
            while ((line = reader.readLine()) != null) 
            {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        }
        finally
        {
            reader.close();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// METHODE TRAITEMENT GAGN-(ER/ANT) ////////////// METHODE TRAITEMENT GAGN-(ER/ANT) ////////////// METHODE TRAITEMENT GAGN-(ER/ANT) //////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// ////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void testergagne() 
    {
        for (int i = 0; i < this.partie.getJoueurs().size(); i++) 
        {
            if (this.partie.getJoueurs().getI(i).getMain().size() == 0) 
            {
                this.gagne = true;
            }
        }

    }

    boolean testerGrandGagnant() 
    {
        int scoreJoueur;
        int scoreIA1;
        int scoreIA2;
        int scoreIA3;

        switch (nbJoueurs)
        {
            case 2:
                scoreJoueur = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 0));
                scoreIA1 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 1));

                if (scoreJoueur >= scoreFinal)
                {
                    return true;
                } 
                else 
                {
                    if (scoreIA1 >= scoreFinal) 
                    {
                        return true;
                    }
                    return false;
                }
            
            case 3:
                scoreJoueur = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 0));
                scoreIA1 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 1));
                scoreIA2 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 2));

                if (scoreJoueur >= scoreFinal)
                {
                    return true;
                }
                else 
                {
                    if (scoreIA1 >= scoreFinal) 
                    {
                        return true;
                    } 
                    else 
                    {
                        if (scoreIA2 >= scoreFinal) 
                        {
                            return true;
                        } 
                        else 
                        {
                            return false;
                        }
                    }
                }

            case 4:
                scoreJoueur = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 0));
                scoreIA1 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 1));
                scoreIA2 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 2));
                scoreIA3 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 3));
                
                if (scoreJoueur >= scoreFinal) 
                {
                    return true;
                } 
                else 
                {
                    if (scoreIA1 >= scoreFinal) 
                    {
                        return true;
                    }
                    else
                    {
                        if (scoreIA2 >= scoreFinal) 
                        {
                            return true;
                        }
                        else 
                        {
                            if (scoreIA3 >= scoreFinal) 
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                }

            default:
                return false;
        }
    }

    void afficherGrandGagnant() 
    {
        String gagnant = "";
        int scoreJoueur;
        int scoreIA1;
        int scoreIA2;

        switch (nbJoueurs)
        {
            case 2:
                scoreJoueur = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 0));

                if (scoreJoueur >= scoreFinal) 
                {
                    gagnant = (String) this.score.getColumnName(0);
                } 
                else
                {
                    gagnant = (String) this.score.getColumnName(1);
                }
            break;
                
            case 3:
                scoreJoueur = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 0));
                scoreIA1 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 1));

                if (scoreJoueur >= scoreFinal) 
                {
                    gagnant = (String) this.score.getColumnName(0);
                } 
                else if (scoreIA1 >= scoreFinal) 
                {
                    gagnant = (String) this.score.getColumnName(2);
                } 
                else 
                {
                    gagnant = (String) this.score.getColumnName(3);
                }
            break;
            
            case 4:
                scoreJoueur = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 0));
                scoreIA1 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 1));
                scoreIA2 = Integer.parseInt((String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 2));

                if (scoreJoueur >= scoreFinal) 
                {
                    gagnant = (String) this.score.getColumnName(0);
                } 
                else if (scoreIA1 >= scoreFinal) 
                {
                    gagnant = (String) this.score.getColumnName(1);
                } 
                else if (scoreIA2 >= scoreFinal) 
                {
                    gagnant = (String) this.score.getColumnName(2);
                } 
                else 
                {
                    gagnant = (String) this.score.getColumnName(3);
                }
                break;
        }

        ImageIcon perdu = new ImageIcon("imgMenu/perdu.jpg");
        ImageIcon gagne = new ImageIcon("imgMenu/gagne.jpg");

        Object[] abientot = {"A Bientôt & Quitter"};

        if (gagnant.equals(this.nomJoueur)) 
        {
            int res = JOptionPane.showOptionDialog(zoneDessin,
                    "************** FELICITATIONS **************"
                    + "\n\n\nVous avec gagné la partie complète", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, gagne, abientot, null);
            if (res == 0) 
            {
                afficherScore();
                quitter();
            } 
            else 
            {
                quitter();
            }

        }
        else
        {
            int res = JOptionPane.showOptionDialog(zoneDessin,
                    "***************** DOMMAGE *****************"
                    + "\n\n\nVous avez malheursement perdu la partie complète", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, perdu, abientot, null);
            if (res == 0)
            {
                afficherScore();
                quitter();
            } 
            else 
            {
                quitter();
            }
        }

    }

    void traitementGagnant() 
    {
        if (gagne) 
        {

            traitementScore();

            if (testerGrandGagnant()) 
            {
                afficherGrandGagnant();
            } 
            else 
            {
                Joueur vainqueur = this.partie.getJou();

                for (int i = 0; i < this.partie.getJoueurs().size(); i++) 
                {
                    if (this.partie.getJoueurs().getI(i).getMain().size() == 0) 
                    {
                        vainqueur = this.partie.getJoueurs().getI(i);
                    }
                }

                if (vainqueur instanceof IA) 
                {
                    this.gagnant = "Vous Avez Perdu !\nLe gagnant est:\n" + vainqueur.getName();

                    //Charger l'icon "perdant" pour le joptionpane
                    this.icon = new ImageIcon("imgMenu/perdre.jpg");
                } 
                else 
                {
                    this.gagnant = "FELICITATIONS\n" + vainqueur.getName() + "\nVous Avez Gagné !";

                    //Charger l'icon "gagnant" pour le joptionpane
                    this.icon = new ImageIcon("imgMenu/gagne.jpg");
                }

                int reponse = JOptionPane.showOptionDialog(null, this.gagnant, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        this.icon, new String[]{"Rejouer", "Quitter"}, "default");

                if (reponse == JOptionPane.YES_OPTION) 
                {

                    afficherScore();

                    effacer();
                    try
                    {
                        init(this.longueur, this.hauteur, false);
                    } 
                    catch (IOException ex) {}

                } 
                else 
                {
                    afficherScore();
                    quitter();
                }
            }
        }
    }

    void traitementScore() 
    {
        String scoreJoueur = (String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 0);
        String scoreIA1 = (String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 1);
        String scoreIA2 = "0";
        String scoreIA3 = "0";

        switch (nbJoueurs)
        {
            case 3:
                scoreIA2 = (String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 2);
            break;

            case 4:
                scoreIA2 = (String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 2);
                scoreIA3 = (String) this.score.getValueAt(this.score.getModel().getRowCount() - 1, 3);
            break;
        }

        // Calcule du score
        for (int i = 0; i < this.partie.getJoueurs().size(); i++) 
        {
            switch (this.partie.getJoueurs().getI(i).getName()) 
            {
                case "Ordi 1":
                    scoreIA1 = calculScore(this.partie.getJoueurs().getI(i), scoreIA1);
                break;
                
                case "Ordi 2":
                    scoreIA2 = calculScore(this.partie.getJoueurs().getI(i), scoreIA2);
                break;
                
                case "Ordi 3":
                    scoreIA3 = calculScore(this.partie.getJoueurs().getI(i), scoreIA3);
                break;
                
                default:
                    scoreJoueur = calculScore(this.partie.getJoueurs().getI(i), scoreJoueur);
                break;
            }
        }

        //Ajouter le score a notre tableau de score
        DefaultTableModel model = (DefaultTableModel) this.score.getModel();
        switch (nbJoueurs)
        {
            case 2:
                model.addRow(new Object[]{scoreJoueur, scoreIA1});
            break;
            
            case 3:
                model.addRow(new Object[]{scoreJoueur, scoreIA1, scoreIA2});
            break;

            case 4:
                model.addRow(new Object[]{scoreJoueur, scoreIA1, scoreIA2, scoreIA3});
            break;

        }
    }

    String calculScore(Joueur j, String score) 
    {
        int sc = Integer.parseInt(score);
        int compt = 0 + sc;

        if (j.getMain().size() == 0)
        {
            for (int i = 0; i < this.partie.getJoueurs().size(); i++) 
            {
                if (this.partie.getJoueurs().getI(i).getMain().size() != 0) 
                {

                    for (int k = 0; k < this.partie.getJoueurs().getI(i).getMain().size(); k++) 
                    {
                        if (this.partie.getJoueurs().getI(i).getMain().getCarte(k) instanceof CarteSpeciale) 
                        {
                            CarteSpeciale cS = (CarteSpeciale) this.partie.getJoueurs().getI(i).getMain().getCarte(k);

                            if (cS.jokerOuSuperJoker()) 
                            {
                                compt += 50;
                            } 
                            else
                            {
                                compt += 20;
                            }
                        } 
                        else 
                        {
                            CarteNormale cN = (CarteNormale) this.partie.getJoueurs().getI(i).getMain().getCarte(k);

                            compt += cN.getValeur();
                        }
                    }
                }
            }

            String res = "" + compt;
            return res;
        }
        else 
        {
            String res = "" + compt;
            return res;
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// EVENEMENT MOUSE ////////////// EVENEMENT MOUSE////////////// EVENEMENT MOUSE ////////////// EVENEMENT MOUSE //////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e)
    {

        String gongFile = "Sounds/cardPlaying.wav";
        InputStream in = null;

        try 
        {
            in = new FileInputStream(gongFile);
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        // create an audiostream from the inputstream
        AudioStream audioStream = null;
        try 
        {
            audioStream = new AudioStream(in);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (jouerOuPiocher) 
        {
            if (this.partie.getJou() instanceof IA || this.prend2) 
            {

                try
                {
                    Thread.sleep(1700);
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        jouerOuPiocher = false;

        try 
        {
            r = new Robot();
        } 
        catch (AWTException ex) 
        {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.aJoue == true) 
        {
            int nbIAaJouer = this.partie.getJoueurs().nbIAaJouer();

            if (nbIAaJouer > 0) 
            {
                traitementIA();
                jouerOuPiocher = true;

                nbIAaJouer = this.partie.getJoueurs().nbIAaJouer();

                if (nbIAaJouer == 0) 
                {
                    this.aJoue = false;
                    jouerOuPiocher = false;
                    this.regles.setEnabled(true);
                    this.scoreButton.setEnabled(true);

                    AudioPlayer.player.start(audioStream);

                }
            } 
            else
            {
                this.regles.setEnabled(true);
                this.scoreButton.setEnabled(true);
                this.aJoue = false;
                jouerOuPiocher = false;

                AudioPlayer.player.start(audioStream);
            }

        }
        else 
        {

            //Piocher si on appuie sur la pioche
            if (((e.getX() > getWidth() / 2 - longueurImage && e.getX() < getWidth() / 2) && (e.getY() > (getHeight() / 2 - (hauteurImage / 2)) && e.getY() < ((getHeight() / 2) + (hauteurImage / 2))))
                    && !this.passeTonTour && !this.prend2)
            {
                this.piocher();

                jouerOuPiocher = true;
                changerPrio();
                this.aJoue = true;
            }

            int xPremiereImage = this.startXPhoto + 50;
            boolean trouve = false;
            boolean deuxfois = true;

            int i = 0;
            int largeurImage = 50;

            //Selectionner une carte et la jouer avec la souris
            //Parcourir tous les images 
            while (this.imJoueur.size() > i && !trouve && !aJoue) 
            {

                //Calculer le xMin et xMax de l'image ou on a cliqué
                int xMinImage = xPremiereImage + (i * largeurImage);
                int xMaxImage = xPremiereImage + largeurImage + (i * largeurImage);
                int yMax = getHeight() + 30;
                int yMin = getHeight() - 100;

                if (i == this.imJoueur.size() - 1)
                {
                    xMaxImage += 50;
                }
                
                //Comparer le X cliqué avec le xMin et xMax 
                if (((e.getX() > xMinImage) && (e.getX() < xMaxImage)) && ((e.getY() > yMin) && (e.getY() < yMax))) 
                {

                    //Si la carte selectionne avant correspond a la meme carte clique donc on va la jouer
                    if (this.curseur != i) 
                    {
                        //On met le curseur a l'indice de l'image
                        this.curseur = i;

                        //On arrete la boucle while dès qu'on a trouvé la bonne image
                        trouve = true;

                        deuxfois = false;
                    }

                }
                if (((e.getX() > xMinImage) && (e.getX() < xMaxImage)) && ((e.getY() > yMin - 15) && (e.getY() < yMax)) && deuxfois) 
                {
                    //Si la carte selectionne avant correspond a la meme carte clique donc on va la jouer
                    if (this.curseur == i) 
                    {

                        int tailleCartes = this.partie.getJou().getMain().size();

                        if (this.partie.getJou() instanceof JoueurReel) 
                        {

                            Carte cAJouer = this.partie.getJoueur().getMain().getCarte(this.curseur);

                            //Tester si on peut jouer la carte choisi
                            if (this.partie.getDefausse().peutJouerCarte(cAJouer)) 
                            {

                                if (this.passeTonTour)
                                {
                                    if (cAJouer instanceof CarteSpeciale) 
                                    {
                                        CarteSpeciale cS = (CarteSpeciale) cAJouer;

                                        if (cS.estPasseTonTour())
                                        {
                                            Joueurjouer(cAJouer);

                                            changerPrio();

                                            faireSpecialites((CarteSpeciale) cAJouer);
                                            this.aJoue = true;
                                        }
                                    }

                                } 
                                else if (this.prend2) 
                                {
                                    if (cAJouer instanceof CarteSpeciale)
                                    {
                                        CarteSpeciale cS = (CarteSpeciale) cAJouer;

                                        if (cS.estPrend2()) 
                                        {
                                            Joueurjouer(cAJouer);

                                            changerPrio();

                                            faireSpecialites((CarteSpeciale) cAJouer);
                                            this.aJoue = true;
                                        }
                                    }
                                } 
                                else
                                {
                                    Joueurjouer(cAJouer);

                                    changerPrio();

                                    if (cAJouer instanceof CarteSpeciale)
                                    {
                                        faireSpecialites((CarteSpeciale) cAJouer);
                                    }

                                    this.aJoue = true;
                                }

                                jouerOuPiocher = true;
                            }

                        }
                    }

                }

                i++;

            }

        }

        // open the sound file as a Java input stream
        repaint();

        if (gagne) 
        {
            traitementGagnant();
        }

        if (jouerOuPiocher) 
        {
            this.scoreButton.setEnabled(false);
            this.regles.setEnabled(false);

            AudioPlayer.player.start(audioStream);

            r.mousePress(InputEvent.BUTTON1_MASK);
            r.mouseRelease(InputEvent.BUTTON1_MASK);

        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
