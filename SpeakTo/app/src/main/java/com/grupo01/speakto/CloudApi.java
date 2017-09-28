package com.grupo01.speakto;

import android.database.CursorJoiner;
import android.util.Log;
import android.widget.Toast;


//import com.ibm.watson.developer_cloud.language_translation.v2.model.IdentifiedLanguage;
import com.ibm.watson.developer_cloud.language_translator.v2.model.IdentifiedLanguage;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;

import org.json.JSONObject;

import java.util.List;


/**
 *
 *  { voz to texto
 "  url": "https://stream.watsonplatform.net/speech-to-text/api",
 "  username": "737719bd-3aed-44d9-8933-3389ec4f4442",
 "  password": "MuTB25zFjLeG"
 *  }
 *
 *
 *  texto to voz
    url": "https://stream.watsonplatform.net/text-to-speech/api"
    username": "eb5008a4-09af-4914-abd1-553c30f615e4"
    password": "mMaUL3c1PAff"
 *
 *
 *
 * traductor de texto a texto de ingles a español e viceversa
 *  {
     "url": "https://gateway.watsonplatform.net/language-translator/api",
     "username": "7cb30235-1358-4c9b-a904-7bc596c6c5c5",
     "password": "Aw6etoqFQp1x"
    }
 *
 *
 *
 */

public class CloudApi {

    private LanguageTranslator traductor;

    public void CloudApi(){
    }

    public String traducir_texto(String cadena){

        String language = this.identificarLenguaje(cadena);
        String resultado = "";
        switch (language){
            case "es": {
                Log.i("ANDROID", " entrando a español");
                resultado = this.traducir_spanish_ingles(cadena);
            }
            break;
            case "en": {
                Log.i("ANDROID", " entrando a ingles");
                resultado = this.traducir_ingles_to_spanish(cadena);
            }
            break;
            default:
                resultado = "no se detecto idioma " + language;
            break;
        }
        Log.i("ANDROID", "No se detecto lenguage valido : " + language);
        Log.i("ANDROID", "No se detecto lenguage CONST-es : " + Language.SPANISH);
        Log.i("ANDROID", "No se detecto lenguage CONST-en : " + Language.ENGLISH);

    return resultado;

    }

    public String identificarLenguaje(String idioma){

        traductor = new LanguageTranslator();
        traductor.setUsernameAndPassword("7cb30235-1358-4c9b-a904-7bc596c6c5c5", "Aw6etoqFQp1x");

        Log.i("ANDROID", "entrando a identificar");
        List<IdentifiedLanguage> languages = traductor.identify(idioma).execute();
        Log.i("ANDROID", "lista de identificadores : count : " + languages.size());
        String languageMay = "";
        double confidencia = 0;
        for (int i = 0; i < languages.size(); i++) {
            IdentifiedLanguage identificador = languages.get(i);
            if(identificador.getConfidence() > confidencia){
                confidencia = identificador.getConfidence();
                languageMay = identificador.getLanguage();
                Log.i("ANDROID", "for confi : " + String.valueOf(confidencia));
                Log.i("ANDROID", "for langu : " + languageMay);
            }
        }
        return languageMay;
    }
    private String traducir_ingles_to_spanish(String cadena){
        try{
            Log.i("ANDROID", "entrando a ingles to spañol : " + cadena);
            traductor = new LanguageTranslator();
            traductor.setUsernameAndPassword("7cb30235-1358-4c9b-a904-7bc596c6c5c5", "Aw6etoqFQp1x");

            TranslationResult resultado = traductor.translate(cadena, Language.ENGLISH, Language.SPANISH).execute();

            Log.i("ANDROID", "saliendo correctamente : ");

            Log.i("ANDROID", "TRD2 : " + resultado.getFirstTranslation());

            return resultado.getFirstTranslation();
        }catch (Exception e){

            Log.i("ANDROID", "Error de trad : " + e.getMessage());
            return "";
        }
    }
    private String traducir_spanish_ingles(String cadena){
        try{
            Log.i("ANDROID", "entrando a español a ingles : " + cadena);
            traductor = new LanguageTranslator();
            traductor.setUsernameAndPassword("7cb30235-1358-4c9b-a904-7bc596c6c5c5", "Aw6etoqFQp1x");

            TranslationResult resultado = traductor.translate(cadena, Language.SPANISH, Language.ENGLISH).execute();

            Log.i("ANDROID", "saliendo correctamente : ");

            Log.i("ANDROID", "TRD2 : " + resultado.getFirstTranslation());
            return resultado.getFirstTranslation();
        }catch (Exception e){
            Log.i("ANDROID", "Error de trad : " + e.getMessage());
            return "";
        }

    }
}
