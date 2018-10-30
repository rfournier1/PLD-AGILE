/*
 * Projet Deliverif
 *
 * Hexanome n° 41
 *
 * Projet développé dans le cadre du cours "Conception Orientée Objet
 * et développement logiciel AGILE".
 */
package modele.outils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Un chemin représente une liste de tronçons, il est caractérisé par son début et sa fin.
 * @version 1.0 23/10/2018
 * @author Louis Ohl
 */
public class Chemin {
    private List<Troncon> troncons;
    private PointPassage debut;
    private PointPassage fin;

    /**
     * Créer un nouveau chemin
     * @param troncons - La liste de tronçons composant ce chemin
     * @param debut - Le point de passage par lequel commence ce chemin
     * @param fin - Le point de passage par lequel se termine ce chemin
     * @see modele.outils.PointPassage
     */
    public Chemin(List<Troncon> troncons, PointPassage debut, PointPassage fin) {
        this.troncons = troncons;
        this.debut=debut;
        this.fin=fin;
    }

    /**
     * Créer un nouveau chemin
     * @param debut - Le point de passage par lequel commence ce chemin
     * @param fin - Le point de passage par lequel se termine ce chemin
     * @see modele.outils.PointPassagen
     */
    public Chemin(PointPassage debut, PointPassage fin){
        this.debut=debut;
        this.fin=fin;
        this.troncons = new ArrayList<Troncon>();
    }

    /**
     * ATTENTION : supprime la fin actuelle du chemin
     * @param troncon - Le troncon à ajouter à la fin du chemin
     */
    protected void addTroncon(Troncon troncon){
        if (troncon==null){
            return;
        } else if (troncons.size()!=0 && this.troncons.get(this.troncons.size()-1).getFin()!=troncon.getDebut()){
            return;
        } else if (troncons.size()==0 && this.debut.getPosition()!=troncon.getDebut()){
            return;
        }
        this.fin=null;
        this.troncons.add(troncon);
    }

    /**
     *
     * @return - Le point de passage par lequel débute le chemin
     */
    protected PointPassage getDebut(){
        return this.debut;
    }

    /**
     * Affecte le param fin à l'attribut fin du chemin
     * @param fin
     */
    protected void setFin(PointPassage fin){
        if (troncons.size()!=0){
            if (this.troncons.get(this.troncons.size()-1).getFin()==fin.getPosition()){
                this.fin=fin;
            }
        }
    }

    /**
     *
     * @return - Le point de passage par lequel se termine le chemin
     */
    protected PointPassage getFin(){
        return this.fin;
    }

    /**
     *
     * @return - La longueur du chemin, i.e la somme des longueurs des tronçons composant le chemin
     */
    protected float getLongueur(){
        float longueur=0.0f;
        for(Troncon t : this.troncons){
            longueur+=t.getLongueur();
        }
        return longueur;
    }

    /**
     *
     * @return - La durée du chemin, i.e la somme des durées des tronçons et de la livraison
     */
    protected float getDuree(){
        float longueur = this.getLongueur();
        float resultat = longueur/3.6f;
        if (this.fin!=null){
            resultat+=this.fin.getDuree();
        }
        return resultat;
    }

    /**
     *
     * @return - La description tectuelle du chemin
     */
    protected List<String> getDescription(Calendar heureDepart){
        List<String> etapes = new ArrayList<String>();
        etapes.add("("+heureDepart.get(Calendar.HOUR_OF_DAY)+"h"+
                heureDepart.get(Calendar.MINUTE)+") Depart"+
                (this.debut.estEntrepot()? " de l'entrepot." : " du point de livraison"));
        String dernierNom="";
        float longueur=0f;
        for(Troncon c : this.troncons){
            longueur+=c.getLongueur();
            if(!c.getNom().equals(dernierNom)){
                etapes.add("Traverser : "+dernierNom+" pendant "+longueur+" m.");
                etapes.add("Tourner à : "+c.getNom());
                dernierNom=c.getNom();
            }
        }
        etapes.add("Traverser : "+dernierNom+" pendant "+longueur+" m.");
        heureDepart.add(Calendar.SECOND, (int)this.getDuree());
        etapes.add("Arriver "+(this.fin.estEntrepot()? "à l'entrepot." :" au point de livraison")+"("+
                heureDepart.get(Calendar.HOUR_OF_DAY)+"h"+heureDepart.get(Calendar.MINUTE)+
                ").");

        return etapes;
    }
}
