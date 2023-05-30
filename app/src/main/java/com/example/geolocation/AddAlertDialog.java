package com.example.geolocation;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class AddAlertDialog extends AppCompatDialogFragment {
    private EditText nameField;
    private EditText descriptionField;
    private Spinner categoryField;
    private String base64Image;
    private DialogListener listener;
    private String coordinates;

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

        builder.setView(view)
                .setTitle("Adicionando alerta")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name  = nameField.getText().toString();
                        String description = descriptionField.getText().toString();
                        int index = categoryField.getSelectedItemPosition();
                        String value = categoryField.getSelectedItem().toString();
                        String b64 = ""; // TODO: extract image
                        listener.applyTexts(name, description, index, b64, coordinates);
                    }
                });

        nameField = view.findViewById(R.id.name);
        descriptionField = view.findViewById(R.id.description);
        categoryField = view.findViewById(R.id.category);

        return builder.create();
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

    public interface DialogListener {
        void applyTexts(String name, String description, int categoryId, String imageBase64, String coordinates);
    }
}