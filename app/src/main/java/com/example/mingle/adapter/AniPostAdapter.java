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
import com.example.mingle.activity.AniDetayActivity;
import com.example.mingle.activity.AniDuzenlemeActivity;
import com.example.mingle.activity.AnilarActivity;
import com.example.mingle.activity.KitapDetayActivity;
import com.example.mingle.activity.KitapDuzenlemeActivity;
import com.example.mingle.databinding.AdapterItemBinding;
import com.example.mingle.post.AniPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AniPostAdapter extends RecyclerView.Adapter<AniPostAdapter.AniPostHolder> {
    private ArrayList<AniPost> aniPostArrayList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public AniPostAdapter(Context context,ArrayList<AniPost> aniPostArrayList) {
        this.context = context;
        this.aniPostArrayList = aniPostArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AniPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterItemBinding adapterItemBinding = AdapterItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new AniPostHolder(adapterItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AniPostHolder holder, int position) {
        holder.adapterItemBinding.adapterItemBaslik.setText(aniPostArrayList.get(position).aniBaslik);
        Picasso.get().load(aniPostArrayList.get(position).downloadUrl).into(holder.adapterItemBinding.adapterItemListelenecekGorsel);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tıklandığında yapılacak işlemleri buraya ekleyin
                // Örneğin, DetayActivity'ye gitmek için:

                Intent intent = new Intent(view.getContext(), AniDetayActivity.class);
                intent.putExtra("downloadUrl", aniPostArrayList.get(position).downloadUrl);
                intent.putExtra("aniBaslik", aniPostArrayList.get(position).aniBaslik);
                intent.putExtra("aniOzeti", aniPostArrayList.get(position).aniOzeti);
                intent.putExtra("aniId", aniPostArrayList.get(position).aniId);
                intent.putExtra("kullaniciPuani", aniPostArrayList.get(position).kullaniciPuani);

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
                           Intent intent = new Intent(view.getContext(), AniDuzenlemeActivity.class);
                           intent.putExtra("downloadUrl", aniPostArrayList.get(position).downloadUrl);
                           intent.putExtra("aniBaslik", aniPostArrayList.get(position).aniBaslik);
                           intent.putExtra("aniOzeti", aniPostArrayList.get(position).aniOzeti);
                           intent.putExtra("kullaniciPuani", aniPostArrayList.get(position).kullaniciPuani);
                           intent.putExtra("id",aniPostArrayList.get(position).aniId );
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
                                           Intent intent = new Intent(view.getContext(), AnilarActivity.class);
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

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
               popupMenu.setGravity(Gravity.END);

               popupMenu.getMenu().add("Düzenle").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(@NonNull MenuItem item) {
                       Intent intent = new Intent(view.getContext(), AniDuzenlemeActivity.class);
                       intent.putExtra("downloadUrl", aniPostArrayList.get(position).downloadUrl);
                       intent.putExtra("aniBaslik", aniPostArrayList.get(position).aniBaslik);
                       intent.putExtra("aniOzeti", aniPostArrayList.get(position).aniOzeti);
                       intent.putExtra("kullaniciPuani", aniPostArrayList.get(position).kullaniciPuani);
                       intent.putExtra("id",aniPostArrayList.get(position).aniId );
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
                                       Intent intent = new Intent(view.getContext(), AnilarActivity.class);
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
               return false;
           }
       });
    }

    @Override
    public int getItemCount() {
        return aniPostArrayList.size();
    }

    class AniPostHolder extends RecyclerView.ViewHolder{

        AdapterItemBinding adapterItemBinding;
        public AniPostHolder(AdapterItemBinding adapterItemBinding) {
            super(adapterItemBinding.getRoot());
            this.adapterItemBinding = adapterItemBinding;



        }
    }

    private void deleteItem(int position) {
        String itemId = aniPostArrayList.get(position).aniId;

        // Reference to the document in Firestore
        DocumentReference documentReference = firebaseFirestore.collection("anilar").document(itemId);

        // Delete the document from Firestore
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Document successfully deleted, now update the local list
                aniPostArrayList.remove(position);


                // Notify the adapter that the data set has changed
                notifyDataSetChanged();

                Toast.makeText(context, "Anı başarıyla silindi", Toast.LENGTH_SHORT).show();
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
