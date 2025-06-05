package com.example.mediatekformationmobile.controleur;

import android.content.Context;
import android.util.Log;

import com.example.mediatekformationmobile.modele.AccesDistant;
import com.example.mediatekformationmobile.modele.Favoris;
import com.example.mediatekformationmobile.modele.Formation;
import com.example.mediatekformationmobile.vue.FormationsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Controle {
    private static final String TAG = "Controle";
    private static Controle instance = null;
    private ArrayList<Formation> lesFormations = new ArrayList<>();
    private Formation formation = null;
    private static AccesDistant accesDistant;
    private FormationsActivity formationsActivity;

    private Favoris favorisDAO;
    private Context context;

    /**
     * constructeur privé
     */
    
    private Controle() {
        super();
    }

    /**
     * récupération de l'instance unique de Controle
     * @return instance
     */
    public static final Controle getInstance() {
        if (Controle.instance == null) {
            Controle.instance = new Controle();
            accesDistant = AccesDistant.getInstance();
            accesDistant.envoi("tous", "formation", null);
        }

        return Controle.instance;
    }

    /**
     * Définir le contexte de l'application pour accéder à la BDD
     * @param context Contexte de l'application
     */
    public void setContext(Context context) {
        this.context = context;
        if (this.context != null) {
            this.favorisDAO = Favoris.getInstance(context);
            // Synchroniser l'état des favoris après avoir défini le contexte
            synchroniserEtatFavoris();
        }
    }

    public Formation getFormation() {
        return formation;
    }

    /**
     * Définir l'activité des formations pour pouvoir la notifier des changements
     * @param activity l'activité FormationsActivity
     */
    public void setFormationsActivity(FormationsActivity activity) {
        this.formationsActivity = activity;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public ArrayList<Formation> getLesFormations() {
        // Synchroniser l'état des favoris avant de retourner la liste
        synchroniserEtatFavoris();
        return lesFormations;
    }

    /**
     * Définir la liste des formations et notifier l'UI
     * @param lesFormations liste des formations
     */
    /**
     * Définir la liste des formations et notifier l'UI
     * @param lesFormations liste des formations
     */
    public void setLesFormations(ArrayList<Formation> lesFormations) {
        // IMPORTANT: Imprimer l'état des favoris AVANT de modifier les formations
        if (favorisDAO != null) {
            ArrayList<Integer> favorisAvant = favorisDAO.getTousLesFavorisIds();
            Log.d(TAG, "AVANT SET: Nombre de favoris en BDD = " + favorisAvant.size());
            Log.d(TAG, "AVANT SET: IDs des favoris = " + favorisAvant.toString());
        }

        // Stocker les anciennes formations pour avoir accès à l'état précédent des favoris si nécessaire
        ArrayList<Formation> anciensFormations = this.lesFormations;

        // Mettre à jour la liste des formations
        this.lesFormations = lesFormations;

        // CRITIQUE: Restaurer l'état des favoris depuis la base de données
        if (favorisDAO != null) {
            // Récupérer la liste actuelle des favoris depuis la BDD
            ArrayList<Integer> favorisIds = favorisDAO.getTousLesFavorisIds();
            Log.d(TAG, "SET: Nombre de favoris trouvés en BDD = " + favorisIds.size());

            // Appliquer l'état des favoris aux nouvelles formations
            for (Formation formation : this.lesFormations) {
                boolean estFavori = favorisIds.contains(formation.getId());
                formation.setFavorite(estFavori);
                Log.d(TAG, "Formation ID " + formation.getId() + " - favori = " + estFavori);
            }

            
        } else {
            Log.d(TAG, "SET: La BDD des favoris n'est pas initialisée!");
        }

        // Notifier l'activité que les données ont changé
        notifierChangementFormations();

        // IMPORTANT: Imprimer l'état des favoris APRÈS avoir modifié les formations
        if (favorisDAO != null) {
            ArrayList<Integer> favorisApres = favorisDAO.getTousLesFavorisIds();

            // Vérifier combien de formations sont marquées comme favorites
            int compteurFavoris = 0;
            for (Formation f : this.lesFormations) {
                if (f.isFavorite()) {
                    compteurFavoris++;
                }
            }
        }
    }

    /**
     * Synchronise l'état des favoris entre la BDD et les objets Formation
     */
    private void synchroniserEtatFavoris() {
        if (favorisDAO != null && lesFormations != null) {

            // Récupérer tous les IDs des favoris en une seule requête pour optimiser
            ArrayList<Integer> favorisIds = favorisDAO.getTousLesFavorisIds();

            // Parcourir toutes les formations et mettre à jour leur état favori
            for (Formation formation : lesFormations) {
                boolean estFavori = favorisIds.contains(formation.getId());
                if (formation.isFavorite() != estFavori) {
                    Log.d(TAG, "Mise à jour du statut favori - Formation ID: " + formation.getId() +
                            ", ancien statut: " + formation.isFavorite() + ", nouveau statut: " + estFavori);
                    formation.setFavorite(estFavori);
                }
            }
        } 
    }

    private void notifierChangementFormations() {
        if (formationsActivity != null) {
            formationsActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    formationsActivity.creerListe();
                    // Afficher un message de confirmation si nécessaire
                }
            });
        } 
    }

    public void filtrerFormations(String motCle) {
        try {
            // IMPORTANT: Imprimons le nombre de favoris AVANT le filtrage
            if (favorisDAO != null) {
                ArrayList<Integer> favorisAvantFiltrage = favorisDAO.getTousLesFavorisIds();
                Log.d(TAG, "AVANT FILTRAGE: Nombre de favoris en BDD = " + favorisAvantFiltrage.size());
                Log.d(TAG, "AVANT FILTRAGE: IDs des favoris = " + favorisAvantFiltrage.toString());
            }

            JSONObject params = new JSONObject();
            params.put("motcle", motCle);
            Log.d(TAG, "Filtrage avec le mot clé: " + motCle);

            // IMPORTANT: Ne pas nettoyer les favoris lors du filtrage
            // On garde une référence aux anciennes formations pour récupérer l'état des favoris après
            final ArrayList<Formation> anciensFormations = new ArrayList<>(lesFormations);

            // Appeler l'API pour le filtrage
            accesDistant.envoi("motcle", "formations", params);
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la création des paramètres JSON: " + e.getMessage());
        }
    }

    /**
     * Récupère la liste des formations favorites
     * @return Liste des formations marquées comme favorites
     */
    public ArrayList<Formation> getFavoriteFormations() {
        // Synchroniser à nouveau pour s'assurer que les données sont à jour
        synchroniserEtatFavoris();

        ArrayList<Formation> favorites = new ArrayList<>();

        // Récupère toutes les formations et filtre celles qui sont favorites
        if (lesFormations != null) {
            for (Formation formation : lesFormations) {
                if (formation.isFavorite()) {
                    favorites.add(formation);
                    Log.d(TAG, "Formation favorite ajoutée - ID: " + formation.getId() + ", Titre: " + formation.getTitle());
                }
            }
            Log.d(TAG, "Nombre total de formations favorites trouvées: " + favorites.size());
        } else {
            Log.d(TAG, "La liste des formations est null, impossible de récupérer les favoris");
        }

        return favorites;
    }

    /**
     * Bascule l'état favori d'une formation et met à jour la BDD
     * @param formation Formation à modifier
     * @return nouvel état de favori
     */
    public boolean toggleFavori(Formation formation) {
        if (favorisDAO != null) {
            boolean nouveauStatut = formation.toggleFavorite();
            Log.d(TAG, "Toggle favori - Formation ID: " + formation.getId() +
                    ", ancien statut: " + !nouveauStatut + ", nouveau statut: " + nouveauStatut);

            if (nouveauStatut) {
                // Ajouter aux favoris dans la BDD
                boolean succes = favorisDAO.ajouterFavori(formation.getId());
                Log.d(TAG, "Ajout du favori ID: " + formation.getId() + " dans la BDD - succès: " + succes);
            } else {
                // Supprimer des favoris dans la BDD
                boolean succes = favorisDAO.supprimerFavori(formation.getId());
                Log.d(TAG, "Suppression du favori ID: " + formation.getId() + " de la BDD - succès: " + succes);
            }

            return nouveauStatut;
        }
        Log.d(TAG, "Impossible de modifier le favori: DAO non initialisé");
        return false;
    }

    /**
     * Vérifie si une formation est dans les favoris
     * @param formation Formation à vérifier
     * @return true si la formation est en favori, false sinon
     */
    public boolean isFavori(Formation formation) {
        if (formation == null) {
            return false;
        }
        return formation.isFavorite();
    }

    /**
     * Ajoute une formation aux favoris
     * @param formation Formation à ajouter aux favoris
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterFavori(Formation formation) {
        if (formation == null || favorisDAO == null) {
            return false;
        }

        // Ajouter à la BDD et mettre à jour l'objet
        boolean succes = favorisDAO.ajouterFavori(formation.getId());
        if (succes) {
            formation.setFavorite(true);
        }
        return succes;
    }

    /**
     * Préserve l'état des favoris après une opération de filtrage ou de rechargement
     * @param nouvellesFormations Les nouvelles formations à mettre à jour
     */
    public void preserverFavoris(ArrayList<Formation> nouvellesFormations) {
        if (favorisDAO == null || nouvellesFormations == null) {
            return;
        }

        // Récupérer tous les IDs des favoris en BDD
        ArrayList<Integer> favorisIds = favorisDAO.getTousLesFavorisIds();

        // Appliquer l'état favori aux nouvelles formations
        for (Formation formation : nouvellesFormations) {
            formation.setFavorite(favorisIds.contains(formation.getId()));
        }

        Log.d(TAG, "État des favoris préservé pour " + nouvellesFormations.size() + " formations");
    }
}