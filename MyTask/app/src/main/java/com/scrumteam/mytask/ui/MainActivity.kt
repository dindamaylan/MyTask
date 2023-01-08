package com.scrumteam.mytask.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.scrumteam.mytask.MainNavGraphDirections
import com.scrumteam.mytask.R
import com.scrumteam.mytask.custom.components.LoadingDialog
import com.scrumteam.mytask.data.mapper.toInSecond
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.model.task.TaskCode
import com.scrumteam.mytask.databinding.ActivityMainBinding
import com.scrumteam.mytask.databinding.BottomSheetAddTaskBinding
import com.scrumteam.mytask.databinding.BottomSheetCheckedTaskBinding
import com.scrumteam.mytask.databinding.BottomSheetDeleteTaskBinding
import com.scrumteam.mytask.ui.auth.login.LoginViewModel
import com.scrumteam.mytask.ui.auth.register.RegisterViewModel
import com.scrumteam.mytask.ui.onboard.OnBoardActivity
import com.scrumteam.mytask.ui.onboard.OnBoardViewModel
import com.scrumteam.mytask.utils.*
import com.scrumteam.mytask.utils.Constants.DATE_FORMATTER
import com.scrumteam.mytask.utils.Constants.DATE_PICKER_TAG
import com.scrumteam.mytask.utils.Constants.TIME_FORMATTER
import com.scrumteam.mytask.utils.Constants.TIME_PICKER_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.TimeZone.getTimeZone

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var _bindingBottomSheetAddTask: BottomSheetAddTaskBinding? = null
    private val bindingBottomSheetAddTask get() = _bindingBottomSheetAddTask as BottomSheetAddTaskBinding

    private var _bindingBottomSheetDeleteTask: BottomSheetDeleteTaskBinding? = null
    private val bindingBottomSheetDeleteTaskBinding get() = _bindingBottomSheetDeleteTask as BottomSheetDeleteTaskBinding

    private var _bindingBottomSheetCheckedTask: BottomSheetCheckedTaskBinding? = null
    private val bindingBottomSheetCheckedTask get() = _bindingBottomSheetCheckedTask as BottomSheetCheckedTaskBinding

    private lateinit var navController: NavController

    private lateinit var loadingDialog: LoadingDialog

    private val onBoardViewModel: OnBoardViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            onBoardViewModel.isFirstRunOnBoard.value == null
        }

        onBoardViewModel.isFirstRunOnBoard.observe(this) { isFirst ->
            onBoardViewModel.updateIsFirstRunOnBoard(false)
            if (isFirst) {
                val intent = Intent(this, OnBoardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        loadingDialog = LoadingDialog(this)

        setupBottomNavigation(navController)

        loginViewModel.currentUser.observe(this) { currentUser ->
            if (currentUser != null) {
                navigateToHome(navController)
            } else {
                navigateToLogin(navController)
            }
        }

        loginViewModel.loginState.observe(this) { state ->
            when {
                state.isError -> {
                    loadingDialog.hideDialog()
                    Toast.makeText(
                        this, "Error", Toast.LENGTH_SHORT
                    ).show()
                }
                state.isLoading -> loadingDialog.showDialog()
                state.currentUser != null -> {
                    loadingDialog.hideDialog()
                    navigateToHome(navController)
                }
            }
        }

        registerViewModel.registerState.observe(this) { state ->
            when {
                state.isError -> {
                    loadingDialog.hideDialog()
                    Toast.makeText(
                        this, "Error", Toast.LENGTH_SHORT
                    ).show()
                }
                state.isLoading -> loadingDialog.showDialog()
                state.currentUser != null -> {
                    loadingDialog.hideDialog()
                    navigateToHome(navController)
                }
            }
        }

        mainViewModel.createTaskState.observe(this) {
            it.getContentIfNotHandled()?.let { state ->
                when {
                    state.isError -> showSnackbar(
                        binding.root,
                        getString((state.message as UiText.StringResource).id),
                        StatusSnackBar.DANGER
                    )
                    state.isSuccess -> showSnackbar(
                        binding.root,
                        getString((state.message as UiText.StringResource).id),
                        StatusSnackBar.SUCCESS
                    )
                }
            }
        }
    }

    private fun setupBottomNavigation(navController: NavController) {
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home_nav,
                R.id.work_nav,
                R.id.personal_nav,
                R.id.school_nav -> {
                    binding.fabTask.show()
                }

                R.id.calendar_nav,
                R.id.notification_nav,
                R.id.profile_nav,
                R.id.login_nav,
                R.id.register_nav -> binding.fabTask.hide()
            }

            if (destination.id != R.id.home_nav &&
                destination.id != R.id.calendar_nav &&
                destination.id != R.id.notification_nav &&
                destination.id != R.id.profile_nav
            ) {
                binding.apply {
                    fragmentContainerView.margin(bottom = 0f)
                    bottomNav.isVisible = false
                }
            } else {
                binding.apply {
                    fragmentContainerView.margin(bottom = 56f)
                    bottomNav.isVisible = true
                }
            }
        }
    }

    fun showBottomSheetTask(
        isUpdate: Boolean = false,
        destinationId: Int? = 0,
        taskId: String = "",
        userId: String = "",
        titleTaskNew: String = "",
        dateTaskNew: LocalDateTime? = null,
        timeTaskNew: LocalDateTime? = null,
    ) {
        binding.fabTask.setOnClickListener {
            setupBottomSheetAddTask(
                isUpdate, destinationId, taskId, userId, titleTaskNew, dateTaskNew, timeTaskNew
            )
        }
    }

    fun setupBottomSheetAddTask(
        isUpdate: Boolean,
        destinationId: Int? = 0,
        taskId: String = "",
        userId: String = "",
        titleTaskNew: String = "",
        dateTaskNew: LocalDateTime? = null,
        timeTaskNew: LocalDateTime? = null
    ) {
        val dialog = BottomSheetDialog(this)
        dialog.apply {
            if (_bindingBottomSheetAddTask == null) {
                _bindingBottomSheetAddTask = BottomSheetAddTaskBinding.inflate(layoutInflater)
                setContentView(bindingBottomSheetAddTask.root)
                show()
            }
        }

        bindingBottomSheetAddTask.btnCreateTask.isEnabled = isUpdate

        var titleTaskValue = titleTaskNew
        var taskType: TaskCode
        var dateTaskValue = dateTaskNew ?: LocalDateTime.now()
        var timeTaskValue = timeTaskNew ?: LocalDateTime.now()

        val titleTask = bindingBottomSheetAddTask.edtTitleTask
        val categoryTask = bindingBottomSheetAddTask.ddCategoryTask
        val dateTask = bindingBottomSheetAddTask.pickDateTask
        val timeTask = bindingBottomSheetAddTask.pickTimeTask

        val listCategoryTask = getTaskTypes(this)
        val listCategoryTaskAdapter = ArrayAdapter(this, R.layout.item_row_text, listCategoryTask)


        with(categoryTask) {
            val category = when (destinationId) {
                R.id.home_nav -> {
                    setAdapter(listCategoryTaskAdapter)
                    listCategoryTask.first()
                }
                R.id.personal_nav -> {
                    isEnabled = false
                    listCategoryTask.first()
                }
                R.id.work_nav -> {
                    isEnabled = false
                    listCategoryTask[1]
                }
                R.id.school_nav -> {
                    isEnabled = false
                    listCategoryTask[2]
                }
                else -> listCategoryTask.first()
            }
            title = category.toString()
            hint = category.toString()
            taskType = category.codeName
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                taskType = listCategoryTask[position].codeName
            }
        }

        val now = LocalDateTime.now()

        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        endDate.set(Calendar.YEAR, endDate.get(Calendar.YEAR) + 1)
        endDate.set(Calendar.DAY_OF_YEAR, endDate.getActualMaximum(Calendar.DAY_OF_YEAR))
        startDate.set(1500, 1, 1)

        val formatterDate: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER)
        val formatterTime: DateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMATTER)

        var dateDialog: MaterialDatePicker<Long>? = null
        var timeDialog: MaterialTimePicker? = null

        dateTask.setOnClickListener {
            if (dateDialog == null) {

                val constraints = CalendarConstraints.Builder().apply {
                    setStart(startDate.timeInMillis)
                    setEnd(endDate.timeInMillis)
                    setValidator(DateValidatorPointForward.now())
                }.build()

                dateDialog = MaterialDatePicker.Builder.datePicker().apply {
                    setTitleText(R.string.setting_date)
                    if (isUpdate) {
                        setSelection(
                            dateTaskValue.toInSecond()
                        )
                    } else {
                        setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    }
                    setCalendarConstraints(constraints)
                }.build()

                dateDialog?.addOnPositiveButtonClickListener {
                    val select = it
                    if (select != null) {
                        val date = Calendar.getInstance()
                        date.timeZone = getTimeZone("UTC")
                        date.timeInMillis = select
                        val year = date.get(Calendar.YEAR)
                        val month = date.get(Calendar.MONTH) + 1
                        val day = date.get(Calendar.DAY_OF_MONTH)
                        dateTaskValue = LocalDateTime.of(year, month, day, 0, 0, 0)

                        dateTask.setText(formatterDate.format(dateTaskValue))
                    }
                }
                dateDialog?.show(supportFragmentManager, DATE_PICKER_TAG)
                lifecycleScope.launch {
                    delay(750)
                    dateDialog = null
                }
            }
        }

        timeTask.setOnClickListener {
            if (timeDialog == null) {
                val isSystem24Hour = DateFormat.is24HourFormat(this)
                val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

                timeDialog = MaterialTimePicker.Builder().setTimeFormat(clockFormat)
                    .setHour(timeTaskValue?.hour ?: now.hour)
                    .setMinute(timeTaskValue?.minute ?: now.minute)
                    .setTitleText(getString(R.string.setting_time)).build()

                timeDialog?.addOnPositiveButtonClickListener {
                    val pickHour = timeDialog?.hour
                    val pickMinute = timeDialog?.minute
                    if (pickHour != null && pickMinute != null) {
                        timeTaskValue = LocalDateTime.of(
                            dateTaskValue.toLocalDate(),
                            LocalTime.of(pickHour, pickMinute, 0)
                        )

                        timeTask.setText(formatterTime.format(timeTaskValue))
                    }
                }

                timeDialog?.addOnDismissListener {
                    timeDialog = null
                }

                timeDialog?.show(supportFragmentManager, TIME_PICKER_TAG)
            }
        }

        var titleTaskCorrect = false
        var dateTaskCorrect = false
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                when {
                    editable === titleTask.editableText -> {
                        val titleTaskText = titleTask.text.toString()
                        if (titleTaskText.isBlank()) {
                            bindingBottomSheetAddTask.layoutEdtTitleTask.apply {
                                error = "error"
                                isErrorEnabled = true
                            }
                            titleTaskCorrect = false
                        } else {
                            titleTaskValue = titleTaskText
                            bindingBottomSheetAddTask.layoutEdtTitleTask.apply {
                                error = null
                                isErrorEnabled = false
                            }
                            titleTaskCorrect = true
                        }
                    }
                    editable === dateTask.editableText -> dateTaskCorrect =
                        dateTask.text.toString().isNotEmpty()
                }
                bindingBottomSheetAddTask.btnCreateTask.isEnabled =
                    titleTaskCorrect && dateTaskCorrect
            }
        }

        titleTask.addTextChangedListener(textWatcher)
        dateTask.addTextChangedListener(textWatcher)

        if (isUpdate) {
            titleTask.setText(titleTaskNew)
            dateTask.setText(dateTaskNew?.format(formatterDate))
            timeTaskNew?.let {
                timeTask.setText(formatterTime.format(it).toString())
            }
        }

        dialog.setOnDismissListener {
            _bindingBottomSheetAddTask = null
        }

        bindingBottomSheetAddTask.btnCreateTask.setOnClickListener {
            val task = Task(
                id = taskId,
                userId = userId,
                titleTaskValue,
                category = taskType.name,
                date = dateTaskValue.toInSecond(),
                time = timeTaskValue.toInSecond(),
                isChecked = false
            )
            if (isUpdate) {
                mainViewModel.updateTask(task)
            } else {
                mainViewModel.insertTask(task)
            }
            dialog.dismiss()
        }
    }

    fun setupBottomSheetDeleteTask(action: (() -> Unit)) {
        val dialog = BottomSheetDialog(this)
        dialog.apply {
            if (_bindingBottomSheetDeleteTask == null) {
                _bindingBottomSheetDeleteTask = BottomSheetDeleteTaskBinding.inflate(layoutInflater)
                setContentView(bindingBottomSheetDeleteTaskBinding.root)
                show()
            }
        }

        dialog.setOnDismissListener {
            _bindingBottomSheetDeleteTask = null
        }

        bindingBottomSheetDeleteTaskBinding.apply {
            btnAccept.setOnClickListener {
                action()
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

    }

    fun setupBottomSheetCheckedTask(action: (() -> Unit)) {
        val dialog = BottomSheetDialog(this)
        dialog.apply {
            if (_bindingBottomSheetCheckedTask == null) {
                _bindingBottomSheetCheckedTask =
                    BottomSheetCheckedTaskBinding.inflate(layoutInflater)
                setContentView(bindingBottomSheetCheckedTask.root)
                show()
            }
        }

        dialog.setOnDismissListener {
            _bindingBottomSheetCheckedTask = null
        }

        bindingBottomSheetCheckedTask.apply {
            btnAccept.setOnClickListener {
                action()
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun navigateToLogin(navController: NavController) {
        val direction = MainNavGraphDirections.actionToLoginNav()
        navController.navigate(direction)
    }

    private fun navigateToHome(navController: NavController) {
        val navOptions =
            NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).setLaunchSingleTop(true)
                .build()
        navController.navigate(R.id.home_nav, null, navOptions)
    }

}