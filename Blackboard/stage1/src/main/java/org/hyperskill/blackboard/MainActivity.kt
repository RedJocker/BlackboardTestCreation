package org.hyperskill.blackboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.blackboard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            loginUsername.error = "error"
        }
    }
}