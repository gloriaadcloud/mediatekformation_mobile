package com.example.mediatekformationmobile.vue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediatekformationmobile.R;
import com.example.mediatekformationmobile.controleur.Controle;
import com.example.mediatekformationmobile.modele.Favoris;
import com.example.mediatekformationmobile.modele.Formation;

import java.util.ArrayList;

/**
 * Classe de gestion de la liste 'adapter'
 */
public class FormationListAdapter extends RecyclerView.Adapter<FormationListAdapter.ViewHolder> {
    private static final String TAG = "FormationListAdapter";
    private ArrayList<Formation> lesFormations;
    private Context context;
    private Controle controle;
    private Favoris favorisDAO;
    

    /**
     * Constructeur : valorise les propriétés privées
     * @param lesFormations
     * @param context
     */
    public FormationListAdapter(ArrayList<Formation> lesFormations, Context context) {
        this.lesFormations = lesFormations;
        this.context = context;
        this.controle = Controle.getInstance();
        this.controle.setContext(context);
        this.favorisDAO = Favoris.getInstance(context);

        Log.d(TAG, "Adapter initialisé avec " + lesFormations.size() + " formations");
    }

    /**
     * Création d'une ligne d'affichage dans la liste "adapter"
     */
    @NonNull
    @Override
    public FormationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater layout = LayoutInflater.from(parentContext);
        View view = layout.inflate(R.layout.layout_liste_formations, parent, false);
        return new FormationListAdapter.ViewHolder(view);
    }

    /**
     * Validation du contenu des objets graphiques
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Formation formation = lesFormations.get(position);
        holder.txtListeTitle.setText(formation.getTitle());
        holder.txtListPublishedAt.setText(formation.getPublishedAtToString());

        // Vérifier si cette formation est favorite
        boolean estFavori = favorisDAO.estFavori(formation.getId());

        // Synchroniser l'état de l'objet Formation avec la BDD
        formation.setFavorite(estFavori);

        // Mettre à jour l'icône en fonction de l'état favori
        if (estFavori) {
            holder.btnListFavori.setImageResource(R.drawable.coeur_rouge);
        } else {
            holder.btnListFavori.setImageResource(R.drawable.coeur_gris);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return lesFormations.size();
    }

    /**
     * Ouvre la page du détail de la formation
     * @param position
     */
    private void ouvrirUneFormationActivity(int position){
        controle.setFormation(lesFormations.get(position));
        Intent intent = new Intent(context, UneFormationActivity.class);
        context.startActivity(intent);
    }

    /**
     * Propriétés de la ligne
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageButton btnListFavori;
        public final TextView txtListPublishedAt;
        public final TextView txtListeTitle;

        /**
         * Constructeur : crée un lien avec les objets graphiques de la ligne
         * et gère les événements sur ces objets graphiques
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtListeTitle = (TextView)itemView.findViewById(R.id.txtListTitle);
            txtListPublishedAt = (TextView)itemView.findViewById(R.id.txtListPublishedAt);
            btnListFavori = (ImageButton)itemView.findViewById(R.id.btnListFavori);

            // Gestion du clic sur le titre ou la date pour ouvrir le détail
            txtListeTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ouvrirUneFormationActivity(getAdapterPosition());
                }
            });
            txtListPublishedAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ouvrirUneFormationActivity(getAdapterPosition());
                }
            });

            // Gestion du clic sur le bouton favori
            btnListFavori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Formation formation = lesFormations.get(position);
                        int formationId = formation.getId();

                        // Vérifier l'état actuel
                        boolean estFavori = favorisDAO.estFavori(formationId);
                        Log.d(TAG, "Clic sur favori - Formation ID: " + formationId +
                                ", Est actuellement favori: " + estFavori);

                        // Inverser l'état
                        boolean succes;
                        if (estFavori) {
                            // Supprimer des favoris
                            succes = favorisDAO.supprimerFavori(formationId);
                            formation.setFavorite(false);
                            btnListFavori.setImageResource(R.drawable.coeur_gris);
                            Toast.makeText(context, "Retiré des favoris", Toast.LENGTH_SHORT).show();

                            // Si nous sommes dans FavorisActivity, supprimer l'élément de la liste
                            if (context instanceof FavorisActivity) {
                                lesFormations.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, lesFormations.size());
                            }
                        } else {
                            // Ajouter aux favoris
                            succes = favorisDAO.ajouterFavori(formationId);
                            formation.setFavorite(true);
                            btnListFavori.setImageResource(R.drawable.coeur_rouge);
                            Toast.makeText(context, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
                        }

                        Log.d(TAG, "Modification du favori - Succès: " + succes);
                    }
                }
            });
        }
    }
}