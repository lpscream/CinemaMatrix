package net.matrixhome.kino.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import net.matrixhome.kino.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.ViewHolder> {
    private ArrayList<String> stringArrayList;
    LayoutInflater inflater;
    private int pos;
    private ItemClickListener mItemClickListener;

    public DescriptionAdapter(Context context, ArrayList<String> list) {
        this.stringArrayList = list;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.number.setText(stringArrayList.get(position));
        pos = position;
    }

    @Override
    public int getItemCount() {
        if (stringArrayList != null)
            return stringArrayList.size();
        else return 0;
    }

    public void setNewList(ArrayList<String> newList){
        stringArrayList.clear();
        stringArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    public void click(){

    }

    public void setOnmClickListener(ItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItenClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            //createRoundedCorners(number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) mItemClickListener.onItenClick(view, getLayoutPosition());
        }
    }

    private void createRoundedCorners(View view){
        ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, android.R.attr.radius)
                .build();
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
        shapeDrawable.setFillColor(ContextCompat.getColorStateList(inflater.getContext(),R.color.btn_background));
        shapeDrawable.setStroke(2.0f, ContextCompat.getColor(inflater.getContext(), R.color.btn_background));
        shapeDrawable.setElevation(10f);
        ViewCompat.setBackground(view, shapeDrawable);


    }

}






