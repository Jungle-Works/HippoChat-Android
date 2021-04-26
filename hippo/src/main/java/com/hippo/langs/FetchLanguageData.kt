package com.hippo.langs

import com.hippo.HippoConfig
import com.hippo.database.CommonData
import com.hippo.model.LangRequest
import com.hippo.model.MultilangualResponse
import com.hippo.retrofit.*
import com.hippo.utils.HippoLog

/**
 * Created by gurmail on 2020-06-18.
 * @author gurmail
 */
object FetchLanguageData {
    fun fetchLanguage(lang: String) {
        val params = LangRequest(CommonData.getUserDetails().data.appSecretKey, 0, lang)
        RestClient.getApiInterface().getLanguageData(params)
            .enqueue(object : ResponseResolver<MultilangualResponse>() {
                override fun success(response: MultilangualResponse?) {
                    Restring.saveStrings(response?.data)
                    CommonData.saveCurrentLang(lang)
                    updateUserLanguage(lang)
                }
                override fun failure(error: APIError?) {

                }
            })
    }

    fun updateUserLanguage(lang: String) {

        val params = CommonParams.Builder()
            .add("app_secret_key", CommonData.getUserDetails().data.appSecretKey)
            .add("en_user_id", CommonData.getUserDetails().data.en_user_id)
            .add("update_lang", lang)
            .build()

        RestClient.getApiInterface().updateUserLanguage(params.map)
            .enqueue(object : ResponseResolver<MultilangualResponse>() {
                override fun success(response: MultilangualResponse?) {

                }
                override fun failure(error: APIError?) {

                }
            })
    }
}