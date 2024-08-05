package com.sleep_paralysis_monitor.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUpScreen()
        }
    }
}

@Composable
fun SignUpScreen() {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var age by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState()),

    contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black)
        ) {
            Text(text = "Sign Up", color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(0.75f),
                textStyle= TextStyle(color = Color.White)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(0.75f),
                textStyle= TextStyle(color = Color.White)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(0.75f),
                textStyle= TextStyle(color = Color.White)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(0.75f),
                textStyle= TextStyle(color = Color.White)
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(0.75f),
                textStyle= TextStyle(color = Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = {
                    val ageInt = age.text.toIntOrNull()
                    when {
                        fullName.text.isBlank() -> Toast.makeText(context, "Full name cannot be empty.", Toast.LENGTH_LONG).show()
                        email.text.isBlank() -> Toast.makeText(context, "Email cannot be empty.", Toast.LENGTH_LONG).show()
                        password.text.length < 6 -> Toast.makeText(context, "Password must be at least 6 characters.", Toast.LENGTH_LONG).show()
                        password.text != confirmPassword.text -> Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_LONG).show()
                        ageInt == null || ageInt !in 1..120 -> Toast.makeText(context, "Invalid age. Must be between 1 and 120.", Toast.LENGTH_LONG).show()
                        else -> {
                            isLoading = true
                            auth.createUserWithEmailAndPassword(email.text, password.text)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val userId = task.result?.user?.uid ?: ""
                                        val user = hashMapOf(
                                            "fullName" to fullName.text,
                                            "age" to ageInt,
                                            "heartRateData" to listOf<Map<String, Any>>(),
                                            "anomalyData" to listOf<Map<String, Any>>()
                                        )
                                        db.collection("users").document(userId).set(user)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                val intent = Intent(context, DashActivity::class.java)
                                                context.startActivity(intent)
                                                (context as Activity).finish()
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                Toast.makeText(context, "Failed to create user profile: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(0.75f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    androidx.wear.compose.material.Text(text = "SignUp")
                }
            }
        }
    }
}
