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
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.model.task.TaskCode
import com.scrumteam.mytask.databinding.ActivityMainBinding
import com.scrumteam.mytask.databinding.BottomSheetAddTaskBinding
import com.scrumteam.mytask.ui.auth.login.LoginViewModel
import com.scrumteam.mytask.ui.auth.register.RegisterViewModel
import com.scrumteam.mytask.ui.onboard.OnBoardActivity
import com.scrumteam.mytask.ui.onboard.OnBoardViewModel
import com.scrumteam.mytask.utils.Constants.DATE_PICKER_TAG
import com.scrumteam.mytask.utils.Constants.TIME_PICKER_TAG
import com.scrumteam.mytask.utils.StatusSnackBar
import com.scrumteam.mytask.utils.getTaskTypes
import com.scrumteam.mytask.utils.margin
import com.scrumteam.mytask.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var _bindingBottomSheetAddTask: BottomSheetAddTaskBinding? = null
    private val bindingBottomSheetAddTask get() = _bindingBottomSheetAddTask as BottomSheetAddTaskBinding

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
                        getString(R.string.text_message_failure_create_task),
                        StatusSnackBar.DANGER
                    )
                    state.isSuccess -> showSnackbar(
                        binding.root,
                        getString(R.string.text_message_success_create_task),
                        StatusSnackBar.SUCCESS
                    )
                }
            }
        }

        binding.fabTask.setOnClickListener {
            setupBottomSheetAddTask()
        }
    }

    private fun setupBottomNavigation(navController: NavController) {
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home_nav,
                R.id.work_nav,
                R.id.personal_nav,
                R.id.school_nav -> binding.fabTask.show()

                R.id.calendar_nav,
                R.id.notification_nav,
                R.id.profile_nav -> binding.fabTask.hide()
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

    private fun setupBottomSheetAddTask() {
        _bindingBottomSheetAddTask = BottomSheetAddTaskBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.apply {
            setContentView(bindingBottomSheetAddTask.root)
            show()
        }

        bindingBottomSheetAddTask.btnCreateTask.isEnabled = false

        var titleTaskValue = ""
        var taskType = TaskCode.PERSONAL
        var dateTaskValue: LocalDate = LocalDate.of(1970, 1, 1)
        var timeTaskValue: LocalTime? = null

        val titleTask = bindingBottomSheetAddTask.edtTitleTask
        val categoryTask = bindingBottomSheetAddTask.ddCategoryTask
        val dateTask = bindingBottomSheetAddTask.pickDateTask
        val timeTask = bindingBottomSheetAddTask.pickTimeTask

        val listCategoryTask = getTaskTypes(this)
        val listCategoryTaskAdapter = ArrayAdapter(this, R.layout.item_row_text, listCategoryTask)

        with(categoryTask) {
            setAdapter(listCategoryTaskAdapter)
            title = listCategoryTask.first().toString()
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
        val formatterDate: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val formatterTime: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

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
                    setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    setCalendarConstraints(constraints)
                }.build()

                dateDialog?.addOnPositiveButtonClickListener {
                    val select = it
                    if (select != null) {
                        val date = Calendar.getInstance()
                        date.timeZone = TimeZone.getTimeZone("UTC")
                        date.timeInMillis = select
                        val year = date.get(Calendar.YEAR)
                        val month = date.get(Calendar.MONTH) + 1
                        val day = date.get(Calendar.DAY_OF_MONTH)
                        dateTaskValue = LocalDate.of(year, month, day)
                        val todayDate = LocalDate.now()

                        while (dateTaskValue.isAfter(todayDate)) {
                            dateTaskValue = LocalDate.of(
                                todayDate.year - 1,
                                dateTaskValue.monthValue,
                                dateTaskValue.dayOfMonth
                            )
                        }

                        dateTask.setText(dateTaskValue.format(formatterDate))
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

                timeDialog = MaterialTimePicker.Builder()
                    .setTimeFormat(clockFormat)
                    .setHour(now.hour)
                    .setMinute(now.minute)
                    .setTitleText(getString(R.string.setting_time))
                    .build()

                timeDialog?.addOnPositiveButtonClickListener {
                    val pickHour = timeDialog?.hour
                    val pickMinute = timeDialog?.minute
                    if (pickHour != null && pickMinute != null) {
                        timeTaskValue = LocalTime.of(pickHour, pickMinute)

                        timeTask.setText(formatterTime.format(timeTaskValue).toString())
                    }
                }

                timeDialog?.show(supportFragmentManager, TIME_PICKER_TAG)
//                lifecycleScope.launch {
//                    delay(750)
//                    timeDialog = null
//                }
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
                    editable === dateTask.editableText -> dateTaskCorrect = true
                }
                bindingBottomSheetAddTask.btnCreateTask.isEnabled =
                    titleTaskCorrect && dateTaskCorrect
            }
        }

        titleTask.addTextChangedListener(textWatcher)
        dateTask.addTextChangedListener(textWatcher)

        bindingBottomSheetAddTask.btnCreateTask.setOnClickListener {
            val task = Task(
                id = "",
                userId = "",
                titleTaskValue,
                category = taskType.name,
                date = dateTaskValue.toString(),
                time = timeTaskValue.toString(),
                isCheck = false
            )
            mainViewModel.insertTask(task)
            dialog.dismiss()
        }
    }

    fun navigateToLogin(navController: NavController) {
        val direction = MainNavGraphDirections.actionToLoginNav()
        navController.navigate(direction)
    }

    private fun navigateToHome(navController: NavController) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.main_nav_graph, true)
            .setLaunchSingleTop(true)
            .build()
        navController.navigate(R.id.home_nav, null, navOptions)
    }

    override fun onDestroy() {
        super.onDestroy()
        _bindingBottomSheetAddTask = null
    }
}