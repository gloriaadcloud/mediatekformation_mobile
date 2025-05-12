package com.example.mediatekformationmobile.vue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediatekformationmobile.R;
import com.example.mediatekformationmobile.controleur.Controle;
import com.example.mediatekformationmobile.modele.Favoris;
import com.example.mediatekformationmobile.modele.Formation;

import java.util.ArrayList;
import java.util.Collections;

public class FavorisActivity extends AppCompatActivity {
    private static final String TAG = "FavorisActivity";
    private Controle controle;
    private RecyclerView lstFormations;
    private Favoris favorisDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formations);

        // Initialiser le contrôleur et le DAO
        controle = Controle.getInstance();
        controle.setContext(this);
        favorisDAO = Favoris.getInstance(this);

        // Masquer les éléments de filtrage
        Button btnFiltrer = findViewById(R.id.btnFiltrer);
        EditText txtFiltre = findViewById(R.id.txtFiltre);
        if (btnFiltrer != null) btnFiltrer.setVisibility(View.GONE);
        if (txtFiltre != null) txtFiltre.setVisibility(View.GONE);

        // Initialiser la RecyclerView
        lstFormations = findViewById(R.id.lstFormations);
        if (lstFormations != null) {
            lstFormations.setLayoutManager(new LinearLayoutManager(this));

            // Charger les favoris immédiatement
            chargerFavoris();
        } else {
            Log.e(TAG, "lstFormations est null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les favoris à chaque fois que l'activité reprend le focus
        chargerFavoris();
    }

    /**
     * Chargement des formations favorites
     */
    private void chargerFavoris() {
        // Récupérer les IDs des favoris depuis la base de données
        ArrayList<Integer> favorisIds = favorisDAO.getTousLesFavorisIds();
        Log.d(TAG, "Nombre de favoris dans la BDD: " + favorisIds.size());

        if (favorisIds.isEmpty()) {
            Toast.makeText(this, "Aucun favori trouvé", Toast.LENGTH_SHORT).show();

            // Afficher une liste vide
            FormationListAdapter adapter = new FormationListAdapter(new ArrayList<Formation>(), this);
            lstFormations.setAdapter(adapter);
            return;
        }

        // Récupérer toutes les formations disponibles
        ArrayList<Formation> toutesLesFormations = controle.getLesFormations();
        if (toutesLesFormations == null || toutesLesFormations.isEmpty()) {
            Log.d(TAG, "Aucune formation disponible");
            Toast.makeText(this, "Aucune formation disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filtrer pour ne garder que les favoris
        ArrayList<Formation> formationsFavorites = new ArrayList<>();
        for (Formation formation : toutesLesFormations) {
            if (favorisIds.contains(formation.getId())) {
                formation.setFavorite(true); // S'assurer que l'état est correct
                formationsFavorites.add(formation);
                Log.d(TAG, "Formation favorite trouvée: ID=" + formation.getId() +
                        ", Titre=" + formation.getTitle());
            }
        }

        Log.d(TAG, "Nombre de formations favorites trouvées: " + formationsFavorites.size());
        Toast.makeText(this, formationsFavorites.size() + " favoris trouvés", Toast.LENGTH_SHORT).show();

        if (!formationsFavorites.isEmpty()) {
            // Trier les formations par date (plus récent en premier)
            Collections.sort(formationsFavorites, Collections.<Formation>reverseOrder());

            // Créer et appliquer l'adaptateur
            FormationListAdapter adapter = new FormationListAdapter(formationsFavorites, this);
            lstFormations.setAdapter(adapter);
        } else {
            // Si aucun favori trouvé, afficher une liste vide
            FormationListAdapter adapter = new FormationListAdapter(new ArrayList<Formation>(), this);
            lstFormations.setAdapter(adapter);
        }
    }
}