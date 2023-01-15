package com.scrumteam.mytask.ui.profile

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.R
import com.scrumteam.mytask.databinding.BottomSheetChangePasswordBinding
import com.scrumteam.mytask.databinding.BottomSheetEditProfileBinding
import com.scrumteam.mytask.databinding.BottomSheetLogoutBinding
import com.scrumteam.mytask.databinding.FragmentProfileBinding
import com.scrumteam.mytask.ui.MainActivity
import com.scrumteam.mytask.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding as FragmentProfileBinding

    private var _bindingBottomSheetLogout: BottomSheetLogoutBinding? = null
    private val bindingBottomSheetLogout
        get() = _bindingBottomSheetLogout as BottomSheetLogoutBinding

    private var _bindingBottomSheetChangePassword: BottomSheetChangePasswordBinding? = null
    private val bindingBottomSheetChangePasswordBinding
        get() = _bindingBottomSheetChangePassword as BottomSheetChangePasswordBinding

    private var _bindingBottomSheetEditProfile: BottomSheetEditProfileBinding? = null
    private val bindingBottomSheetEditProfileBinding
        get() = _bindingBottomSheetEditProfile as BottomSheetEditProfileBinding

    private val profileViewModel: ProfileViewModel by viewModels()

    private var currentUser: FirebaseUser? = null

    private lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            currentUser = user
            currentUser?.let {
                setupDisplayProfile(it)
            }
        }

        profileViewModel.updateProfileState.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { state ->
                when {
                    state.isError -> showSnackbar(
                        binding.root,
                        getString(R.string.text_message_failure_update_profile),
                        StatusSnackBar.DANGER
                    )
                    state.isSuccess -> showSnackbar(
                        binding.root,
                        getString(R.string.text_message_success_update_profile),
                        StatusSnackBar.SUCCESS
                    )
                }
            }
        }

        profileViewModel.changePasswordState.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { state ->
                when {
                    state.isError -> showSnackbar(
                        binding.root,
                        getString(R.string.password_failed_change),
                        StatusSnackBar.DANGER
                    )
                    state.isSuccess -> showSnackbar(
                        binding.root,
                        getString(R.string.password_successfully_change),
                        StatusSnackBar.SUCCESS
                    )
                }
            }
        }
        setupProfile()
    }

    private fun setupDisplayProfile(firebaseUser: FirebaseUser) {
        binding.apply {
            tvUsername.text = firebaseUser.displayName
            tvEmail.text = firebaseUser.email
            val avatar = if (firebaseUser.photoUrl != null) {
                firebaseUser.photoUrl
            } else {
                R.drawable.ic_avatar
            }
            ivAvatar.load(avatar)
        }
    }

    private fun setupProfile() {
        val iconArrow = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right_small)
        binding.itemEditProfile.tvAction.apply {
            val icon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit)
            text = getString(R.string.edit_profile)
            setCompoundDrawablesWithIntrinsicBounds(icon, null, iconArrow, null)

            setOnClickListener {
                setupBottomSheetEditProfile()
            }
        }

        binding.itemChangePassword.tvAction.apply {
            val icon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_lock_24)
            text = getString(R.string.change_password)
            setCompoundDrawablesWithIntrinsicBounds(icon, null, iconArrow, null)

            setOnClickListener {
                setupBottomSheetChangePassword()
            }
        }

        binding.itemLogout.tvAction.apply {
            val icon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_exit)
            text = getString(R.string.logout)
            setCompoundDrawablesWithIntrinsicBounds(icon, null, iconArrow, null)

            setOnClickListener {
                setupBottomSheetLogout()
            }
        }
    }

    private fun setupBottomSheetLogout() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.apply {
            if (_bindingBottomSheetLogout == null) {
                _bindingBottomSheetLogout = BottomSheetLogoutBinding.inflate(layoutInflater)
                setContentView(bindingBottomSheetLogout.root)
                show()
            }
        }

        dialog.setOnDismissListener {
            _bindingBottomSheetLogout = null
        }

        bindingBottomSheetLogout.apply {
            btnAccept.setOnClickListener {
                profileViewModel.logout()
                dialog.dismiss()
                act.navigateToLogin(findNavController())
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun setupBottomSheetChangePassword() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.apply {
            if (_bindingBottomSheetChangePassword == null) {
                _bindingBottomSheetChangePassword =
                    BottomSheetChangePasswordBinding.inflate(layoutInflater)
                setContentView(bindingBottomSheetChangePasswordBinding.root)
                show()
            }
        }

        bindingBottomSheetChangePasswordBinding.apply {
            btnAccept.isEnabled = false

            val passwordBefore = bindingBottomSheetChangePasswordBinding.edtPasswordBefore
            val passwordNow = bindingBottomSheetChangePasswordBinding.edtPasswordNow
            val passwordNowConfirm = bindingBottomSheetChangePasswordBinding.edtPasswordNowConfirm

            var passwordBeforeCorrect = false
            var passwordNowCorrect = false
            var passwordNowConfirmCorrect = false

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(editable: Editable) {
                    when {
                        editable === passwordBefore.editableText -> {
                            passwordBeforeCorrect = passwordBefore.text.toString().isNotEmpty()
                        }
                        editable === passwordNow.editableText -> {
                            passwordNowCorrect = passwordNow.text.toString().isNotEmpty()
                        }
                        editable === passwordNowConfirm.editableText -> {
                            passwordNowConfirmCorrect =
                                passwordNowConfirm.text.toString().isNotEmpty()
                        }
                    }
                    btnAccept.isEnabled =
                        passwordBeforeCorrect && passwordNowCorrect && passwordNowConfirmCorrect
                }
            }

            passwordBefore.addTextChangedListener(textWatcher)
            passwordNow.addTextChangedListener(textWatcher)
            passwordNowConfirm.addTextChangedListener(textWatcher)

            dialog.setOnDismissListener {
                _bindingBottomSheetChangePassword = null
            }

            btnAccept.setOnClickListener {
                if (validNewPassword(
                        passwordNow.text.toString(),
                        passwordNowConfirm.text.toString()
                    )
                ) {
                    profileViewModel.changeNewPassword(passwordNow.text.toString())
                    dialog.dismiss()
                } else {
                    showSnackbar(
                        bindingBottomSheetChangePasswordBinding.coordinator,
                        getString(R.string.password_invalid),
                        StatusSnackBar.WARNING
                    )
                }
            }
        }
    }

    private fun setupBottomSheetEditProfile() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.apply {
            if (_bindingBottomSheetEditProfile == null) {
                _bindingBottomSheetEditProfile =
                    BottomSheetEditProfileBinding.inflate(layoutInflater)
                setContentView(bindingBottomSheetEditProfileBinding.root)
                show()
            }
        }

        bindingBottomSheetEditProfileBinding.apply {

            var firstNameValue = ""
            var lastNameValue = ""

            val firstName = edtFirstName
            val lastName = edtLastName

            currentUser?.let { user ->
                ivAvatar.apply {
                    if (user.photoUrl != null) {
                        load(user.photoUrl)
                    } else {
                        load(R.drawable.ic_avatar)
                        setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary))
                    }
                }

                user.displayName?.let {
                    firstNameValue = splitFullName(it)[0]
                    lastNameValue = splitFullName(it)[1]

                    edtFirstName.setText(firstNameValue)
                    edtLastName.setText(lastNameValue)
                }
            }

            var firstNameCorrect = firstName.text.toString().isNotEmpty()
            var lastNameCorrect = lastName.text.toString().isNotEmpty()

            btnAccept.isEnabled = firstNameCorrect && lastNameCorrect

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(editable: Editable) {
                    when {
                        editable === firstName.editableText -> {
                            val firstNameText = firstName.text.toString()
                            if (firstNameText.isBlank()) {
                                layoutEdtFirstName.apply {
                                    error = getString(
                                        R.string.field_cant_empty,
                                        getString(R.string.nama_depan)
                                    )
                                    isErrorEnabled = true
                                }
                                firstNameCorrect = false
                            } else {
                                layoutEdtFirstName.apply {
                                    error = null
                                    isErrorEnabled = false
                                }
                                firstNameCorrect = true
                                firstNameValue = firstNameText
                            }
                        }
                        editable === lastName.editableText -> {
                            val lastNameText = lastName.text.toString()
                            if (lastNameText.isBlank()) {
                                layoutEdtLastName.apply {
                                    error = getString(
                                        R.string.field_cant_empty,
                                        getString(R.string.nama_belakang)
                                    )
                                    isErrorEnabled = false
                                }
                                lastNameCorrect = false
                            } else {
                                layoutEdtLastName.apply {
                                    error = null
                                    isErrorEnabled = false
                                }
                                lastNameCorrect = true
                                lastNameValue = lastNameText
                            }
                        }
                    }
                    btnAccept.isEnabled = firstNameCorrect && lastNameCorrect
                }
            }

            firstName.addTextChangedListener(textWatcher)
            lastName.addTextChangedListener(textWatcher)

            var avatarDrawable: Int
            var avatarUri: Uri? = null
            rgAvatar.setOnCheckedChangeListener { _, id ->
                avatarDrawable = when (id) {
                    R.id.btn_avatar_1 -> R.drawable.avatar_1
                    R.id.btn_avatar_2 -> R.drawable.avatar_2
                    R.id.btn_avatar_3 -> R.drawable.avatar_3
                    R.id.btn_avatar_4 -> R.drawable.avatar_4
                    R.id.btn_avatar_5 -> R.drawable.avatar_5
                    R.id.btn_avatar_6 -> R.drawable.avatar_6
                    else -> 0
                }
                ivAvatar.apply {
                    load(avatarDrawable)
                    colorFilter = null
                }
                avatarUri = getUriFromDrawable(avatarDrawable)
            }

            dialog.setOnDismissListener {
                _bindingBottomSheetEditProfile = null
            }

            btnAccept.setOnClickListener {
                profileViewModel.updateProfileUser(firstNameValue, lastNameValue, avatarUri)
                dialog.dismiss()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bindingBottomSheetEditProfile = null
        _bindingBottomSheetLogout = null
        _bindingBottomSheetChangePassword = null
    }
}