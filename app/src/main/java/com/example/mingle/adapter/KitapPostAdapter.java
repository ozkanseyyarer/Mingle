package com.example.mingle.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mingle.R;
import com.example.mingle.activity.AniDuzenlemeActivity;
import com.example.mingle.activity.AnilarActivity;
import com.example.mingle.activity.KitapDetayActivity;
import com.example.mingle.activity.KitapDuzenlemeActivity;
import com.example.mingle.activity.OkudugumKitaplarActivity;
import com.example.mingle.databinding.AdapterItemBinding;
import com.example.mingle.post.KitapPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class KitapPostAdapter extends RecyclerView.Adapter<KitapPostAdapter.KitapPostHolder> {

    private ArrayList<KitapPost> kitapPostArrayList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public KitapPostAdapter(Context context, ArrayList<KitapPost> kitapPostArrayList) {
        this.context = context;
        this.kitapPostArrayList = kitapPostArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public KitapPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterItemBinding adapterItemBinding = AdapterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new KitapPostHolder(adapterItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull KitapPostHolder holder, int position) {
        holder.adapterItemBinding.adapterItemBaslik.setText(kitapPostArrayList.get(position).kitapAdi);
        Picasso.get().load(kitapPostArrayList.get(position).downloadUrl).into(holder.adapterItemBinding.adapterItemListelenecekGorsel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tıklandığında yapılacak işlemleri buraya ekleyin
                // Örneğin, DetayActivity'ye gitmek için:

                Intent intent = new Intent(view.getContext(), KitapDetayActivity.class);
                intent.putExtra("downloadUrl", kitapPostArrayList.get(position).downloadUrl);
                intent.putExtra("kitapAdi", kitapPostArrayList.get(position).kitapAdi);
                intent.putExtra("kitapYazari", kitapPostArrayList.get(position).kitapYazari);
                intent.putExtra("kitapOzeti", kitapPostArrayList.get(position).kitapOzeti);
                intent.putExtra("kullaniciPuani", kitapPostArrayList.get(position).kullaniciPuani);
                intent.putExtra("kitapId", kitapPostArrayList.get(position).kitapId);


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
                            Intent intent = new Intent(view.getContext(), KitapDuzenlemeActivity.class);
                            intent.putExtra("downloadUrl", kitapPostArrayList.get(position).downloadUrl);
                            intent.putExtra("kitapAdi", kitapPostArrayList.get(position).kitapAdi);
                            intent.putExtra("kitapOzeti", kitapPostArrayList.get(position).kitapOzeti);
                            intent.putExtra("kitapYazari", kitapPostArrayList.get(position).kitapYazari);
                            intent.putExtra("kullaniciPuani", kitapPostArrayList.get(position).kullaniciPuani);
                            intent.putExtra("kitapId", kitapPostArrayList.get(position).kitapId);
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
                                            Intent intent = new Intent(view.getContext(), OkudugumKitaplarActivity.class);
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
        return kitapPostArrayList.size();
    }

    class KitapPostHolder extends RecyclerView.ViewHolder {

        AdapterItemBinding adapterItemBinding;

        public KitapPostHolder(AdapterItemBinding adapterItemBinding) {
            super(adapterItemBinding.getRoot());
            this.adapterItemBinding = adapterItemBinding;


        }
    }

    private void deleteItem(int position) {
        String itemId = kitapPostArrayList.get(position).kitapId;

        // Firestore içerisindeki dökümana referans verildi
        DocumentReference documentReference = firebaseFirestore.collection("okunan_kitaplar").document(itemId);

        // döküman firesoreden silindi
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Document successfully deleted, now update the local list
                kitapPostArrayList.remove(position);


                // Notify the adapter that the data set has changed
                notifyDataSetChanged();

                Toast.makeText(context, "Kitap başarıyla silindi", Toast.LENGTH_SHORT).show();
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
