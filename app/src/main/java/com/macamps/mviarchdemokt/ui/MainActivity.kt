package com.macamps.mviarchdemokt.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.macamps.mviarchdemokt.MainContract
import com.macamps.mviarchdemokt.MainViewModel
import com.macamps.mviarchdemokt.SecondActivity
import com.macamps.mviarchdemokt.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        binding.generateNumber.setOnClickListener {
            viewModel.setEvent(MainContract.Event.OnRandomNumberClicked)
        }
        binding.showToast.setOnClickListener {
            viewModel.setEvent(MainContract.Event.OnShowToastClicked)
        }
        binding.secondActivity.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }

    /**
     * Initialize Observers
     */
    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it.randomNumberState) {
                    is MainContract.RandomNumberState.Idle -> {
                        binding.progressBar.isVisible = false
                    }
                    is MainContract.RandomNumberState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is MainContract.RandomNumberState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.number.text = it.randomNumberState.number.toString()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                when (it) {
                    is MainContract.Effect.ShowToast -> {
                        binding.progressBar.isVisible = false
                        showToast("Error, number is even")
                    }
                }
            }
        }
    }


    /**
     * Show simple toast message
     */
    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}