package edu.illinois.cs.cs125.therecipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class MainActivity extends Activity {
    private static final String TAG = "USETHISTheRecipe:Main";
    private static RequestQueue requestQueue;

    Button newButton, saveButton, openButton;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newButton = (Button) findViewById(R.id.newButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        openButton = (Button) findViewById(R.id.openButton);
        text = (EditText) findViewById(R.id.text);

        requestQueue = Volley.newRequestQueue(this);

        final Button randomButton = findViewById(R.id.randomButton);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                text.setText(getName("https://www.themealdb.com/api/json/v1/1/random.php"));
            }
        });
    }
    public static String getName(final String json) {
        JsonParser parser = new JsonParser();
        JsonObject allrecipe = parser.parse(json).getAsJsonObject();
        JsonObject meals = allrecipe.get("strMeal").getAsJsonObject();
        return meals.get("strMeal").getAsString();
    }
        void startAPIcall () {
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        "https://www.themealdb.com/api/json/v1/1/random.php",
                        (String) null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                if (response != null) {
                                    final TextView text = findViewById(R.id.text);
                                    if (text == null) {
                                        Log.d(TAG, "Null pointer.");
                                    } else {
                                        text.setText(response.toString());
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.w(TAG, error.toString());
                    }
                });
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    public class JsonReader {

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }

        public void main(String[] args) throws IOException, JSONException {
            JSONObject json = readJsonFromUrl("https://www.themealdb.com/api/json/v1/1/random.php");
            //System.out.println(json.toString());
            //System.out.println(json.get("id"));

        }
    }

    public void buttonAction(View v) {
        final EditText fileName = new EditText(this);
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setView(fileName);

        if (v.getId() == R.id.saveButton) {
            ad.setMessage("Save File");

            ad.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        FileOutputStream fout = openFileOutput(fileName.getText().toString() + ".txt", Context.MODE_PRIVATE);
                        fout.write(text.getText().toString().getBytes());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error Occured: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });

            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            ad.show();

        }

        if (v.getId() == R.id.openButton) {
            ad.setMessage("Open File");

            ad.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int c;
                    text.setText("");

                    try {
                        FileInputStream fin = openFileInput(fileName.getText().toString() + ".txt");

                        while ((c = fin.read()) != -1) {
                            text.setText((text.getText().toString() + Character.toString((char) c)));
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error Occured: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });

            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            ad.show();
        }
        if (v.getId() == R.id.newButton) {
            text.setText("");
        }
    }
}