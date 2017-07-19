package com.example.anubhav.taskmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anubhav on 14-07-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ToDoViewHolder>{
    private Context mContext;
    private ArrayList<ToDoItem> arrayList, arrayListCopy;
    private ToDoClickListener mListener;
//    private ToDoLongClickListener mLongListener;


    public RecyclerAdapter(Context mContext, ArrayList<ToDoItem> arrayList, ToDoClickListener mListener){//}, ToDoLongClickListener mLongListener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mListener = mListener;
//        this.mLongListener = mLongListener;
        arrayListCopy=arrayList;
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.todoitem_layout, parent, false);
        return new ToDoViewHolder(itemView, mListener);//, mLongListener);

    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ToDoViewHolder holder, int position) {
        ToDoItem toDoItem = arrayList.get(position);
        holder.titleTextView.setText(toDoItem.getTitle());
        holder.dateTextView.setText(toDoItem.getDate());
        holder.timeTextView.setText(toDoItem.getTime());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void filter(String newText,boolean isChecked) {
        newText = newText.toLowerCase();
        arrayList = new ArrayList<>();
        if (newText.length() == 0) {
            arrayList=arrayListCopy;
        } else {
            for (int i = 0; i < arrayListCopy.size(); i++) {
                ToDoItem toDoItem = arrayListCopy.get(i);
                if(isChecked==false) {
                    if (toDoItem.title.toLowerCase().contains(newText)) {
                        arrayList.add(toDoItem);
                    }
                }
                else if(isChecked){
                    if(toDoItem.category.toLowerCase().contains(newText)){
                        arrayList.add(toDoItem);
                    }
                }
                notifyDataSetChanged();
            }
        }

    }

    public void dataupdate(){
        notifyDataSetChanged();
    }

//    public interface ToDoLongClickListener {
//        void onItemLongClick(View view, int position);
//    }

    public interface ToDoClickListener {
        void onItemClick(View view, int position);
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;
        ToDoClickListener mClickListener;
//        ToDoLongClickListener mLongClickListener;

        public ToDoViewHolder(View itemView, ToDoClickListener listener){//}, ToDoLongClickListener longClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    int id = v.getId();
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        if (id == R.id.todoitem_layout) {
//                            mLongClickListener.onItemLongClick(v, position);
//                        }
//                    }
//                    return true;
//                }
//            });
            mClickListener = listener;
//            mLongClickListener = longClickListener;
            titleTextView = (TextView) itemView.findViewById(R.id.title_item);
            dateTextView = (TextView) itemView.findViewById(R.id.date_item);
            timeTextView = (TextView) itemView.findViewById(R.id.time_item);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (id == R.id.todoitem_layout) {
                    mClickListener.onItemClick(view, position);
                }
            }

        }


    }
}
