package com.posturedetection.android.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posturedetection.android.data.model.User

class ProfileViewModel : ViewModel() {
    var user = User()

}