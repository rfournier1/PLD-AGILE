/*
 * Projet Deliverif
 *
 * Hexanome n° 41
 *
 * Projet développé dans le cadre du cours "Conception Orientée Objet
 * et développement logiciel AGILE".
 */
package controleur;

import java.io.IOException;
import org.xml.sax.SAXException;

/** Etat dans lequel se trouve l'application une fois le fichier de livraisons 
 *  chargé.
 * @author Hex'Calibur
 */

public class EtatLivraisonsChargees extends EtatDefaut{

    /**
     * Constructeur EtatLivraisonsChargees
     */
    public EtatLivraisonsChargees() {
    }
    
    /** 
      *  @param gestionLivraison
      *  @param nbLivreurs
      * @param fenetre
      *  @see modele.GestionLivraison
      *  @see EtatCalculTournees
     */
    @Override
    public void calculerTournees(modele.outils.GestionLivraison gestionLivraison, int nbLivreurs, deliverif.Deliverif fenetre){
        Controleur.etatCourant = Controleur.ETAT_CALCUL_TOURNEES;
        try{
            gestionLivraison.calculerTournees(nbLivreurs);
            fenetre.activerBoutonArreterCalcul(false);
        } catch(Exception e){
            e.printStackTrace();
            Controleur.etatCourant = Controleur.ETAT_LIVRAISONS_CHARGEES;
        }
    }
    
    /**  Cette méthode délègue la chargement des livraisons au modèle
     *   Si le chargement s'est bien passé on passe dans 
     *   l'EtatLivraisonsChargees   
     *  @param gestionLivraison
     *  @param fichier
     * @param fenetre
     *  @see modele.GestionLivraison
     *  @see EtatLivraisonsChargees
     */
    @Override
    public void chargeLivraisons (modele.outils.GestionLivraison gestionLivraison, String fichier, deliverif.Deliverif fenetre) {
        try{
            gestionLivraison.chargerDemandeLivraison(fichier);
            fenetre.estDemandeLivraisonChargee("SUCCESS");
        } catch (SAXException e){
            fenetre.estDemandeLivraisonChargee(e.getMessage());
        } catch (IOException e) {
            fenetre.estDemandeLivraisonChargee(e.getMessage());
        } catch (Exception e) {
            fenetre.estDemandeLivraisonChargee(e.getMessage());
        }
    }
    
    /**  Cette méthode délègue la chargement du plan au modèle
      *  Si le chargement s'est bien passé on passe dans 
      *  l'EtatPlanCharge 
      *  @param gestionLivraison
      *  @param fichier
     * @param fenetre
      *  @see modele.GestionLivraison
      *  @see EtatPlanCharge
     */
    @Override
    public void chargePlan (modele.outils.GestionLivraison gestionLivraison, String fichier, deliverif.Deliverif fenetre){
        try{
            gestionLivraison.chargerPlan(fichier);
            Controleur.etatCourant = Controleur.ETAT_PLAN_CHARGE;
            fenetre.estPlanCharge("SUCCESS");
        } catch (SAXException e) {
            fenetre.estPlanCharge(e.getMessage());
            
        } catch (IOException e) {
            fenetre.estPlanCharge(e.getMessage());
            
        } catch (Exception e) {
            //fenetre.estPlanCharge(e.getMessage());
            e.printStackTrace();
        }
    }
}


