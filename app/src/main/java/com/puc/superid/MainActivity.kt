package com.puc.superid
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val testDoc = db.collection("test").document("check")

        testDoc.set(mapOf("status" to "success"))
            .addOnSuccessListener {
                Log.d("FirebaseTest", "Firestore está funcionando corretamente!")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "Erro ao acessar Firestore", e)
            }
    }
}