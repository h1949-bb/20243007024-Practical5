package com.example.practicalfive;

import android.os.Bundle;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.content.res.ColorStateList;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Municipality extends AppCompatActivity {
    public static final String EXTRA_MUNICIPALITY = "municipality_name";
    private String selectedWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_municipality);
        UiInsets.apply(this, findViewById(R.id.municipalityRoot), false);

        TextProcessor processor = new TextProcessor();
        String name = processor.processMunicipality(
                getIntent().getStringExtra(EXTRA_MUNICIPALITY));

        if (name == null) {
            Toast.makeText(this, R.string.error_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int index;
        int imageResource;
        int colourResource;
        int websiteResource;

        switch (name) {
            case "Beijing":
                index = 0;
                imageResource = R.drawable.beijing;
                colourResource = R.color.municipality_beijing;
                websiteResource = R.string.website_beijing;
                break;
            case "Shanghai":
                index = 1;
                imageResource = R.drawable.shanghai;
                colourResource = R.color.municipality_shanghai;
                websiteResource = R.string.website_shanghai;
                break;
            case "Chongqing":
                index = 2;
                imageResource = R.drawable.chongqing;
                colourResource = R.color.municipality_chongqing;
                websiteResource = R.string.website_chongqing;
                break;
            case "Tianjin":
                index = 3;
                imageResource = R.drawable.tianjin;
                colourResource = R.color.municipality_tianjin;
                websiteResource = R.string.website_tianjin;
                break;
            default:
                finish();
                return;
        }

        TextView title = findViewById(R.id.municipalityName);
        ImageView photo = findViewById(R.id.municipalityImage);
        TextView info = findViewById(R.id.municipalityInfo);
        Button visit = findViewById(R.id.visitSiteButton);

        String[] summaries = getResources().getStringArray(R.array.municipalities_array);
        int colour = ContextCompat.getColor(this, colourResource);
        boolean orange = name.equals("Chongqing");
        int contrast = ContextCompat.getColor(this,
                orange ? R.color.color_text_dark : R.color.color_text_light);

        title.setText(name);
        title.setBackgroundColor(colour);
        title.setTextColor(contrast);
        photo.setImageResource(imageResource);
        photo.setContentDescription(getString(R.string.municipality_image_description, name));
        info.setText(summaries[index]);
        info.setTextColor(colour);
        info.setBackgroundColor(ContextCompat.getColor(this,
                orange ? R.color.color_text_dark : R.color.color_default_background));
        visit.setBackgroundTintList(ColorStateList.valueOf(colour));
        visit.setTextColor(contrast);
        selectedWebsite = getString(websiteResource);

        visit.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedWebsite));
            try {
                startActivity(browserIntent);
            } catch (ActivityNotFoundException exception) {
                Toast.makeText(this, R.string.error_browser, Toast.LENGTH_LONG).show();
            }
        });
    }

}
