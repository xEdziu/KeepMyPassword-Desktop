package me.goral.keepmypassworddesktop.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LanguageConverter {

    // KEY -> LOCALE
    // VALUE -> LANGUAGE

    //COMPLETE LIST using:
    // a)  https://www.oracle.com/java/technologies/javase/java8locales.html
    // and b) https://wiki.freepascal.org/Language_Codes
    private HashMap<String, String> langs = new HashMap<>();

    public LanguageConverter(){
        this.langs.put("sq-al","Albania");//NON-NLS
        this.langs.put("ar-dz","Algeria");//NON-NLS
        this.langs.put("ar-bh","Bahrain");//NON-NLS
        this.langs.put("ar-eg","Egypt");//NON-NLS
        this.langs.put("ar-iq","Iraq");//NON-NLS
        this.langs.put("ar-jo","Jordan");//NON-NLS
        this.langs.put("ar-kw","Kuwait");//NON-NLS
        this.langs.put("ar-lb","Lebanon");//NON-NLS
        this.langs.put("ar-ly","Libya");//NON-NLS
        this.langs.put("ar-ma","Morocco");//NON-NLS
        this.langs.put("sq-AL","Oman");//NON-NLS
    }

    public String convertToLanguage(String locale){
        return this.langs.get(locale);
    }

    public String convertToLocale(String language){
        for (Map.Entry<String, String> entry : this.langs.entrySet()){
            if (Objects.equals(entry.getValue(), language)){
                return entry.getKey();
            }
        }
        return null;
    }
}
