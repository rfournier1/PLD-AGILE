/*
 * Projet Deliverif
 *
 * Hexanome n° 41
 *
 * Projet développé dans le cadre du cours "Conception Orientée Objet
 * et développement logiciel AGILE".
 */
package deliverif;

import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;

import javafx.scene.input.MouseButton;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import modele.outils.Chemin;
import modele.outils.GestionLivraison;
import modele.outils.Tournee;
import modele.outils.Troncon;

/**
 * Classe implémentant le composant de Vue Graphique de l'IHM du projet ainsi que son comportement.
 * La Vue Graphique représente la description graphique des tournées de livraison à effectuer par les livreurs, ainsi que du plande la ville.
 * @author Aurelien Belin
 * @see StackPane
 * @see Deliverif
 * @see Observer
 */
public class VueGraphique extends StackPane implements Observer {
    
    private final GestionLivraison gestionLivraison;
    private double echelleLat;
    private double echelleLong;
    private double origineLatitude;
    private double origineLongitude;
    private double origineLatitudeMin;
    private double origineLongitudeMin;
    private double echelleLatitudeMin;
    private double echelleLongitudeMin;
    private Deliverif fenetre;
    
    //Composants
    private final Color[] couleurs = {Color.BLUEVIOLET, Color.BROWN, Color.CHARTREUSE,Color.CORAL,Color.CRIMSON,Color.DARKBLUE, Color.DARKGREEN, Color.DEEPPINK, Color.GOLD, Color.LIGHTSALMON};
    private Canvas plan;
    private Canvas dl;
    private ArrayList<Canvas> tournees;
    private Canvas marker;
    private Image imageMarker;
    private Pair<Double, Double> positionMarker;

    /**
     * Constructeur de VueGraphique.
     * @param gl - point d'entrée du modèle observé
     * @param f - IHM principale dans laquelle est affichée la Vue Graphique
     * @see GestionLivraison
     * @see Deliverif
     */
    public VueGraphique(GestionLivraison gl, Deliverif f){
        super();
        this.fenetre=f;
        this.setPrefSize(640,640-95);
        
        this.gestionLivraison = gl;
        this.fenetre = f;
        gestionLivraison.addObserver(this);
        this.fenetre = f;
        
        plan = new Canvas(640,640-95);
        dl = new Canvas(640,640-95);
        tournees = new ArrayList<>();
        
        this.getChildren().addAll(plan,dl);
        
        imageMarker = new Image("/deliverif/Marker_1.png",true);
        this.marker = new Canvas(640,640-95);
        this.fenetre = f;
    }
    
    /**
     * Met à jour la VueGraphique en fonction des données du modèle et de ses mises à jour.
     * @param o - Objet à observer, ici une instance de GestionLivraison
     * @param arg - contenu de la mise à jour 
     */
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(arg instanceof modele.outils.PlanVille){
                    calculEchelle(gestionLivraison.getPlan().getIntersections());
                    dessinerPlan();
                } else if (arg instanceof modele.outils.DemandeLivraison){
                    dessinerPtLivraison();
                } else if (arg instanceof modele.outils.Tournee[]){
                    dessinerTournees();
                }
            }
        });
    }
    
    /**
     * Calcule l'échelle d'affichage du plan de la ville à afficher.
     * @param intersections - liste des intersections contenues dans le plan à afficher.
     */
    public void calculEchelle (List <modele.outils.Intersection> intersections) {
        float maxLatitude = -90;
        float minLatitude = 90;
        float maxLongitude = -180;
        float minLongitude = 180;
        
        for(modele.outils.Intersection i: intersections){
            if(i.getLatitude() > maxLatitude){
                maxLatitude = i.getLatitude();
            }else if(i.getLatitude() < minLatitude){
                minLatitude = i.getLatitude();
            }
            if(i.getLongitude() > maxLongitude){
                maxLongitude = i.getLongitude();
            }else if(i.getLongitude() < minLongitude){
                minLongitude = i.getLongitude();
            }
        }
        
        echelleLat = (640-95)/(maxLatitude-minLatitude);
        echelleLong = (640)/(maxLongitude-minLongitude); //longueur fenetre
        
        echelleLatitudeMin = echelleLat;
        echelleLongitudeMin = echelleLong;
        
        origineLatitude = minLatitude;
        origineLongitude = minLongitude;
        
        origineLatitudeMin = minLatitude;
        origineLongitudeMin = minLongitude;
    }
    
    /**
     * Dessine le plan à l'échelle dans la VueGraphique.
     */
    public void dessinerPlan(){      
        GraphicsContext gc = this.plan.getGraphicsContext2D();
        gc.clearRect(0, 0, plan.getWidth(), plan.getHeight());
            
        GraphicsContext gc1 = this.dl.getGraphicsContext2D();
        gc1.clearRect(0, 0, dl.getWidth(), dl.getHeight());
        
        //this.tournees.clear();
        
        Iterator<Node> iter = this.getChildren().iterator();
        while(iter.hasNext()) {
            Node n = iter.next();
            if( !n.equals(dl) && !n.equals(plan)){
                iter.remove();
            }
        }
            
        gc.setStroke(Color.SLATEGREY);
        List <modele.outils.Troncon> troncons = gestionLivraison.getPlan().getTroncons();
                
        for(modele.outils.Troncon troncon : troncons){
            int absDebutTroncon =(int) ((troncon.getDebut().getLongitude() - origineLongitude) * echelleLong); 
            int ordDebutTroncon =(int) (this.getHeight() - (troncon.getDebut().getLatitude() - origineLatitude) * echelleLat); 
            int absFinTroncon = (int)((troncon.getFin().getLongitude() - origineLongitude) * echelleLong); 
            int ordFinTroncon = (int)(this.getHeight()- (troncon.getFin().getLatitude() - origineLatitude) * echelleLat);
            
            //Dessin des traits
            gc.strokeLine(absDebutTroncon,ordDebutTroncon,absFinTroncon,ordFinTroncon);
        }
        
    }
    
    /**
     * Dessine les points de livraison à desservir sur le plan préalablement dessiné.
     */
    public void dessinerPtLivraison(){
        GraphicsContext gc = this.dl.getGraphicsContext2D();
        gc.clearRect(0, 0, dl.getWidth(), dl.getHeight());
        
        //this.tournees.clear();
        
        Iterator<Node> iter = this.getChildren().iterator();
        while(iter.hasNext()) {
            Node n = iter.next();
            if( !n.equals(dl) && !n.equals(plan)){
                iter.remove();
            }
        }
        
        List <modele.outils.PointPassage> livraisons = gestionLivraison.getDemande().getLivraisons();
        
        for(modele.outils.PointPassage livraison : livraisons){
            double[] ptLivraison = { 
                                    livraison.getPosition().getLongitude(),
                                    livraison.getPosition().getLatitude()
            };
            ptLivraison = this.mettreCoordonneesALechelle(ptLivraison, false);

            //Dessin marqueur
            gc.setFill(Color.BLUE);
            gc.fillOval(ptLivraison[0]-4, ptLivraison[1]-4, 8, 8);
   
        }
        
        double[] ptLivraison = { 
                                    gestionLivraison.getDemande().getEntrepot().getPosition().getLongitude(),
                                    gestionLivraison.getDemande().getEntrepot().getPosition().getLatitude()
            };
        
        ptLivraison = this.mettreCoordonneesALechelle(ptLivraison, false);
        gc.setFill(Color.RED);
        
        gc.fillOval(ptLivraison[0]-4, ptLivraison[1]-4, 8, 8);

        
    }
    
    /**
     * Dessine les tournées à effectuer pour desservir tous les points de livraison préalablement affichée sur le plan.
     */
    public void dessinerTournees(){
        Tournee[] listeTournees = this.gestionLivraison.getTournees();
        int nCouleur=0;
        int i=0;
        
        if(listeTournees!=null){
            for(Tournee tournee : listeTournees){            
                GraphicsContext gc = this.tournees.get(i).getGraphicsContext2D();
                gc.clearRect(0, 0, this.getWidth(), this.getHeight());

                if(tournee != null){
                    List<Chemin> chemins = tournee.getTrajet();

                    //Changer de couleur
                    /*int couleur = (int)(Math.random()*0xFFFFFF);
                    String couleur_hex = Integer.toHexString(couleur);
                    gc.setStroke(Color.web("#"+couleur_hex.substring(2,couleur_hex.length())));*/
                    gc.setLineWidth(3);
                    gc.setStroke(couleurs[nCouleur]);
                    gc.setLineDashes(0,0);

                    for(Chemin chemin : chemins){
                        List<Troncon> troncons = chemin.getTroncons();

                        for(Troncon troncon : troncons){
                           int absDebutTroncon =(int) ((troncon.getDebut().getLongitude() - origineLongitude) * echelleLong); 
                           int ordDebutTroncon =(int) (this.getHeight() - (troncon.getDebut().getLatitude() - origineLatitude) * echelleLat); 
                           int absFinTroncon = (int)((troncon.getFin().getLongitude() - origineLongitude) * echelleLong); 
                           int ordFinTroncon = (int)(this.getHeight()- (troncon.getFin().getLatitude() - origineLatitude) * echelleLat);

                            gc.strokeLine(absDebutTroncon,ordDebutTroncon,absFinTroncon,ordFinTroncon);
                        }
                    }

                    this.getChildren().remove(this.tournees.get(i));
                    this.getChildren().add(i+1, this.tournees.get(i));
                }

                i++;
                nCouleur++;
            }
        }
        
        fenetre.informationEnCours("");
    }
    
    /**
     * Dessine les tournées à effectuer pour desservir tous les points de livraison préalablement affichée sur le plan.
     * Mets en valeur la tournée selectionnée par rapport aux autres.
     * @param numTournee - la tournée à mettre en valeur
     */
    public void dessinerTournees(int numTournee){
        Tournee[] listeTournees = this.gestionLivraison.getTournees();
        int nCouleur=0;
        int i=0;
        
        if(listeTournees!=null){
            for(Tournee tournee : listeTournees){
                GraphicsContext gc = this.tournees.get(i).getGraphicsContext2D();
                gc.clearRect(0, 0, this.getWidth(), this.getHeight());

                if(tournee != null){
                    List<Chemin> chemins = tournee.getTrajet();

                    gc.setLineWidth(3);

                    if(i==(numTournee-1) || numTournee==0){
                        gc.setStroke(couleurs[nCouleur]);
                        gc.setLineDashes(0,0);
                    }else{
                        gc.setStroke(Color.rgb(0,0,0,0.5)); //A modifier : mettre la couleur du livreur, à 0.5 de transparence et en pointillé
                        gc.setLineDashes(5,5);
                    }

                    for(Chemin chemin : chemins){
                        List<Troncon> troncons = chemin.getTroncons();

                        for(Troncon troncon : troncons){
                           int absDebutTroncon =(int) ((troncon.getDebut().getLongitude() - origineLongitude) * echelleLong); 
                           int ordDebutTroncon =(int) (this.getHeight() - (troncon.getDebut().getLatitude() - origineLatitude) * echelleLat); 
                           int absFinTroncon = (int)((troncon.getFin().getLongitude() - origineLongitude) * echelleLong); 
                           int ordFinTroncon = (int)(this.getHeight()- (troncon.getFin().getLatitude() - origineLatitude) * echelleLat);

                            gc.strokeLine(absDebutTroncon,ordDebutTroncon,absFinTroncon,ordFinTroncon);
                        }
                    }

                    this.getChildren().remove(this.tournees.get(i));
                    this.getChildren().add(i+1, this.tournees.get(i));
                }
                
                i++;
                nCouleur++;
            }

            //On passe à l'affichage la tournée choisie devant les autres tournées, tout en conservant la demande de livraison et les marqueurs devant dans l'affichage
            this.getChildren().removeAll(this.tournees.get(numTournee-1), this.dl, this.marker);
            this.getChildren().addAll(this.tournees.get(numTournee-1), this.dl, this.marker);
        }
    }
    
    /**
     * 
     * @param nb 
     */
    public void creerCalques(int nb){
        this.tournees.clear();
        
        Iterator<Node> iter = this.getChildren().iterator();
        while(iter.hasNext()) {
            Node n = iter.next();
            if( !n.equals(dl) && !n.equals(plan)){
                iter.remove();
            }
        }
        
        for(int i=0;i<nb;i++){
            Canvas canvasTemp = new Canvas(this.getWidth(),this.getHeight());
            this.tournees.add(canvasTemp);
        }
        
        this.getChildren().addAll(this.tournees);
        this.getChildren().get(0).toBack();
        this.getChildren().get(1).toFront();
        this.getChildren().add(this.marker);
    }
    
    /**
     * Change la tournée affichée pour la tournée dont l'indice est passée en paramètre.
     * @param numTournee - indice de la tournée à afficher
     */
    public void changerTourneeAffichee(int numTournee){
        for(int i=0;i<this.tournees.size();i++){
            this.tournees.get(i).setVisible(true);
            if(numTournee!=0 && i!=numTournee-1)
                this.tournees.get(i).setVisible(false);
        }
    }
    
    public double[] mettreCoordonneesALechelle(double[] pointAMAJ, boolean estCoordonneesVueGraphique){
        double[] pointAJour = new double[2];
        if(estCoordonneesVueGraphique){
            pointAJour[0] = pointAMAJ[0] / echelleLong + origineLongitude;
            pointAJour[1] = (pointAMAJ[1] - this.getHeight()) / (-echelleLat) + origineLatitude;
        }
        else
        {
            pointAJour[0] = (pointAMAJ[0] - origineLongitude) * echelleLong;
            pointAJour[1] = this.getHeight() - (pointAMAJ[1] - origineLatitude) * echelleLat;
        }
        return pointAJour;
    }
    
    //Test
    public void effacerMarker() {
        this.positionMarker = null;
        this.marker.getGraphicsContext2D().clearRect(0,0,this.marker.getWidth(), this.marker.getHeight());
    }
    
    //Test
    public void ajouterMarker(double lat, double lon){
        int x = (int)((lon - origineLongitude)*echelleLong);
        int y = (int)(getHeight() - (lat - origineLatitude)*echelleLat);

        GraphicsContext gc = marker.getGraphicsContext2D();
        gc.drawImage(imageMarker, x - imageMarker.getWidth()/2.0, y - imageMarker.getHeight());
        
        this.positionMarker = new Pair(lat,lon);
        
        this.getChildren().remove(this.marker);
        this.getChildren().add(this.marker);
    }
    
    //Test
    public void dessinerMarker(){
        this.marker.getGraphicsContext2D().clearRect(0,0,this.marker.getWidth(), this.marker.getHeight());
        
        if(this.positionMarker != null)
            ajouterMarker(this.positionMarker.getKey(),this.positionMarker.getValue());
    }
    
    public void identifierPtPassage(boolean aAjouter, double lat, double lon){
        this.effacerMarker();
        
        if(aAjouter)
            this.ajouterMarker(lat,lon);
    }

    public void zoomPlus(double lat, double lon){
        origineLongitude+=(lon-origineLongitude)*0.2/1.2;
        origineLatitude+= (lat-origineLatitude)*0.2/1.2;
        echelleLong = echelleLong *1.2;
        echelleLat=echelleLat*1.2;   
    }
    
    public void zoomMoins(double lat, double lon){
        echelleLong = echelleLong /1.2;
        echelleLat=echelleLat/1.2;
        
        if(echelleLong<=echelleLongitudeMin && echelleLat<=echelleLatitudeMin){
            echelleLong = echelleLongitudeMin;
            echelleLat = echelleLatitudeMin;
            origineLatitude = origineLatitudeMin;
            origineLongitude = origineLongitudeMin;
        }else{
            origineLongitude-=(lon-origineLongitude)*0.2;
            origineLatitude-= (lat-origineLatitude)*0.2;
        }
    }
}
