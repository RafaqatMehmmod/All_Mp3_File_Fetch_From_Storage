package com.example.allfilefetch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    public static ArrayList<File> pdfFile;
    A a;


    public MyAdapter(Context context, ArrayList<File> pdfFile ,A a) {
        this.context = context;
        this.pdfFile = pdfFile;
        this.a=a;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.mp3_item_show,parent,false)) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textName.setText(pdfFile.get(position).getName());
        holder.textName.setSelected(true);


    }

    @Override
    public int getItemCount() {
        return pdfFile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public CardView cardView;
        public ImageView imageView,imageView2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName=itemView.findViewById(R.id.pdf_textName);
            cardView=itemView.findViewById(R.id.pdf_cardView);
            imageView=itemView.findViewById(R.id.pdf_imageView);
            imageView2=itemView.findViewById(R.id.img_menu);


//            cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    a.click(textName,);
//                }
//            });

            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    a.click(textName,view,getAdapterPosition());
                }
            });
        }
    }
}
