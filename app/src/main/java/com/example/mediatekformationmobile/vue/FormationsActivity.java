package com.example.mediatekformationmobile.vue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mediatekformationmobile.R;
import com.example.mediatekformationmobile.controleur.Controle;
import com.example.mediatekformationmobile.modele.AccesDistant;
import com.example.mediatekformationmobile.modele.Formation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FormationsActivity extends AppCompatActivity {

    private Controle controle;
    private Button btnFiltrer;
    private EditText txtFiltre;
    // Map pour sauvegarder l'état des favoris avant filtrage
    private Map<Integer, Boolean> favorisEtat = new HashMap<>();

    private static final String TAG = "FormationsActivity";

    private RecyclerView lstFormations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formations);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste en cas de changement d'état des favoris
        creerListe();
    }

    /**
     * initialisations
     */
    private void init(){
        controle = Controle.getInstance();
        controle.setContext(this);
        controle.setFormationsActivity(this);
        btnFiltrer = findViewById(R.id.btnFiltrer);
        txtFiltre = findViewById(R.id.txtFiltre);
        lstFormations = findViewById(R.id.lstFormations);

        lstFormations.setLayoutManager(new LinearLayoutManager(this));

        // Écouter le clic sur le bouton Filtrer
        btnFiltrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filtre = txtFiltre.getText().toString().trim();
                Log.d(TAG, "Texte du filtre: " + filtre);

                // Sauvegarder l'état des favoris avant filtrage
                sauvegarderEtatFavoris();

                if (filtre.isEmpty()) {
                    // Si vide, charger toutes les formations
                    AccesDistant.getInstance().envoi("tous", "formations", null);
                } else {
                    // Filtrer les formations
                    controle.filtrerFormations(filtre);
                }

                // Informer l'utilisateur
                Toast.makeText(FormationsActivity.this,
                        "Recherche en cours...", Toast.LENGTH_SHORT).show();
            }
        });

        // Charger les formations au démarrage
        creerListe();
    }

    /**
     * Sauvegarde l'état des favoris avant filtrage
     */
    private void sauvegarderEtatFavoris() {
        favorisEtat.clear();
        ArrayList<Formation> lesFormations = controle.getLesFormations();
        if (lesFormations != null) {
            for (Formation formation : lesFormations) {
                // Sauvegarder l'état du favori pour chaque formation
                favorisEtat.put(formation.getId(), controle.isFavori(formation));
            }
            Log.d(TAG, "État des favoris sauvegardé: " + favorisEtat.size() + " formations");
        }
    }

    /**
     * Restaure l'état des favoris après filtrage
     */
    private void restaurerEtatFavoris() {
        ArrayList<Formation> lesFormations = controle.getLesFormations();
        if (lesFormations != null && !favorisEtat.isEmpty()) {
            for (Formation formation : lesFormations) {
                int id = formation.getId();
                if (favorisEtat.containsKey(id) && favorisEtat.get(id)) {
                    // Si la formation était en favori, s'assurer qu'elle l'est toujours
                    if (!controle.isFavori(formation)) {
                        controle.ajouterFavori(formation);
                        Log.d(TAG, "Favori restauré pour formation ID: " + id);
                    }
                }
            }
        }
    }

    /**
     * création de la liste adapter
     */
    public void creerListe() {
        // Restaurer l'état des favoris si nécessaire
        restaurerEtatFavoris();

        ArrayList<Formation> lesFormations = controle.getLesFormations();
        if (lesFormations != null) {
            Collections.sort(lesFormations, Collections.<Formation>reverseOrder());
            FormationListAdapter adapter = new FormationListAdapter(lesFormations, this);
            lstFormations.setAdapter(adapter);
        } else {
            // Charger les formations depuis l'API
            AccesDistant.getInstance().envoi("tous", "formations", null);
        }
    }
}