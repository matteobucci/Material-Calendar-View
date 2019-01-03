package com.applandeo.materialcalendarview.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.applandeo.materialcalendarview.R
import com.applandeo.materialcalendarview.extensions.CalendarGridView
import com.applandeo.materialcalendarview.listeners.DayRowClickListener
import com.applandeo.materialcalendarview.utils.CalendarProperties
import com.applandeo.materialcalendarview.utils.SelectedDay

import java.util.ArrayList
import java.util.Calendar
import java.util.Date

import com.applandeo.materialcalendarview.utils.CalendarProperties.CALENDAR_SIZE

/**
 * This class is responsible for loading a calendar page content.
 *
 *
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

class CalendarPageAdapter(private val mContext: Context, private val mCalendarProperties: CalendarProperties) : PagerAdapter() {

    public interface MonthLoadedListener{
        fun onMonthLoaded(monthNumber: Int, faceViews: Array<ImageView>, lineViews: Array<View>)
    }

    val viewMap: MutableMap<Int, Pair<Array<ImageView>, Array<View>>> = mutableMapOf()

    val TAG = CalendarPageAdapter::class.java.simpleName

    private var callback: MonthLoadedListener? = null

    private var mCalendarGridView: CalendarGridView? = null

    private var mPageMonth: Int = 0

    private lateinit var faceViews: Array<ImageView>
    private lateinit var lineViews: Array<View>


    val selectedDays: List<SelectedDay>
        get() = mCalendarProperties.selectedDays

    var selectedDay: SelectedDay
        get() = mCalendarProperties.selectedDays[0]
        set(selectedDay) {
            mCalendarProperties.setSelectedDay(selectedDay)
            informDatePicker()
        }


    init {
        informDatePicker()
    }

    override fun getCount(): Int {
        return CALENDAR_SIZE
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.i(TAG, "Created position ${position}")

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.calendar_view_turns, null)

        mCalendarGridView = v.findViewById(R.id.calendarGridView)

        val ivWeek1 = v.findViewById<ImageView>(R.id.imageView1)
        val ivWeek2 = v.findViewById<ImageView>(R.id.imageView2)
        val ivWeek3 = v.findViewById<ImageView>(R.id.imageView3)
        val ivWeek4 = v.findViewById<ImageView>(R.id.imageView4)
        val ivWeek5 = v.findViewById<ImageView>(R.id.imageView5)
        val ivWeek6 = v.findViewById<ImageView>(R.id.imageView6)

        faceViews = arrayOf(ivWeek1, ivWeek2, ivWeek3, ivWeek4, ivWeek5, ivWeek6)

        val viewLine1 = v.findViewById<View>(R.id.view1)
        val viewLine2 = v.findViewById<View>(R.id.view2)
        val viewLine3 = v.findViewById<View>(R.id.view3)
        val viewLine4 = v.findViewById<View>(R.id.view4)
        val viewLine5 = v.findViewById<View>(R.id.view5)
        val viewLine6 = v.findViewById<View>(R.id.view6)

        lineViews = arrayOf(viewLine1, viewLine2, viewLine3, viewLine4, viewLine5, viewLine6)

        loadMonth(position)

        viewMap.put(position, Pair(faceViews, lineViews))
        callback?.onMonthLoaded(position, faceViews, lineViews)

        mCalendarGridView!!.onItemClickListener = DayRowClickListener(this,
                mCalendarProperties, mPageMonth)

        container.addView(v)
        return v
    }

    fun addSelectedDay(selectedDay: SelectedDay) {
        if (!mCalendarProperties.selectedDays.contains(selectedDay)) {
            mCalendarProperties.selectedDays.add(selectedDay)
            informDatePicker()
            return
        }

        mCalendarProperties.selectedDays.remove(selectedDay)
        informDatePicker()
    }

    /**
     * This method inform DatePicker about ability to return selected days
     */
    private fun informDatePicker() {
        if (mCalendarProperties.onSelectionAbilityListener != null) {
            mCalendarProperties.onSelectionAbilityListener.onChange(mCalendarProperties.selectedDays.size > 0)
        }
    }

    /**
     * This method fill calendar GridView with days
     *
     * @param position Position of current page in ViewPager
     */
    private fun loadMonth(position: Int) {
        val days = ArrayList<Date>()

        // Get Calendar object instance
        val calendar = mCalendarProperties.firstPageCalendarDate.clone() as Calendar

        // Add months to Calendar (a number of months depends on ViewPager position)
        calendar.add(Calendar.MONTH, position)

        // Set day of month as 1
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Get a number of the first day of the week
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Count when month is beginning
        val firstDayOfWeek = calendar.firstDayOfWeek
        val monthBeginningCell = (if (dayOfWeek < firstDayOfWeek) 7 else 0) + dayOfWeek - firstDayOfWeek

        // Subtract a number of beginning days, it will let to load a part of a previous month
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        /*
        Get all days of one page (42 is a number of all possible cells in one page
        (a part of previous month, current month and a part of next month))
         */
        while (days.size < 42) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        mPageMonth = calendar.get(Calendar.MONTH) - 1
        val calendarDayAdapter = CalendarDayAdapter(this, mContext,
                mCalendarProperties, days, mPageMonth)

        mCalendarGridView!!.adapter = calendarDayAdapter
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        Log.i(TAG, "Destroyed position ${position}")
        viewMap.remove(position)
        container.removeView(`object` as View)
    }

    fun setCallback(callback: MonthLoadedListener?){
        this.callback = callback
    }

    fun redrawMonths(){
        for (key in viewMap.keys) {
            callback?.onMonthLoaded(key, viewMap[key]!!.first, viewMap[key]!!.second)
        }
    }

}
