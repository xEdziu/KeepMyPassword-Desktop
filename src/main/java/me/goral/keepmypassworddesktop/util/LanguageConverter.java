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

    @SuppressWarnings("HardCodedStringLiteral")
    public LanguageConverter(){
        this.langs.put("sq-al","Albania");
        this.langs.put("ar-dz","Algeria");
        this.langs.put("ar-bh","Bahrain");
        this.langs.put("ar-eg","Egypt");
        this.langs.put("ar-iq","Iraq");
        this.langs.put("ar-jo","Jordan");
        this.langs.put("ar-kw","Kuwait");
        this.langs.put("ar-lb","Lebanon");
        this.langs.put("ar-ly","Libya");
        this.langs.put("ar-ma","Morocco");
        this.langs.put("sq-AL","Oman");
        this.langs.put("ar-qa","Qatar");
        this.langs.put("ar-sa","Saudi Arabia");
        this.langs.put("ar-sd","Sudan");
        this.langs.put("ar-sy","Syria");
        this.langs.put("ar-tn","Tunisia");
        this.langs.put("ar-ae","United Arab Emirates");
        this.langs.put("ar-ye","Yemen");
        this.langs.put("be-by","Belarus");
        this.langs.put("bg-bg","Bulgaria");
        this.langs.put("ca-es","Spain - Catalan");
        this.langs.put("zh-cn","China");
        this.langs.put("zh-sg","Singapore");
        this.langs.put("zh-hk","Hong Kong");
        this.langs.put("zh-tw","Taiwan");
        this.langs.put("hr-hr","Croatia");
        this.langs.put("cs-cz","Czech Republic");
        this.langs.put("da-dk","Denmark");
        this.langs.put("nl-be","Belgium");
        this.langs.put("nl-nl","Netherlands");
        this.langs.put("en-au","Australia English");
        this.langs.put("en-ca","Canada English");
        this.langs.put("en-in","India English");
        this.langs.put("en-ie","Ireland English");
        this.langs.put("en-mt","Malta English");
        this.langs.put("en-nz","New Zealand English");
        this.langs.put("en-ph","Philippines English");
        this.langs.put("en-sg","Singapore English");
        this.langs.put("en-za","South Africa English");
        this.langs.put("en-gb","Great Britain English");
        this.langs.put("en-us","USA English");
        this.langs.put("et-ee","Estonia");
        this.langs.put("fi-fi","Finland");
        this.langs.put("fr-be","Belgium French");
        this.langs.put("fr-ca","Canada French");
        this.langs.put("fr-fr","France");
        this.langs.put("fr-lu","Luxembourg French");
        this.langs.put("fr-ch","Switzerland French");
        this.langs.put("de-at","Austria");
        this.langs.put("de-de","Germany");
        this.langs.put("de-lu","Luxembourg German");
        this.langs.put("de-ch","Switzerland German");
        this.langs.put("el-cy","Cyprus");
        this.langs.put("el-gr","Greece");
        this.langs.put("iw-il","Israel");
        this.langs.put("hi-in","India Hindi");
        this.langs.put("hu-hu","Hungary");
        this.langs.put("is-is","Iceland");
        this.langs.put("in-id","Indonesia");
        this.langs.put("ga-ie","Ireland Irish");
        this.langs.put("it-it","Italy");
        this.langs.put("it-ch","Switzerland Italian");
        this.langs.put("ja-jp","Japan");
        this.langs.put("ko-kr","South Korea");
        this.langs.put("lv-lv","Latvia");
        this.langs.put("lt-lt","Lithuania");
        this.langs.put("mk-mk","Macedonia");
        this.langs.put("ms-my","Malaysia");
        this.langs.put("mt-mt","Malta");
        this.langs.put("no-no","Norway");
        this.langs.put("pl-pl","Poland");
        this.langs.put("pt-br","Brazil");
        this.langs.put("pt-pt","Portugal");
        this.langs.put("ro-ro","Romania");
        this.langs.put("ru-ru","Russia");
        this.langs.put("sr-ba","Bosnia and Herzegovina");
        this.langs.put("sr-me","Montenegro");
        this.langs.put("sr-rs","Serbia");
        this.langs.put("sk-sk","Slovakia");
        this.langs.put("sl-si","Slovenia");
        this.langs.put("es-ar","Argentina");
        this.langs.put("es-bo","Bolivia");
        this.langs.put("es-cl","Chile");
        this.langs.put("es-co","Colombia");
        this.langs.put("es-cr","Costa Rica");
        this.langs.put("es-do","Dominican Republic");
        this.langs.put("es-ec","Ecuador");
        this.langs.put("es-sv","El Salvador");
        this.langs.put("es-gt","Guatemala");
        this.langs.put("es-hn","Honduras");
        this.langs.put("es-mx","Mexico");
        this.langs.put("es-ni","Nicaragua");
        this.langs.put("es-pa","Panama");
        this.langs.put("es-py","Paraguay");
        this.langs.put("es-pe","Peru");
        this.langs.put("es-pr","Puerto Rico");
        this.langs.put("es-es","Spain");
        this.langs.put("es-us","USA Spanish");
        this.langs.put("es-uy","Uruguay");
        this.langs.put("es-ve","Venezuela");
        this.langs.put("te-in","Telugu");
        this.langs.put("ta-in","Tamil");
        this.langs.put("sv-se","Sweden");
        this.langs.put("th-th","Thailand");
        this.langs.put("tr-tr","Turkey");
        this.langs.put("uk-ua","Ukraine");
        this.langs.put("vi-vn","Vietnam");
        this.langs.put("single-am","Amharic");
        this.langs.put("single-hy","Armenian");
        this.langs.put("gu-in","Gujarati English");
        this.langs.put("bn-bn","Bengali");
        this.langs.put("ma-in","Marathi");
        this.langs.put("sr-latn-ba", "Bosnian Latin");
        this.langs.put("np-np","Nepal");
        this.langs.put("mr-in","Marathi");
        this.langs.put("ur-pk","Urdu");
        this.langs.put("kn-in","Kannada India");
        this.langs.put("mal-in", "Malayalam");
        this.langs.put("ko-in","Konkani");
        this.langs.put("bh-in", "Bihari");
        this.langs.put("pu-in", "Punjab India");
        this.langs.put("am-et", "Ahamic");

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
