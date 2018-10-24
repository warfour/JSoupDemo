package com.example.adamsaunders.jsoupdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Document currentPage;
    private Button idButton;
    private Button wikipediaButton;
    private Button elementButton;
    private TextView htmlView;
    private EditText tagText;
    private String content = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idButton = findViewById(R.id.idButton);
        wikipediaButton = findViewById(R.id.wikipediaButton);
        elementButton = findViewById(R.id.elementButton);
        htmlView = findViewById(R.id.htmlView);
        tagText = findViewById(R.id.tagText);


        idButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WikipediaTask wikipediaTask = new WikipediaTask();
                wikipediaTask.execute();
            }
        });

        wikipediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullTask fulltask = new FullTask();
                fulltask.execute();
            }
        });

        elementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage != null)
                {
                    String target = tagText.getText().toString();
                    List<String> output = new ArrayList<String>();

                    // Grabs all elements from inside the current pages body
                    Elements elements = currentPage.body().select("*");
                    for (Element element : elements) {
                        String currentTag = element.id();
                        if (currentTag.equalsIgnoreCase(target)) {
                            output.add(element.text());
                        }
                    }

                    String formattedText = "";
                    for (String text : output)
                    {
                        formattedText += text + "\n";
                    }

                    htmlView.setText(formattedText);
                }
            }
        });
    }

    class FullTask extends  AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                currentPage = Jsoup.connect("http://en.wikipedia.org/").get();
                Elements elements = currentPage.body().select("*");

                content = "";
                for (Element element : elements) {
                    content += element.toString() + "\n";
                }

            }
            catch (Exception e)
            {
                content = e.getMessage();
            }
            return null;
        }

        //has UI thread access
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            htmlView.setText(content);
        }
    }

    class WikipediaTask extends AsyncTask<Void, Void, Void> {

        //The "main" method of the thread
        //NO UI thread access
        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                Elements elements = currentPage.body().select("*");

                // finds unique IDS
                List<String> seenbefore = new ArrayList<String>();
                content = "";
                for (Element element : elements) {
                    if (!seenbefore.contains(element.id())) {
                        content += element.id() + "\n";
                        seenbefore.add(element.id());
                    }
                }

            }
            catch (Exception e)
            {
                content = e.getMessage();
            }
            return null;
        }

        //has UI thread access
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            htmlView.setText(content);
        }
    }
}
