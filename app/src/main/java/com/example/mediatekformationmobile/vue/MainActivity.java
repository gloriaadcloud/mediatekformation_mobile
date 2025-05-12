package com.example.mediatekformationmobile.vue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mediatekformationmobile.R;
import com.example.mediatekformationmobile.controleur.Controle;
import com.example.mediatekformationmobile.modele.Favoris;
import com.example.mediatekformationmobile.outils.MySQLiteOpenHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * Initialisations
     */
    private void init() {
        Controle controle = Controle.getInstance();
        controle.setContext(this);

        // Initialiser la base de données et vérifier son état
        MySQLiteOpenHelper dbHelper = MySQLiteOpenHelper.getInstance(this);
        boolean dbOk = dbHelper.verifierBaseDeDonnees();

        // Vérifier si la base de données contient des favoris
        Favoris favorisDAO = Favoris.getInstance(this);
        int nbFavoris = favorisDAO.getTousLesFavorisIds().size();

        // Afficher un toast pour informer l'utilisateur

        creerMenu();
    }

    /**
     * Appelle les procédures événementielles pour gérer le menu
     */
    private void creerMenu() {
        ecouteMenu((ImageButton)findViewById(R.id.btnFormations), FormationsActivity.class);
        ecouteMenu((ImageButton)findViewById(R.id.btnFavoris), FavorisActivity.class);
    }

    /**
     * Procédure événementielle sur le clic d'une image du menu
     * @param btn
     * @param classe
     */
    private void ecouteMenu(ImageButton btn, final Class classe) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = MainActivity.this;
                Intent intent = new Intent(activity, classe);
                activity.startActivity(intent);
                Log.d(TAG, "Navigation vers: " + classe.getSimpleName());
            }
        });
    }
}