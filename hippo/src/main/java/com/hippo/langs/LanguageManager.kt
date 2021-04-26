package com.hippo.langs

import android.content.Context
import android.content.res.Configuration
import com.hippo.utils.fileUpload.Prefs
import java.util.*

/**
 * Created by gurmail on 2020-06-18.
 * @author gurmail
 */
object LanguageManager {

    val language: String = "language"
    val defaultLang: String = "en"
    /**
     * Method to set the name value
     *
     * @param context the context
     */
    fun getLanguage(context: Context): String {
        return Prefs.with(context).getString(language, defaultLang)
    }

    fun isEnglishLanguage(context: Context): Boolean {
        val language = defaultLang
        return getLanguage(context) == language
    }


    fun setLanguageStrings(context: Context, translations: Translation, languageCode: String) {
        setLanguage(context, languageCode)
        Restring.saveStrings(translations)
    }

    fun updateLanguage(language: String) {
        FetchLanguageData.fetchLanguage(language)
//        val data = FetchLanguageData.demoString()
//        val translations: Translation = Translation()
//        translations.setValues(data)
//        Restring.saveStrings(translations)
    }

    /**
     * Method to set the name value
     */
    private fun setLanguage(context: Context, value: String) {
        Prefs.with(context).save(language, value)

//        val locale = Locale(value)
//        Locale.setDefault(locale)
//
//        val config = Configuration()
//        config.locale = locale
//
//        val resources = context.resources
//        resources.updateConfiguration(config, resources.displayMetrics)
    }
}