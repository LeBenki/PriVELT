/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.biometric

import android.content.Context.MODE_PRIVATE
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.google.crypto.tink.Aead
import com.google.crypto.tink.Config
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadFactory
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.util.concurrent.Executors


class BiometricPromptTinkManager(private val activity: FragmentActivity) {

    private val sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
    private val aead: Aead

    init {
        Config.register(TinkConfig.LATEST)
        aead = AeadFactory.getPrimitive(getOrGenerateNewKeysetHandle())
    }

    fun isFingerPrintAvailable(): Boolean {
        val fingerprintManagerCompat = BiometricManager.from(activity).canAuthenticate()

        return (fingerprintManagerCompat == BIOMETRIC_SUCCESS)
    }

    fun decryptPrompt(failedAction: () -> Unit, successAction: (ByteArray) -> Unit) {
        try {
            handleDecrypt(failedAction, successAction)
        } catch (e: Exception) {
            failedAction()
        }
    }

    fun encryptPrompt(
            data: ByteArray,
            failedAction: () -> Unit,
            successAction: (ByteArray) -> Unit
    ) {
        try {
            handleEncrypt(data, failedAction, successAction)
        } catch (e: Exception) {
            failedAction()
        }
    }

    private fun getOrGenerateNewKeysetHandle(): KeysetHandle {
        return AndroidKeysetManager.Builder()
                .withSharedPref(activity, TINK_KEYSET_NAME, null)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
                .withMasterKeyUri(MASTER_KEY_URI)
                .build().keysetHandle
    }

    private fun handleEncrypt(
            data: ByteArray,
            failedAction: () -> Unit,
            successAction: (ByteArray) -> Unit
    ) {

        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val encryptedData = aead.encrypt(data, EMPTY_ASSOCIATED_DATA)
                saveEncryptedData(encryptedData)
                activity.runOnUiThread { successAction(encryptedData) }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                activity.runOnUiThread { failedAction() }
            }
        })

        val promptInfo = biometricPromptInfoEncrypt()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun saveEncryptedData(dataEncrypted: ByteArray) {
        sharedPreferences.edit().putString(DATA_ENCRYPTED, Base64.encodeToString(dataEncrypted, Base64.DEFAULT)).apply()
    }

    fun checkIfPreviousEncryptedData(): Boolean {
        return sharedPreferences.getString(DATA_ENCRYPTED, null) != null
    }

    fun clearMasterPassword() {
        sharedPreferences.edit().putString(DATA_ENCRYPTED, null).apply()
    }

    private fun getEncryptedData(): ByteArray? {
        val data = sharedPreferences.getString(DATA_ENCRYPTED, null)
        return when {
            data != null -> Base64.decode(data, Base64.DEFAULT)
            else -> null
        }
    }

    private fun handleDecrypt(
            failedAction: () -> Unit,
            successAction: (ByteArray) -> Unit
    ) {

        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val decryptedData = aead.decrypt(getEncryptedData(), EMPTY_ASSOCIATED_DATA)
                activity.runOnUiThread { successAction(decryptedData) }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                activity.runOnUiThread { failedAction() }
            }
        })

        val promptInfo = biometricPromptInfoDecrypt()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun biometricPromptInfoEncrypt(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
                .setTitle("PriVELT")
                .setSubtitle("Register fingerprint")
                .setDescription("Your master password will be stored in your device and encrypted with your fingerprint.")
                .setNegativeButtonText(activity.getString(android.R.string.cancel))
                .build()
    }

    private fun biometricPromptInfoDecrypt(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
                .setTitle("PriVELT")
                .setSubtitle("Login")
                .setDescription("Your fingerprint is used to decrypt your stored master password.")
                .setNegativeButtonText(activity.getString(android.R.string.cancel))
                .build()
    }

    companion object {
        private const val TINK_KEYSET_NAME = "tink_keyset"
        private const val DATA_ENCRYPTED = "data_encrypted"
        private const val MASTER_KEY_URI = "android-keystore://tink_master_key"
        private const val SHARED_PREFERENCES = "shared preferences"
        private val EMPTY_ASSOCIATED_DATA = ByteArray(0)
    }
}