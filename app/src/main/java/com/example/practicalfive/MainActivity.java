package com.example.practicalfive;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText municipalityInput;
    private final TextProcessor processor = new TextProcessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        UiInsets.apply(this, findViewById(R.id.mainRoot), true);
        municipalityInput = findViewById(R.id.municipalityInput);

        municipalityInput.setOnEditorActionListener((textView, actionId, event) -> {
            boolean keyboardAction = actionId == EditorInfo.IME_ACTION_GO
                    || actionId == EditorInfo.IME_ACTION_DONE;
            boolean enter = event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            if (keyboardAction || (enter && event.getAction() == KeyEvent.ACTION_DOWN)) {
                openMunicipality();
                return true;
            }
            return enter;
        });
    }

    private void openMunicipality() {
        String raw = municipalityInput.getText().toString();
        String name = processor.processMunicipality(raw);
        if (name == null) {
            municipalityInput.setError(getString(raw.trim().isEmpty()
                    ? R.string.error_empty : R.string.error_invalid));
            municipalityInput.requestFocus();
            return;
        }
        municipalityInput.setError(null);
        Intent intent = new Intent(this, Municipality.class);
        intent.putExtra(Municipality.EXTRA_MUNICIPALITY, name);
        startActivity(intent);
    }

}
