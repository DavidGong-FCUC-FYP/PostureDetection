package com.posturedetection.android.ui.detection

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.posturedetection.android.R
import com.posturedetection.android.camera.CameraSource
import com.posturedetection.android.data.Camera
import com.posturedetection.android.data.Device
import com.posturedetection.android.ml.ModelType
import com.posturedetection.android.ml.MoveNet
import com.posturedetection.android.ml.PoseClassifier

class DetectionFragment : Fragment() {

    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    private lateinit var surfaceView: SurfaceView
    private lateinit var tvDebug: TextView
    private lateinit var tvToolbarTitle: TextView
    private lateinit var tvFPS: TextView
    private lateinit var tvScore: TextView
    private lateinit var spnDevice: Spinner
    private lateinit var spnCamera: Spinner

    private var cameraSource: CameraSource? = null
    private var isClassifyPose = true
    private var device = Device.CPU
    private var selectedCamera = Camera.BACK

    private var forwardheadCounter = 0
    private var crosslegCounter = 0
    private var standardCounter = 0
    private var missingCounter = 0
    private var poseRegister = "standard"

//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//            if (isGranted) {
//                openCamera()
//            } else {
//                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
//                    .show(childFragmentManager, FRAGMENT_DIALOG)
//            }
//        }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // If the user has not selected any device, use the default device.
        }
    }

    private var changeCameraListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, view: View?, direction: Int, id: Long) {
            changeCamera(direction)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            // If the user has not selected any camera, use the default camera.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_detection, container, false)

        // Initialize your views here
        tvScore = rootView.findViewById(R.id.tvScore)
        tvDebug = rootView.findViewById(R.id.tvDebug)
        tvToolbarTitle = rootView.findViewById(R.id.tvToolbarTitle)
        tvFPS = rootView.findViewById(R.id.tvFps)
        spnDevice = rootView.findViewById(R.id.spnDevice)
        spnCamera = rootView.findViewById(R.id.spnCamera)
        surfaceView = rootView.findViewById(R.id.surfaceView)

        initSpinner()
//
//        if (!isCameraPermissionGranted()) {
//            requestPermission()
//        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tfe_pe_device_name, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnDevice.adapter = adapter
            spnDevice.onItemSelectedListener = changeDeviceListener
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tfe_pe_camera_name, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCamera.adapter = adapter
            spnCamera.onItemSelectedListener = changeCameraListener
        }
    }



//    private fun openCamera() {
//        // Initialize MediaPlayer for audio feedback
//        val crosslegPlayer = MediaPlayer.create(requireContext(), R.raw.crossleg)
//        val forwardheadPlayer = MediaPlayer.create(requireContext(), R.raw.forwardhead)
//        val standardPlayer = MediaPlayer.create(requireContext(), R.raw.standard)
//        var crosslegPlayerFlag = true
//        var forwardheadPlayerFlag = true
//        var standardPlayerFlag = true
//
//      //  if (isCameraPermissionGranted()) {
//            if (cameraSource == null) {
//                cameraSource = CameraSource(
//                    surfaceView,
//                    selectedCamera,
//                    object : CameraSource.CameraSourceListener {
//                        override fun onFPSListener(fps: Int) {
//                            // Update FPS information
//                            tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
//                        }
//
//                        override fun onDetectedInfo(
//                            personScore: Float?,
//                            poseLabels: List<Pair<String, Float>>?
//                        ) {
//                            // Handle the detected information here, just like in the MainActivity
//                            // ...
//                            // (Your pose detection and feedback logic)
//                        }
//                    }).apply {
//                    prepareCamera()
//                }
//                isPoseClassifier()
//                lifecycleScope.launch(Dispatchers.Main) {
//                    cameraSource?.initCamera()
//                }
//            }
//            createPoseEstimator()
//      //  }
//    }



    private fun openCamera() {
        /** 音频播放 */
        val crosslegPlayer = MediaPlayer.create(requireContext(), R.raw.crossleg)
        val forwardheadPlayer = MediaPlayer.create(requireContext(), R.raw.forwardhead)
        val standardPlayer = MediaPlayer.create(requireContext(), R.raw.standard)
        var crosslegPlayerFlag = true
        var forwardheadPlayerFlag = true
        var standardPlayerFlag = true
        //if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(
                        surfaceView,
                        selectedCamera,
                        object : CameraSource.CameraSourceListener {
                            override fun onFPSListener(fps: Int) {

                                /** 解释一下，tfe_pe_tv 的意思：tensorflow example、pose estimation、text view */
                                tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                            }

                            /** 对检测结果进行处理 */
                            override fun onDetectedInfo(
                                personScore: Float?,
                                poseLabels: List<Pair<String, Float>>?
                            ) {
                                tvScore.text =
                                    getString(R.string.tfe_pe_tv_score, personScore ?: 0f)

                                /** 分析目标姿态，给出提示 */
                                if (poseLabels != null && personScore != null && personScore > 0.3) {
                                    missingCounter = 0
                                    val sortedLabels = poseLabels.sortedByDescending { it.second }
                                    when (sortedLabels[0].first) {
                                        "forwardhead" -> {
                                            crosslegCounter = 0
                                            standardCounter = 0
                                            if (poseRegister == "forwardhead") {
                                                forwardheadCounter++
                                            }
                                            poseRegister = "forwardhead"

                                            /** 显示当前坐姿状态：脖子前伸 */
                                            if (forwardheadCounter > 60) {

                                                /** 播放提示音 */
                                                if (forwardheadPlayerFlag) {
                                                    forwardheadPlayer.start()
                                                }
                                                standardPlayerFlag = true
                                                crosslegPlayerFlag = true
                                                forwardheadPlayerFlag = false

                                                tvToolbarTitle.setText("please don't forward your head");
                                                tvToolbarTitle.setTextColor(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.red
                                                    )
                                                );

                                                //ivStatus.setImageResource(R.drawable.forwardhead_confirm)
                                            } else if (forwardheadCounter > 30) {
                                                tvToolbarTitle.setText("Are you forward your head?");
                                                tvToolbarTitle.setTextColor(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.yellow
                                                    )
                                                );
                                                //tvToolbarTitle.setText("Your sitting posture have problem.");
                                                // ivStatus.setImageResource(R.drawable.forwardhead_suspect)
                                            }

                                            /** 显示 Debug 信息 */
                                            tvDebug.text = getString(
                                                R.string.tfe_pe_tv_debug,
                                                "${sortedLabels[0].first} $forwardheadCounter"
                                            )
                                        }

                                        "crossleg" -> {
                                            forwardheadCounter = 0
                                            standardCounter = 0
                                            if (poseRegister == "crossleg") {
                                                crosslegCounter++
                                            }
                                            poseRegister = "crossleg"

                                            /** 显示当前坐姿状态：翘二郎腿 */
                                            if (crosslegCounter > 60) {

                                                /** 播放提示音 */
                                                if (crosslegPlayerFlag) {
                                                    crosslegPlayer.start()
                                                }
                                                standardPlayerFlag = true
                                                crosslegPlayerFlag = false
                                                forwardheadPlayerFlag = true
                                                tvToolbarTitle.setText("please don't cross your legs");
                                                tvToolbarTitle.setTextColor(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.red
                                                    )
                                                );
                                                //ivStatus.setImageResource(R.drawable.crossleg_confirm)
                                            } else if (crosslegCounter > 30) {
                                                tvToolbarTitle.setText("Are you crossing your legs?");
                                                tvToolbarTitle.setTextColor(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.yellow
                                                    )
                                                );
                                                //ivStatus.setImageResource(R.drawable.crossleg_suspect)
                                            }

                                            /** 显示 Debug 信息 */
                                            tvDebug.text = getString(
                                                R.string.tfe_pe_tv_debug,
                                                "${sortedLabels[0].first} $crosslegCounter"
                                            )
                                        }

                                        else -> {
                                            forwardheadCounter = 0
                                            crosslegCounter = 0
                                            if (poseRegister == "standard") {
                                                standardCounter++
                                            }
                                            poseRegister = "standard"

                                            /** 显示当前坐姿状态：标准 */
                                            if (standardCounter > 30) {

                                                /** 播放提示音：坐姿标准 */
                                                if (standardPlayerFlag) {
                                                    standardPlayer.start()
                                                }
                                                standardPlayerFlag = false
                                                crosslegPlayerFlag = true
                                                forwardheadPlayerFlag = true

                                                tvToolbarTitle.setText("Your sitting posture is standard.");
                                                tvToolbarTitle.setTextColor(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.teal_200
                                                    )
                                                );

                                                //ivStatus.setImageResource(R.drawable.standard)
                                            }

                                            /** 显示 Debug 信息 */
                                            tvDebug.text = getString(
                                                R.string.tfe_pe_tv_debug,
                                                "${sortedLabels[0].first} $standardCounter"
                                            )
                                        }
                                    }


                                } else {
                                    missingCounter++
                                    if (missingCounter > 30) {
                                        tvToolbarTitle.setText("Please sit in front of the camera.");
                                        tvToolbarTitle.setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.white
                                            )
                                        );
                                        // ivStatus.setImageResource(R.drawable.no_target)
                                    }

                                    /** 显示 Debug 信息 */
                                    tvDebug.text = getString(
                                        R.string.tfe_pe_tv_debug,
                                        "missing $missingCounter"
                                    )
                                }
                            }
                        }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        //}
    }

    private fun isCameraPermissionGranted(): Boolean {
        return requireContext().let {
            return ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }
//        return checkPermission(
//            Manifest.permission.CAMERA,
//            Process.myPid(),
//            Process.myUid()
//        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(requireContext()) else null)
    }

    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    private fun changeCamera(direction: Int) {
        val targetCamera = when (direction) {
            0 -> Camera.BACK
            else -> Camera.FRONT
        }
        if (selectedCamera == targetCamera) return
        selectedCamera = targetCamera

        cameraSource?.close()
        cameraSource = null
        openCamera()
    }

    private fun createPoseEstimator() {
        val poseDetector = MoveNet.create(requireContext(), device, ModelType.Thunder)
        poseDetector.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

//    private fun requestPermission() {
//        when (PackageManager.PERMISSION_GRANTED) {
//            ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CAMERA
//            ) -> {
//                openCamera()
//            }
//            else -> {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.CAMERA
//                )
//            }
//        }
//    }



    class ErrorDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // pass
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}
