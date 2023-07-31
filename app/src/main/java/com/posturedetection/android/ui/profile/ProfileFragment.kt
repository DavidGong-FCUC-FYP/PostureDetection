package com.posturedetection.android.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.posturedetection.android.data.User
import com.posturedetection.android.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gson = Gson()

       //sp = getPreferences(MODE_PRIVATE)
        val sp: SharedPreferences = requireActivity().getSharedPreferences("Login", AppCompatActivity.MODE_PRIVATE)
        val json: String? = sp.getString("account","")
        val account: User = gson.fromJson(json, User::class.java)
        Log.d("ProfileFragment", "onCreateView: $account")
        binding.profileUsername.text = account.name
        binding.profileEmail.text = account.email

        binding.signOut.setOnClickListener {
            auth.signOut()
            val editor = sp.edit()
            editor.clear()
            editor.apply()
            requireActivity().finish()
        }

//        val textView: TextView = binding.textNotifications
//        profileViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}