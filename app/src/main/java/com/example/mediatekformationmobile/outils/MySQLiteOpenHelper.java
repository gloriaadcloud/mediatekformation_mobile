package com.example.mediatekformationmobile.outils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe de gestion de la base de données SQLite
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "MySQLiteOpenHelper";

    // Nom de la base de données
    private static final String DATABASE_NAME = "favoris.db";

    // Version de la base de données
    private static final int DATABASE_VERSION = 1;

    // Nom de la table des favoris
    public static final String FAVORIS_TABLE_NAME = "favoris";

    // Colonnes de la table favoris
    public static final String COLUMN_ID = "id";

    // Requête de création de la table
    private static final String CREATE_TABLE_FAVORIS = "CREATE TABLE " + FAVORIS_TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY);";

    // Instance unique de l'helper
    private static MySQLiteOpenHelper instance;

    /**
     * Constructeur privé pour empêcher l'instanciation directe
     * @param context Contexte de l'application
     */
    private MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "MySQLiteOpenHelper initialisé avec le contexte: " + context);
    }

    /**
     * Récupération de l'instance unique de MySQLiteOpenHelper
     * @param context Contexte de l'application
     * @return instance MySQLiteOpenHelper
     */
    public static synchronized MySQLiteOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MySQLiteOpenHelper(context.getApplicationContext());
            Log.d(TAG, "Nouvelle instance de MySQLiteOpenHelper créée");
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Création de la table: " + CREATE_TABLE_FAVORIS);
        db.execSQL(CREATE_TABLE_FAVORIS);
        Log.d(TAG, "Base de données créée");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Mise à jour de la base de données de la version " + oldVersion + " à " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + FAVORIS_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Méthode de débogage pour vérifier que la base de données est correctement configurée
     * @return true si la base de données existe et contient la table favoris
     */
    public boolean verifierBaseDeDonnees() {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            if (db != null && db.isOpen()) {
                Log.d(TAG, "Base de données ouverte avec succès, vérifiant les tables...");
                String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + FAVORIS_TABLE_NAME + "'";
                boolean tableExists = db.rawQuery(query, null).getCount() > 0;
                Log.d(TAG, "Table " + FAVORIS_TABLE_NAME + " existe: " + tableExists);
                return tableExists;
            }
            Log.e(TAG, "Impossible d'ouvrir la base de données");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la vérification de la base de données: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}