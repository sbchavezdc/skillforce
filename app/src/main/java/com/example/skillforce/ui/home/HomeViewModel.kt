package com.example.skillforce.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Aqui tiene que ir el diseño"
    }
    val text: LiveData<String> = _text
}