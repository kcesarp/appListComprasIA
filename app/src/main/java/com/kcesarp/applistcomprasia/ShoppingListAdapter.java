package com.kcesarp.applistcomprasia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private Context context;
    private List<ShoppingItem> shoppingList;
    private OnItemClickListener onItemClickListener;

    public ShoppingListAdapter(Context context, List<ShoppingItem> shoppingList) {
        this.context = context;
        this.shoppingList = shoppingList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopping, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem item = shoppingList.get(position);
        holder.checkBoxItem.setText(item.getName());
        holder.checkBoxItem.setChecked(item.isChecked());

        holder.checkBoxItem.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnOptions);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_item_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.action_edit) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onEditClick(position);
                    }
                    return true;
                } else if (id == R.id.action_delete) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onDeleteClick(position);
                    }
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });
    }


    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBoxItem;
        ImageButton btnOptions;

        ViewHolder(View itemView) {
            super(itemView);
            checkBoxItem = itemView.findViewById(R.id.checkbox_item);
            btnOptions = itemView.findViewById(R.id.btn_options);
        }
    }
}
