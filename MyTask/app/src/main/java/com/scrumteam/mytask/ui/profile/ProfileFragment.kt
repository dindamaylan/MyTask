package com.scrumteam.mytask.ui.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.R
import com.scrumteam.mytask.databinding.BottomSheetChangePasswordBinding
import com.scrumteam.mytask.databinding.BottomSheetEditProfileBinding
import com.scrumteam.mytask.databinding.BottomSheetLogoutBinding
import com.scrumteam.mytask.databinding.FragmentProfileBinding
import com.scrumteam.mytask.utils.StatusSnackBar
import com.scrumteam.mytask.utils.showSnackbar
import com.scrumteam.mytask.utils.splitFullName
import com.scrumteam.mytask.utils.validNewPassword
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

        profileViewModel.changePasswordState.observe(viewLifecycleOwner) { state ->
            when {
                state.isError -> showSnackbar(
                    binding.root, getString(R.string.password_failed_change), StatusSnackBar.DANGER
                )
                state.isLoading -> {}
                state.isSuccess -> showSnackbar(
                    binding.root,
                    getString(R.string.password_successfully_change),
                    StatusSnackBar.SUCCESS
                )
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
                R.drawable.avatar_1
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
        _bindingBottomSheetLogout = BottomSheetLogoutBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.apply {
            setContentView(bindingBottomSheetLogout.root)
            show()
        }

        bindingBottomSheetLogout.apply {
            btnAccept.setOnClickListener {
                profileViewModel.logout()
                dialog.cancel()
            }
            btnCancel.setOnClickListener {
                dialog.cancel()
            }
        }
    }

    private fun setupBottomSheetChangePassword() {
        _bindingBottomSheetChangePassword = BottomSheetChangePasswordBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.apply {
            setContentView(bindingBottomSheetChangePasswordBinding.root)
            show()
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


            btnAccept.setOnClickListener {
                if (validNewPassword(
                        passwordNow.text.toString(), passwordNowConfirm.text.toString()
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
        _bindingBottomSheetEditProfile = BottomSheetEditProfileBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.apply {
            setContentView(bindingBottomSheetEditProfileBinding.root)
            show()
        }

        bindingBottomSheetEditProfileBinding.apply {
            btnAccept.isEnabled = false

            currentUser?.let { user ->
                val avatar = if (user.photoUrl != null) user.photoUrl else R.drawable.avatar_1
                ivAvatar.load(avatar)

                user.displayName?.let {
//                    edtFirstName.setText(splitFullName(it)[0]?:"")
//                    edtLastName.setText(splitFullName(it)[1])
                }
            }


            rgAvatar.setOnCheckedChangeListener { _, id ->
                when (id) {
                    R.id.btn_avatar_1 -> ivAvatar.load(R.drawable.avatar_1)
                    R.id.btn_avatar_2 -> ivAvatar.load(R.drawable.avatar_2)
                    R.id.btn_avatar_3 -> ivAvatar.load(R.drawable.avatar_3)
                    R.id.btn_avatar_4 -> ivAvatar.load(R.drawable.avatar_4)
                    R.id.btn_avatar_5 -> ivAvatar.load(R.drawable.avatar_5)
                    R.id.btn_avatar_6 -> ivAvatar.load(R.drawable.avatar_6)

                }
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