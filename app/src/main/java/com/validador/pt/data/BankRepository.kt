package com.validador.pt.data

import android.content.Context
import com.validador.pt.data.model.Bank
import org.json.JSONArray
import org.json.JSONObject

class BankRepository private constructor() {
    
    private var banks: List<Bank> = emptyList()
    
    companion object {
        @Volatile
        private var INSTANCE: BankRepository? = null
        
        fun getInstance(context: Context): BankRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = BankRepository()
                instance.loadBanks(context)
                INSTANCE = instance
                instance
            }
        }
    }
    
    private fun loadBanks(context: Context) {
        try {
            val jsonString = context.assets.open("banks.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val result = mutableListOf<Bank>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                result.add(
                    Bank(
                        code = obj.getString("code"),
                        name = obj.getString("name"),
                        swift = if (obj.has("swift")) obj.getString("swift") else null
                    )
                )
            }
            banks = result
        } catch (e: Exception) {
            banks = emptyList()
        }
    }
    
    fun getBankName(code: String): String? {
        return banks.find { it.code == code }?.name
    }
    
    fun getBank(code: String): Bank? {
        return banks.find { it.code == code }
    }
}
