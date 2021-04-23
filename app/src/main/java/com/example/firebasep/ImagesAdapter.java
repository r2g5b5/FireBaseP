package com.example.firebasep;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasep.models.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    Context context;
    List<Upload> uploads;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    ImagesAdapter(Context context, List<Upload> uploads){
        this.context=context;
        this.uploads=uploads;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view=  LayoutInflater.from(context).inflate(R.layout.row_images,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtName.setText(uploads.get(position).getName());
        Picasso.get().load(uploads.get(position).getImgUrl()).
                placeholder(R.mipmap.ic_launcher).into(holder.imgMain);

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtName;
        ImageView imgMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.row_images_txtName);
            imgMain=itemView.findViewById(R.id.row_images_imgMain);



                    itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle("Select an Action: ");
                    MenuItem menu_deletePic=menu.add(Menu.NONE,1,1,"Delete this Picture");
                    MenuItem menu_EditPic=menu.add(Menu.NONE,2,2,"Edit Picture Name");

                    menu_deletePic.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (getAdapterPosition()!=RecyclerView.NO_POSITION){
                                onItemClickListener.onDeleteClicked(getAdapterPosition());
                            }
                            return true;
                        }
                    });

                    menu_EditPic.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (getAdapterPosition()!=RecyclerView.NO_POSITION){
                                onItemClickListener.onEditClicked(getAdapterPosition());
                            }

                            return true;
                        }
                    });

                }
            });


        }
    }


   public interface OnItemClickListener{
        void onDeleteClicked(int position);
       void onEditClicked(int position);

   }

}
