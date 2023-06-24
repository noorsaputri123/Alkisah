
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rie.alkisah.model.MrUser
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
//Noor Saputri
class MrPref private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<MrUser> {
        return dataStore.data.map { preferences ->
            MrUser(
                preferences[NAME_KEY] ?:"",
                preferences[EMAIL_KEY] ?:"",
                preferences[TOKEN_KEY] ?:"",
                preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveUser(user: MrUser) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.users
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[STATE_KEY] = user.isLogin
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
            preferences[STATE_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MrPref? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): MrPref {
            return INSTANCE ?: synchronized(this) {
                val instance = MrPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}