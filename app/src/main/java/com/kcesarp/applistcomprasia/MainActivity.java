package com.kcesarp.applistcomprasia;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ShoppingListAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private List<ShoppingItem> shoppingList;
    private EditText editTextItem;
    private AdministradorDB dbHelper;
    private static final String TAG = "MainActivity";
    private static final String OPENAI_API_KEY = "Tu_API_KEY_DE_OPENAI"; // Reemplaza con tu clave de API de OpenAI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(theme);

        setContentView(R.layout.activity_main);

        editTextItem = findViewById(R.id.edit_text_item);
        recyclerView = findViewById(R.id.recycler_view);
        shoppingList = new ArrayList<>();
        adapter = new ShoppingListAdapter(this, shoppingList);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        dbHelper = new AdministradorDB(this);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> addItem());

        ImageButton btnMenuOptions = findViewById(R.id.btn_menu_options);
        btnMenuOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_view_food) {
                    fetchFoodSuggestions();
                    return true;
                } else if (id == R.id.action_toggle_theme) {
                    toggleTheme();
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });

        loadItems();
    }

    private void loadItems() {
        shoppingList.clear();
        shoppingList.addAll(dbHelper.getAllItems());
        adapter.notifyDataSetChanged();
    }

    private void addItem() {
        String itemName = editTextItem.getText().toString().trim();
        if (!itemName.isEmpty()) {
            ShoppingItem newItem = new ShoppingItem(itemName);
            shoppingList.add(newItem);
            dbHelper.addItem(newItem);
            adapter.notifyItemInserted(shoppingList.size() - 1);
            editTextItem.setText("");
        } else {
            Toast.makeText(this, "El nombre del ítem no puede estar vacío", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchFoodSuggestions() {
        List<ShoppingItem> checkedItems = new ArrayList<>();
        for (ShoppingItem item : shoppingList) {
            if (item.isChecked()) {
                checkedItems.add(item);
            }
        }

        if (checkedItems.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un ingrediente", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder ingredients = new StringBuilder();
        for (int i = 0; i < checkedItems.size(); i++) {
            ingredients.append(checkedItems.get(i).getName());
            if (i < checkedItems.size() - 1) {
                ingredients.append(", ");
            }
        }

        String prompt = "Tengo los siguientes ingredientes: " + ingredients.toString() + ". " +
                "Sugiere 3 platos peruanos que puedo cocinar con estos ingredientes. " +
                "Si faltan ingredientes para alguno de los platos, por favor indícalos. " +
                "Devuelve la respuesta en formato JSON con los campos 'platos' y 'ingredientes_faltantes'.";

        OpenAIApi openAIApi = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIApi.class);

        OpenAIApi.ChatCompletionRequest request = new OpenAIApi.ChatCompletionRequest(
                "gpt-4o-mini",
                new ArrayList<OpenAIApi.Message>() {{
                    add(new OpenAIApi.Message("user", prompt));
                }}
        );

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Obteniendo sugerencias de comida...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        openAIApi.createChatCompletion("Bearer " + OPENAI_API_KEY, request).enqueue(new Callback<OpenAIApi.ChatCompletionResponse>() {
            @Override
            public void onResponse(Call<OpenAIApi.ChatCompletionResponse> call, Response<OpenAIApi.ChatCompletionResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    String apiResponse = response.body().getChoices().get(0).getMessage().getContent();
                    parseAndShowSuggestions(apiResponse);
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<OpenAIApi.ChatCompletionResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "Error en la solicitud", t);
                Toast.makeText(MainActivity.this, "Error en la solicitud: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiError(Response<OpenAIApi.ChatCompletionResponse> response) {
        String errorMessage = "Error en la respuesta de la API";
        try {
            if (response.errorBody() != null) {
                errorMessage = response.errorBody().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error al leer el cuerpo del error", e);
        }
        Log.e(TAG, "Código de Respuesta: " + response.code());
        Log.e(TAG, "Mensaje de Respuesta: " + response.message());
        Log.e(TAG, "Cuerpo de Respuesta: " + errorMessage);
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }


    private void parseAndShowSuggestions(String apiResponse) {
        try {
            JSONObject jsonObject = new JSONObject(apiResponse);
            JSONArray platosArray = jsonObject.getJSONArray("platos");
            JSONArray faltantesArray = jsonObject.getJSONArray("ingredientes_faltantes");

            List<String> platos = new ArrayList<>();
            List<String> faltantes = new ArrayList<>();

            for (int i = 0; i < platosArray.length(); i++) {
                platos.add(platosArray.getString(i));
            }

            for (int i = 0; i < faltantesArray.length(); i++) {
                faltantes.add(faltantesArray.getString(i));
            }

            showFoodSuggestions(platos, faltantes);

        } catch (JSONException e) {
            Log.e(TAG, "Error al parsear la respuesta de la API", e);
            Toast.makeText(this, "Error al procesar la respuesta de la API", Toast.LENGTH_SHORT).show();
        }
    }


    private void showFoodSuggestions(List<String> platos, List<String> faltantes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sugerencias de Comida Peruana");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_food_suggestions, (ViewGroup) findViewById(android.R.id.content), false);
        LinearLayout suggestionsLayout = viewInflated.findViewById(R.id.suggestions_layout);
        LinearLayout missingLayout = viewInflated.findViewById(R.id.missing_layout);

        suggestionsLayout.removeAllViews();
        missingLayout.removeAllViews();

        TextView titlePlatos = new TextView(this);
        titlePlatos.setText("Platos Sugeridos:");
        titlePlatos.setTextSize(18);
        titlePlatos.setPadding(16, 16, 16, 8);
        suggestionsLayout.addView(titlePlatos);

        for (String plato : platos) {
            TextView textView = new TextView(this);
            textView.setText("- " + plato);
            textView.setPadding(16, 8, 16, 8);
            textView.setTextSize(16);
            suggestionsLayout.addView(textView);
        }

        if (!faltantes.isEmpty()) {
            TextView titleFaltantes = new TextView(this);
            titleFaltantes.setText("Ingredientes Faltantes:");
            titleFaltantes.setTextSize(18);
            titleFaltantes.setPadding(16, 16, 16, 8);
            missingLayout.addView(titleFaltantes);

            for (String faltante : faltantes) {
                TextView textView = new TextView(this);
                textView.setText("- " + faltante);
                textView.setPadding(16, 8, 16, 8);
                textView.setTextSize(16);
                missingLayout.addView(textView);
            }
        }

        builder.setView(viewInflated);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void toggleTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putInt("theme", AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putInt("theme", AppCompatDelegate.MODE_NIGHT_YES);
        }

        editor.apply();
        recreate();
    }

    @Override
    public void onItemClick(int position) {
        ShoppingItem item = shoppingList.get(position);
        item.setChecked(!item.isChecked());
        dbHelper.updateItem(item);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onEditClick(int position) {
        ShoppingItem item = shoppingList.get(position);
        showEditItemDialog(item, position);
    }

    @Override
    public void onDeleteClick(int position) {
        ShoppingItem item = shoppingList.remove(position);
        dbHelper.deleteItem(item.getName());
        adapter.notifyItemRemoved(position);
    }

    private void showEditItemDialog(ShoppingItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Ítem");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_edit_item, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = viewInflated.findViewById(R.id.edit_text_item_name);
        input.setText(item.getName());
        builder.setView(viewInflated);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                item.setName(newName);
                dbHelper.updateItem(item);
                adapter.notifyItemChanged(position);
            } else {
                Toast.makeText(this, "El nombre del ítem no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}