package com.geolocatepoop;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddAlertDialog extends AppCompatDialogFragment {
    private EditText nameField;
    private EditText descriptionField;
    private Spinner categoryField;
    private ImageButton btn_foto;
    private int cameraRequest = 1888;
    private String base64Image;
    private DialogListener listener;
    private String coordinates;
    private ImageButton saveButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_alert_modal, null);

        /* OBTER ARGUMENTOS FORNECIDOS */

        // LINKS UTEIS PARA ABRIR O ALERT
        // https://www.youtube.com/watch?v=ARezg1D9Zd0
        // https://gist.github.com/codinginflow/11e5acb69a91db8f2be0f8e495505d12
        assert getArguments() != null;
        coordinates = getArguments().getString("coordinates");

        builder.setView(view);

        nameField = view.findViewById(R.id.name);
        descriptionField = view.findViewById(R.id.description);
        categoryField = view.findViewById(R.id.category);
        saveButton = view.findViewById(R.id.btn_save);

        AlertDialog alert = builder.create();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewTeste) {
                String name  = nameField.getText().toString();
                String description = descriptionField.getText().toString();
                Category category = (Category) categoryField.getSelectedItem();
                String b64 = ""; // TODO: extract image
                listener.applyTexts(name, description, category.getId(), b64, coordinates);
                alert.cancel();
            }
        });


        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {android.Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this.getActivity(), permissions, cameraRequest);
        }

        btn_foto = view.findViewById(R.id.btn_foto);
        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), cameraRequest);
            }
        });

        loadCategories();

        return alert;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    private void loadCategories() {
        ArrayList<Category> categoriesList = new ArrayList<Category>();

        new VolleyRequest(this.getContext()).executeGet("/categories", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arr = new JSONArray(response);

                    for (int i=0; i < arr.length(); i++) {
                        JSONObject respObj = arr.getJSONObject(i);

                        int id = respObj.getInt("id");
                        String name = respObj.getString("name");

                        Category category = new Category(id, name);

                        categoriesList.add(category);
                    }

                    ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(
                            AddAlertDialog.this.getContext(),
                            android.R.layout.simple_list_item_1,
                            categoriesList
                    );

                    categoryField.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface DialogListener {
        void applyTexts(String name, String description, int categoryId, String imageBase64, String coordinates);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cameraRequest) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            base64Image = Base64.encodeToString(b, Base64.DEFAULT);

        }
    }
}
