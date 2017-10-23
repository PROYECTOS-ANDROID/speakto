package com.grupo01.speakto;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements  MediaPlayer.OnCompletionListener{

    private final static String TAG = "ANDROID";
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private boolean permissionToInternetAccepet = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET"};

    private MediaRecorder recorder;
    private MediaPlayer player;
    private File archivo;

    private final int REQ_OUT = 143;
    private Button button;
    private TextToSpeech text_to_voz;

    // aqui esta el texto que se va enviar para texto a audio
    private String texto_dialogo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pedirPermisos();

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarCaptura();
            }
        });
    }

    /**
     * Metodo que invoca el inicio de captura de la voz
     */
    private void iniciarCaptura(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hable por favor");
        try{
            startActivityForResult(intent, REQ_OUT);
        }catch (ActivityNotFoundException e){
            Log.i("ANDROID", e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data){
        super.onActivityResult(requestCode, resultcode, data);

        Log.i("ANDROID", "REQ CODE : " + requestCode);

        switch (requestCode){
            case REQ_OUT:{
                if(resultcode == RESULT_OK && data != null){
                    ArrayList<String> voiceinText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(voiceinText.size()>0){
                        AsyntackServicio servicio = new AsyntackServicio(voiceinText.get(0), 0);
                        servicio.execute();
                        Toast.makeText(this, voiceinText.get(0), Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(this, "No se encontraron textos", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(this, "Error no data null", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    private void pedirPermisos(){
         int requestCode = 200;
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int intPermisoRecordAudio  = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO);
            int intPermisoWrite_External  = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int intPermisoInternet  = checkSelfPermission(android.Manifest.permission.INTERNET);

            if((intPermisoRecordAudio != PackageManager.PERMISSION_GRANTED) &&
                    (intPermisoWrite_External != PackageManager.PERMISSION_GRANTED) &&
                    (intPermisoInternet != PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(this.getApplicationContext(), "No tiene permisos", Toast.LENGTH_SHORT).show();
                requestPermissions(permissions, requestCode);
            }else{
                Toast.makeText(this.getApplicationContext(), "Ya tiene permisos!!", Toast.LENGTH_SHORT).show();
            }
         }else{
            Toast.makeText(this.getApplicationContext(), "Este no es un movil M", Toast.LENGTH_SHORT).show();
         }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 200:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                permissionToInternetAccepet  = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted )
            MainActivity.super.finish();
        if (!permissionToWriteAccepted )
            MainActivity.super.finish();
        if (!permissionToInternetAccepet)
            MainActivity.super.finish();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Toast.makeText(this.getApplicationContext(), "...Reproduciendo...", Toast.LENGTH_SHORT).show();
    }

    private void iniciarDialogo(String data){
        texto_dialogo = data;
        text_to_voz = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    text_to_voz.setLanguage(Locale.ENGLISH); // Locale.ENGLISH
                }
                if(status == TextToSpeech.SUCCESS){
                    text_to_voz.speak(texto_dialogo, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }
    private class AsyntackServicio extends AsyncTask<String, Integer, Integer> {


        private CloudApi s;

        private String cadena;
        private String resultado;
        private int language;
        public AsyntackServicio(String cadena, int language){
            s  = new CloudApi();
            this.cadena = cadena;
            this.resultado = "";
            this.language = language;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            Log.i("ANDROID", "entrando a background");
            resultado = s.traducir_texto(this.cadena);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            Log.i("ANDROID", "entrando a post : " + resultado);
            Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            // cuando finaliza
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Log.i("ANDROID", "resultado a audio : " + resultado);
                if(resultado.length() > 0){
                    iniciarDialogo(resultado);
                }else{
                    Toast.makeText(getApplicationContext(), "No se encontro ninguna palabra", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void onPause(){
        if(text_to_voz !=null){
            text_to_voz.stop();
            text_to_voz.shutdown();
        }
        super.onPause();
    }
}
