package com.example.onlinebookcrossing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.annotation.NonNullApi;


import java.util.ArrayList;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{
    Context context;
    ArrayList<Book> arrayList;
    OnItemClickListener onItemClickListener;

    public BookAdapter(Context context,  ArrayList<Book> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    // Создание нового элемента списка (ViewHolder)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.books_list_item, parent, false);
        return new ViewHolder(view);
    }
    // Привязка данных к элементу списка
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getTitle());
        holder.subtitle.setText(arrayList.get(position).getAuthor());
        holder.place.setText(arrayList.get(position).getPlace());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onClick(arrayList.get(position)));
    }
// Возвращает общее количество элементов в списке
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // Класс ViewHolder для отображения каждого элемента списка
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, place;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_title);
            subtitle = itemView.findViewById(R.id.list_item_subtitle);
            place = itemView.findViewById(R.id.list_item_place);
        }
    }

    // Установка обработчика нажатия на элемент списка
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Интерфейс для обработки события нажатия на элемент списка
    public interface OnItemClickListener{
        void onClick(Book book);
    }
}
