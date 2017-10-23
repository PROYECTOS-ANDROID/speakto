package com.grupo01.speakto;

import android.util.Log;

//import com.ibm.watson.developer_cloud.language_translation.v2.model.IdentifiedLanguage;
import com.ibm.watson.developer_cloud.language_translator.v2.model.IdentifiedLanguage;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;

import org.json.JSONObject;

import java.util.List;


/**
 *
 * traductor de texto a texto de ingles a español e viceversa
 *
 * 49d7f6de-0138-4f5b-8f78-ac48cdd93142
 * 7Zz0RNhGCWHQ
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
        traductor.setUsernameAndPassword("49d7f6de-0138-4f5b-8f78-ac48cdd93142", "7Zz0RNhGCWHQ");

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
            traductor.setUsernameAndPassword("49d7f6de-0138-4f5b-8f78-ac48cdd93142", "7Zz0RNhGCWHQ");

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
            traductor.setUsernameAndPassword("49d7f6de-0138-4f5b-8f78-ac48cdd93142", "7Zz0RNhGCWHQ");

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
