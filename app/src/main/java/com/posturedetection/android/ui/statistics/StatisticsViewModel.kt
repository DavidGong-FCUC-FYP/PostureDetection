package com.posturedetection.android.ui.statistics

import Counter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class StatisticsViewModel : ViewModel() {
    val counterData = Counter()
}
