package com.asoft.blog.ui.addpost.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.asoft.blog.R
import com.asoft.blog.data.remote.Post
import com.asoft.blog.databinding.DialogBottomSheetAddPostBinding
import com.asoft.blog.ui.main.viewmodel.MainViewModel
import com.asoft.blog.ui.main.viewmodel.Status
import com.asoft.blog.utils.Constants
import com.asoft.blog.utils.hideViewWithAnimation
import com.asoft.blog.utils.showAlertDialog
import com.asoft.blog.utils.showViewWithAnimation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddPostBS : BottomSheetDialogFragment() {
    private lateinit var binding: DialogBottomSheetAddPostBinding
    private val viewModel: MainViewModel by viewModels()

    private val bottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        }

    private var contentView: View? = null
    private val isExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (isExpanded) {
            //  if you wanna show the bottom sheet as full screen,
            val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
            bottomSheetDialog.setOnShowListener { dialog: DialogInterface ->
                val bottomSheet: FrameLayout? = (dialog as BottomSheetDialog)
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet)
                if (bottomSheet != null) BottomSheetBehavior
                    .from<View>(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
            return bottomSheetDialog
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contentView = view
        otherConfigs()
        listeners()
        observePost()
    }

    private fun observePost() {
        viewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                Status.SUCCESS -> {
                    dismissAllowingStateLoss()
                    binding.progress.hideViewWithAnimation()
                }
                Status.LOADING -> {
                    //show loading dialog
                    binding.progress.showViewWithAnimation()
                }
                Status.ERROR -> {
                    //show alert
                    binding.progress.hideViewWithAnimation()
                }
                else -> {}
            }
        }
    }

    private fun getActualFormattedDate(): String {
        val currentTime: Date = Calendar.getInstance(Locale.getDefault()).time
        val simpleDateFormat = SimpleDateFormat("dd.LLL.yyyy HH:mm:ss", Locale.getDefault())
        return simpleDateFormat.format(currentTime).toString()
    }

    private fun listeners() {
        binding.closeIv.setOnClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnAddpost.setOnClickListener {
            val post = Post(
                author = Constants.name,
                date = getActualFormattedDate(),
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString(),
                file = if (Constants.photoUrl !=null) Constants.photoUrl.toString() else ""
            )
            viewModel.addPost(post)
        }

        binding.etTitle.addTextChangedListener {
            viewModel.setTitle(it.toString())
            if (it?.length!! > 0) {
                lifecycleScope.launch {
                    viewModel.titleMessages.collect { value ->
                        if (value != "") {
                            binding.tilTitle.isErrorEnabled = true
                            binding.tilTitle.error = value
                        } else {
                            binding.tilTitle.isErrorEnabled = false
                            binding.tilTitle.error = null
                        }
                    }
                }
            } else {
                binding.tilTitle.isErrorEnabled = true
                binding.tilTitle.error = "Este campo es obligatorio"
            }
        }

        binding.etDescription.addTextChangedListener {
            viewModel.setDescription(it.toString())
            if (it?.length!! > 0) {
                lifecycleScope.launch {
                    viewModel.descMessages.collect { value ->
                        if (value != "") {
                            binding.tilDescription.isErrorEnabled = true
                            binding.tilDescription.error = value
                        } else {
                            binding.tilDescription.isErrorEnabled = false
                            binding.tilDescription.error = null
                        }
                    }
                }
            } else {
                binding.tilDescription.isErrorEnabled = true
                binding.tilDescription.error = "Este campo es obligatorio"
            }
        }

        lifecycleScope.launch {
            viewModel.isSubmitEnabled.collect { value ->
                binding.btnAddpost.isEnabled = value
            }
        }
    }

    //Lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomSheetAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun otherConfigs() {
        // create a color state list programmatically
        val cstates = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled) // disabled
        )
        val colors = intArrayOf(
            Color.parseColor("#112B44"), // enabled color
            Color.parseColor("#A6A4A4") // disabled color
        )
        val colorStates = ColorStateList(cstates, colors)

        // set button background tint
        binding.btnAddpost.backgroundTintList = colorStates
    }
}