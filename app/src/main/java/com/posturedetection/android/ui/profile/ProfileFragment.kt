package com.posturedetection.android.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.posturedetection.android.PersonalInformationActivity
import com.posturedetection.android.data.LoginUser
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
        auth = FirebaseAuth.getInstance()
        val root: View = binding.root

        val gson = Gson()

       //sp = getPreferences(MODE_PRIVATE)
        val sp: SharedPreferences = requireActivity().getSharedPreferences("Login", AppCompatActivity.MODE_PRIVATE)
        val json: String? = sp.getString("account","")
        val account: LoginUser = gson.fromJson(json, LoginUser::class.java)
        Log.d("ProfileFragment", "onCreateView: $account")
        binding.profileUsername.text = account.name
        binding.profileEmail.text = account.email

        binding.btnPersonalInformation.setOnClickListener(View.OnClickListener {
            var intent = Intent()

            startActivity(requireActivity().intent.setClass(requireActivity(), PersonalInformationActivity::class.java))
        })

        binding.signOut.setOnClickListener {
            auth.signOut()
            //close the app
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