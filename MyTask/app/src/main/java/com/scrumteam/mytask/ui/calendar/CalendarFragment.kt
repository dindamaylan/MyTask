package com.scrumteam.mytask.ui.calendar

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import com.scrumteam.mytask.R
import com.scrumteam.mytask.adapter.ListTaskAdapter
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.databinding.FragmentCalendarBinding
import com.scrumteam.mytask.databinding.ItemRowCalendarDayBinding
import com.scrumteam.mytask.utils.displayText
import com.scrumteam.mytask.utils.setTextColorRes
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding as FragmentCalendarBinding

    private val monthCalendarView: CalendarView get() = binding.recyclerCalendar
    private val weekCalendarView: WeekCalendarView get() = binding.recyclerWeekCalendar

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val calendarViewModel: CalendarViewModel by viewModels()

    private lateinit var tasks: Map<LocalDate, List<Task>>

    private lateinit var listTaskAdapter: ListTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listTaskAdapter = ListTaskAdapter(
            onActionItem = { _, _ ->
            },
            onCompleteItem = {}
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarViewModel.tasksState.observe(viewLifecycleOwner) { state ->
            if (!state.isError) {
                tasks = state.tasks
            }
        }

        val daysOfWeek = daysOfWeek()
        binding.layoutLegend.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText()
                textView.setTextColorRes(R.color.black)
            }


        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)

        setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
        setupWeekCalendar(startMonth, endMonth, currentMonth, daysOfWeek)

        monthCalendarView.isInvisible = binding.calendarMode.isChecked
        weekCalendarView.isInvisible = !binding.calendarMode.isChecked

        binding.calendarMode.setOnCheckedChangeListener(weekModeToggled)

        binding.btnArrowBefore.setOnClickListener {
            monthCalendarView.findFirstVisibleMonth()?.let {
                monthCalendarView.scrollToMonth(it.yearMonth.previousMonth)
            }
        }

        binding.btnArrowAfter.setOnClickListener {
            monthCalendarView.findFirstVisibleMonth()?.let {
                monthCalendarView.scrollToMonth(it.yearMonth.nextMonth)
            }
        }

        setupRecyclerTask()
    }

    private fun setupRecyclerTask(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTask.apply {
            layoutManager = linearLayoutManager
            adapter = listTaskAdapter
        }
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = ItemRowCalendarDayBinding.bind(view).tvOneDay

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate == day.date) {
                            selectedDate = null
                            monthCalendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            monthCalendarView.notifyDateChanged(day.date)
                            oldDate?.let { monthCalendarView.notifyDateChanged(oldDate) }
                            listTaskAdapter.submitList(tasks[day.date].orEmpty())
                        }
                    }
                }
            }
        }
        monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
            }
        }
        monthCalendarView.monthScrollListener = { updateTitle() }
        monthCalendarView.setup(startMonth, endMonth, daysOfWeek.first())
        monthCalendarView.scrollToMonth(currentMonth)
    }

    private fun setupWeekCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class WeekDayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: WeekDay
            val textView = ItemRowCalendarDayBinding.bind(view).tvOneDay

            init {
                view.setOnClickListener {
                    if (day.position == WeekDayPosition.RangeDate) {
                        if (selectedDate == day.date) {
                            selectedDate = null
                            weekCalendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            weekCalendarView.notifyDateChanged(day.date)
                            oldDate?.let { weekCalendarView.notifyDateChanged(oldDate) }
                            listTaskAdapter.submitList(tasks[day.date].orEmpty())
                        }
                    }
                }
            }
        }
        weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == WeekDayPosition.RangeDate)
            }
        }
        weekCalendarView.weekScrollListener = { updateTitle() }
        weekCalendarView.setup(
            startMonth.atStartOfMonth(),
            endMonth.atEndOfMonth(),
            daysOfWeek.first(),
        )
        weekCalendarView.scrollToWeek(currentMonth.atStartOfMonth())
    }


    private fun updateTitle() {
        val isMonthMode = !binding.calendarMode.isChecked
        if (isMonthMode) {
            val month = monthCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
            binding.tvMonthYear.text = getString(
                R.string.month_year,
                month.month.displayText(short = false),
                month.year.toString()
            )
        } else {
            val week = weekCalendarView.findFirstVisibleWeek() ?: return
            val firstDate = week.days.first().date
            val lastDate = week.days.last().date
            if (firstDate.yearMonth == lastDate.yearMonth) {
                binding.tvMonthYear.text = getString(
                    R.string.month_year,
                    firstDate.month.displayText(short = false),
                    firstDate.year.toString()
                )
            } else {
                val year = if (firstDate.year == lastDate.year) {
                    firstDate.year.toString()
                } else {
                    "${firstDate.year} - ${lastDate.year}"
                }
                binding.tvMonthYear.text = getString(
                    R.string.double_month_year,
                    firstDate.month.displayText(short = false),
                    lastDate.month.displayText(short = false),
                    year
                )
            }
        }
    }

    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
        textView.text = date.dayOfMonth.toString()
        if (isSelectable) {
            when(date) {
                selectedDate -> {
                    textView.setTextColorRes(R.color.white)
                    textView.setBackgroundResource(R.drawable.shape_bg_today)
                }
                today -> {
                    textView.setTextColorRes(R.color.success)
                    textView.background = null
                }
                else -> {
                    textView.setTextColorRes(R.color.black)
                    textView.background = null
                }
            }
        } else {
            textView.setTextColorRes(R.color.placeholder)
            textView.background = null
        }
    }

    private val weekModeToggled = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, monthToWeek: Boolean) {
            // We want the first visible day to remain visible after the
            // change so we scroll to the position on the target calendar.
            if (monthToWeek) {
                val targetDate = monthCalendarView.findFirstVisibleDay()?.date ?: return
                weekCalendarView.scrollToWeek(targetDate)
            } else {
                // It is possible to have two months in the visible week (30 | 31 | 1 | 2 | 3 | 4 | 5)
                // We always choose the second one. Please use what works best for your use case.
                val targetMonth = weekCalendarView.findLastVisibleDay()?.date?.yearMonth ?: return
                monthCalendarView.scrollToMonth(targetMonth)
            }

            val weekHeight = weekCalendarView.height
            // If OutDateStyle is EndOfGrid, you could simply multiply weekHeight by 6.
            val visibleMonthHeight = weekHeight *
                    monthCalendarView.findFirstVisibleMonth()?.weekDays.orEmpty().count()

            val oldHeight = if (monthToWeek) visibleMonthHeight else weekHeight
            val newHeight = if (monthToWeek) weekHeight else visibleMonthHeight

            // Animate calendar height changes.
            val animator = ValueAnimator.ofInt(oldHeight, newHeight)
            animator.addUpdateListener { anim ->
                monthCalendarView.updateLayoutParams {
                    height = anim.animatedValue as Int
                }
                // A bug is causing the month calendar to not redraw its children
                // with the updated height during animation, this is a workaround.
                monthCalendarView.children.forEach { child ->
                    child.requestLayout()
                }
            }

            animator.doOnStart {
                if (!monthToWeek) {
                    weekCalendarView.isInvisible = true
                    monthCalendarView.isVisible = true
                }
            }
            animator.doOnEnd {
                if (monthToWeek) {
                    weekCalendarView.isVisible = true
                    monthCalendarView.isInvisible = true
                } else {
                    // Allow the month calendar to be able to expand to 6-week months
                    // in case we animated using the height of a visible 5-week month.
                    // Not needed if OutDateStyle is EndOfGrid.
                    monthCalendarView.updateLayoutParams {
                        height =
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                updateTitle()
            }
            animator.duration = 250
            animator.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}