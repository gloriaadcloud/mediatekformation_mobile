package com.example.mediatekformationmobile.modele;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mediatekformationmobile.outils.MySQLiteOpenHelper;

import java.util.ArrayList;

public class Favoris {
    private static final String TAG = "Favoris";
    private MySQLiteOpenHelper dbHelper;
    private static Favoris instance = null;

    /**
     * Constructeur privé
     * @param context Contexte de l'application
     */
    private Favoris(Context context) {
        dbHelper = MySQLiteOpenHelper.getInstance(context);
        Log.d(TAG, "Instance Favoris créée avec le contexte: " + context);
    }

    /**
     * Récupération de l'instance unique de Favoris
     * @param context Contexte de l'application
     * @return instance Favoris
     */
    public static synchronized Favoris getInstance(Context context) {
        if (instance == null) {
            instance = new Favoris(context);
        }
        return instance;
    }

    /**
     * Ajoute une formation aux favoris
     * @param formationId ID de la formation à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterFavori(int formationId) {
        Log.d(TAG, "Tentative d'ajout du favori: " + formationId);
        SQLiteDatabase db = null;

        try {
            // Ouvrir la base de données
            db = dbHelper.getWritableDatabase();

            // Vérifier si ce favori existe déjà dans la même connexion
            boolean existe = false;
            Cursor cursor = db.query(
                    MySQLiteOpenHelper.FAVORIS_TABLE_NAME,
                    new String[]{MySQLiteOpenHelper.COLUMN_ID},
                    MySQLiteOpenHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(formationId)},
                    null, null, null
            );

            existe = cursor.getCount() > 0;
            cursor.close();

            // Si le favori existe déjà, ne pas l'ajouter
            if (existe) {
                Log.d(TAG, "Le favori ID: " + formationId + " existe déjà, pas d'ajout");
                return true; // Déjà favori, considéré comme un succès
            }

            // Ajouter le favori
            ContentValues values = new ContentValues();
            values.put(MySQLiteOpenHelper.COLUMN_ID, formationId);
            long result = db.insert(MySQLiteOpenHelper.FAVORIS_TABLE_NAME, null, values);
            Log.d(TAG, "Résultat de l'insertion: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout du favori: " + e.getMessage(), e);
            return false;
        } finally {
            // Fermer la base de données si elle est ouverte
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Supprime une formation des favoris
     * @param formationId ID de la formation à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerFavori(int formationId) {
        Log.d(TAG, "Tentative de suppression du favori: " + formationId);
        SQLiteDatabase db = null;

        try {
            // Ouvrir la base de données
            db = dbHelper.getWritableDatabase();

            // Supprimer le favori
            int result = db.delete(
                    MySQLiteOpenHelper.FAVORIS_TABLE_NAME,
                    MySQLiteOpenHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(formationId)}
            );

            Log.d(TAG, "Nombre de lignes supprimées: " + result);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la suppression du favori: " + e.getMessage(), e);
            return false;
        } finally {
            // Fermer la base de données si elle est ouverte
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Vérifie si une formation est dans les favoris
     * @param formationId ID de la formation à vérifier
     * @return true si la formation est dans les favoris, false sinon
     */
    public boolean estFavori(int formationId) {
        Log.d(TAG, "Vérification si ID " + formationId + " est favori");
        SQLiteDatabase db = null;

        try {
            // Ouvrir la base de données
            db = dbHelper.getReadableDatabase();

            // Vérifier si le favori existe
            Cursor cursor = db.query(
                    MySQLiteOpenHelper.FAVORIS_TABLE_NAME,
                    new String[]{MySQLiteOpenHelper.COLUMN_ID},
                    MySQLiteOpenHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(formationId)},
                    null, null, null
            );

            boolean estFavori = cursor.getCount() > 0;
            cursor.close();

            Log.d(TAG, "Formation ID " + formationId + " est" + (estFavori ? "" : " non") + " favori");
            return estFavori;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la vérification du favori: " + e.getMessage(), e);
            return false;
        } finally {
            // Fermer la base de données si elle est ouverte
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Récupère tous les IDs des formations favorites
     * @return ArrayList contenant les IDs des formations favorites
     */
    public ArrayList<Integer> getTousLesFavorisIds() {
        Log.d(TAG, "Récupération de tous les IDs de favoris");
        ArrayList<Integer> favorisIds = new ArrayList<>();
        SQLiteDatabase db = null;

        try {
            // Ouvrir la base de données
            db = dbHelper.getReadableDatabase();

            // Récupérer tous les favoris
            Cursor cursor = db.query(
                    MySQLiteOpenHelper.FAVORIS_TABLE_NAME,
                    new String[]{MySQLiteOpenHelper.COLUMN_ID},
                    null, null, null, null, null
            );

            Log.d(TAG, "Nombre de favoris trouvés: " + cursor.getCount());

            // Parcourir les résultats
            if (cursor.moveToFirst()) {
                do {
                    int formationId = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteOpenHelper.COLUMN_ID));
                    favorisIds.add(formationId);
                    Log.d(TAG, "ID favori trouvé: " + formationId);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération des favoris: " + e.getMessage(), e);
        } finally {
            // Fermer la base de données si elle est ouverte
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return favorisIds;
    }

    /**
     * Nettoie les favoris qui ne sont plus dans la liste des formations
     * @param formationIdsDisponibles Liste des IDs de formations disponibles
     */
    public void nettoyerFavorisObsoletes(ArrayList<Integer> formationIdsDisponibles) {
        Log.d(TAG, "Nettoyage des favoris obsolètes");

        // Récupérer tous les favoris
        ArrayList<Integer> favorisIds = getTousLesFavorisIds();
        Log.d(TAG, "Nombre de favoris à vérifier: " + favorisIds.size());

        // Parcourir les favoris et supprimer ceux qui ne sont plus disponibles
        for (Integer favorisId : favorisIds) {
            if (!formationIdsDisponibles.contains(favorisId)) {
                supprimerFavori(favorisId);
                Log.d(TAG, "Favori obsolète supprimé: " + favorisId);
            }
        }
    }
}