package com.example.mingle.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mingle.R;
import com.example.mingle.activity.FilmDetayActivity;
import com.example.mingle.activity.FilmDuzenleActivity;
import com.example.mingle.activity.FilmlerActivity;
import com.example.mingle.activity.KitapDetayActivity;
import com.example.mingle.activity.KitapDuzenlemeActivity;
import com.example.mingle.activity.OkudugumKitaplarActivity;
import com.example.mingle.databinding.AdapterItemBinding;
import com.example.mingle.post.FilmPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilmPostAdapter extends RecyclerView.Adapter<FilmPostAdapter.FilmPostHolder> {

    private ArrayList<FilmPost> filmPostArrayList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public FilmPostAdapter(Context context,ArrayList<FilmPost> filmPostArrayList) {
        this.filmPostArrayList = filmPostArrayList;
        this.context = context;
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public FilmPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterItemBinding adapterItemBinding = AdapterItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FilmPostHolder(adapterItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmPostHolder holder, int position) {
        holder.adapterItemBinding.adapterItemBaslik.setText(filmPostArrayList.get(position).filmAdi);
        Picasso.get().load(filmPostArrayList.get(position).downloadUrl).into(holder.adapterItemBinding.adapterItemListelenecekGorsel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tıklandığında yapılacak işlemleri buraya ekleyin
                // Örneğin, DetayActivity'ye gitmek için:

                Intent intent = new Intent(view.getContext(), FilmDetayActivity.class);
                intent.putExtra("downloadUrl", filmPostArrayList.get(position).downloadUrl);
                intent.putExtra("filmAdi", filmPostArrayList.get(position).filmAdi);
                intent.putExtra("filmYonetmeni", filmPostArrayList.get(position).filmYonetmeni);
                intent.putExtra("filmOzeti", filmPostArrayList.get(position).filmOzeti);
                intent.putExtra("kullaniciPuani", filmPostArrayList.get(position).kullaniciPuani);
                intent.putExtra("filmId", filmPostArrayList.get(position).filmId);

                view.getContext().startActivity(intent);
            }
        });
        holder.itemView.findViewById(R.id.adapter_item_popUp_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.setGravity(Gravity.END);

                    popupMenu.getMenu().add("Düzenle").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {
                            Intent intent = new Intent(view.getContext(), FilmDuzenleActivity.class);
                            intent.putExtra("downloadUrl", filmPostArrayList.get(position).downloadUrl);
                            intent.putExtra("filmAdi", filmPostArrayList.get(position).filmAdi);
                            intent.putExtra("filmYonetmeni", filmPostArrayList.get(position).filmYonetmeni);
                            intent.putExtra("filmOzeti", filmPostArrayList.get(position).filmOzeti);
                            intent.putExtra("kullaniciPuani", filmPostArrayList.get(position).kullaniciPuani);
                            intent.putExtra("filmId", filmPostArrayList.get(position).filmId);

                            view.getContext().startActivity(intent);
                            return false;
                        }
                    });


                    popupMenu.getMenu().add("Sil").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Ask the user for confirmation before deleting
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Silme İşlemi")
                                    .setMessage("Bu öğeyi silmek istediğinizden emin misiniz?")
                                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Delete the item and update the list
                                            deleteItem(position);
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("Hayır", null)
                                    .show();


                            return true;
                        }
                    });


                    popupMenu.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return filmPostArrayList.size();
    }

    class FilmPostHolder extends RecyclerView.ViewHolder {

        AdapterItemBinding adapterItemBinding;
        public FilmPostHolder(AdapterItemBinding adapterItemBinding) {
            super(adapterItemBinding.getRoot());
            this.adapterItemBinding = adapterItemBinding;
        }
    }

    private void deleteItem(int position) {
        String itemId = filmPostArrayList.get(position).filmId;

        // Firestore içerisindeki dökümana referans verildi
        DocumentReference documentReference = firebaseFirestore.collection("filmler").document(itemId);

        // döküman firestoreden silindi
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Document successfully deleted, now update the local list
                filmPostArrayList.remove(position);


                // Notify the adapter that the data set has changed
                notifyDataSetChanged();

                Toast.makeText(context, "Film başarıyla silindi", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // An error occurred while deleting the document
                Toast.makeText(context, "Silme işlemi başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
