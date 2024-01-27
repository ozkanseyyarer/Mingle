package com.example.mingle.adapter;

import android.annotation.SuppressLint;
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
import com.example.mingle.activity.GezdigimYerlerActivity;
import com.example.mingle.activity.GezdigimYerlerDetayActivity;
import com.example.mingle.activity.GezdigimYerlerDuzenlemeActivity;
import com.example.mingle.activity.KitapDetayActivity;
import com.example.mingle.activity.KitapDuzenlemeActivity;
import com.example.mingle.activity.OkudugumKitaplarActivity;
import com.example.mingle.databinding.AdapterItemBinding;
import com.example.mingle.post.GezdigimYerlerPost;
import com.example.mingle.post.KitapPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GezdigimYerlerPostAdapter extends RecyclerView.Adapter<GezdigimYerlerPostAdapter.GezdigimYerlerPostHolder> {
    private ArrayList<GezdigimYerlerPost> gezdigimYerlerPostArrayList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public GezdigimYerlerPostAdapter(Context context,ArrayList<GezdigimYerlerPost> gezdigimYerlerPostArrayList) {
        this.gezdigimYerlerPostArrayList = gezdigimYerlerPostArrayList;
        this.context = context;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public GezdigimYerlerPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterItemBinding adapterItemBinding = AdapterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GezdigimYerlerPostAdapter.GezdigimYerlerPostHolder(adapterItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GezdigimYerlerPostHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.adapterItemBinding.adapterItemBaslik.setText(gezdigimYerlerPostArrayList.get(position).yerAdi);
        Picasso.get().load(gezdigimYerlerPostArrayList.get(position).downloadUrl).into(holder.adapterItemBinding.adapterItemListelenecekGorsel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tıklandığında yapılacak işlemleri buraya ekleyin
                // Örneğin, DetayActivity'ye gitmek için:

                Intent intent = new Intent(view.getContext(), GezdigimYerlerDetayActivity.class);
                intent.putExtra("downloadUrl", gezdigimYerlerPostArrayList.get(position).downloadUrl);
                intent.putExtra("yerAdi", gezdigimYerlerPostArrayList.get(position).yerAdi);
                intent.putExtra("yerYorumu", gezdigimYerlerPostArrayList.get(position).yerYorumu);
                intent.putExtra("tarihGun", gezdigimYerlerPostArrayList.get(position).tarihGun);
                intent.putExtra("tarihAy", gezdigimYerlerPostArrayList.get(position).tarihAy);
                intent.putExtra("tarihYil", gezdigimYerlerPostArrayList.get(position).tarihYil);
                intent.putExtra("zamanSaat", gezdigimYerlerPostArrayList.get(position).zamanSaat);
                intent.putExtra("zamanDakika", gezdigimYerlerPostArrayList.get(position).zamanDakika);
                intent.putExtra("latitude", gezdigimYerlerPostArrayList.get(position).latitude);
                intent.putExtra("longitude", gezdigimYerlerPostArrayList.get(position).longitude);
                intent.putExtra("kullaniciPuani", gezdigimYerlerPostArrayList.get(position).kullaniciPuani);
                intent.putExtra("yerId", gezdigimYerlerPostArrayList.get(position).yerId);


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
                            Intent intent = new Intent(view.getContext(), GezdigimYerlerDuzenlemeActivity.class);
                            intent.putExtra("downloadUrl", gezdigimYerlerPostArrayList.get(position).downloadUrl);
                            intent.putExtra("yerAdi", gezdigimYerlerPostArrayList.get(position).yerAdi);
                            intent.putExtra("yerYorumu", gezdigimYerlerPostArrayList.get(position).yerYorumu);
                            intent.putExtra("tarihGun", gezdigimYerlerPostArrayList.get(position).tarihGun);
                            intent.putExtra("tarihAy", gezdigimYerlerPostArrayList.get(position).tarihAy);
                            intent.putExtra("tarihYil", gezdigimYerlerPostArrayList.get(position).tarihYil);
                            intent.putExtra("zamanSaat", gezdigimYerlerPostArrayList.get(position).zamanSaat);
                            intent.putExtra("zamanDakika", gezdigimYerlerPostArrayList.get(position).zamanDakika);
                            intent.putExtra("latitude", gezdigimYerlerPostArrayList.get(position).latitude);
                            intent.putExtra("longitude", gezdigimYerlerPostArrayList.get(position).longitude);
                            intent.putExtra("kullaniciPuani", gezdigimYerlerPostArrayList.get(position).kullaniciPuani);
                            intent.putExtra("yerId", gezdigimYerlerPostArrayList.get(position).yerId);

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
                                            Intent intent = new Intent(view.getContext(), GezdigimYerlerActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            view.getContext().startActivity(intent);
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
        return gezdigimYerlerPostArrayList.size();
    }

    class GezdigimYerlerPostHolder extends RecyclerView.ViewHolder {

        AdapterItemBinding adapterItemBinding;

        public GezdigimYerlerPostHolder(AdapterItemBinding adapterItemBinding) {
            super(adapterItemBinding.getRoot());
            this.adapterItemBinding = adapterItemBinding;


        }
    }

    private void deleteItem(int position) {
        String itemId = gezdigimYerlerPostArrayList.get(position).yerId;

        // Firestore içerisindeki dökümana referans verildi
        DocumentReference documentReference = firebaseFirestore.collection("gezilen_yerler").document(itemId);

        // döküman firesoreden silindi
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Document successfully deleted, now update the local list
                gezdigimYerlerPostArrayList.remove(position);


                // Notify the adapter that the data set has changed
                notifyDataSetChanged();

                Toast.makeText(context, "Öğe silindi", Toast.LENGTH_SHORT).show();
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
