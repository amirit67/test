package ir.payebash.utils.toggleSwitch

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ir.payebash.R
import java.util.*

abstract class BaseToggleSwitch : LinearLayout, ToggleSwitchButton.Listener {


    /*
       Default Values
     */

    companion object Default {

        @JvmStatic private val BORDER_RADIUS_DP            = 4
        @JvmStatic private val BORDER_WIDTH                = 4

        @JvmStatic @ColorRes private val CHECKED_BACKGROUND_COLOR    = R.color.blue
        @JvmStatic @ColorRes private val CHECKED_BORDER_COLOR        = R.color.black
        @JvmStatic @ColorRes private val CHECKED_TEXT_COLOR          = android.R.color.white

        @JvmStatic private val EMPTY_TOGGLE_DECORATOR      = object: ToggleSwitchButton.ToggleSwitchButtonDecorator {
            override fun decorate(toggleSwitchButton: ToggleSwitchButton, view: View, position: Int) {}
        }

        @JvmStatic private val ENABLED                     = true

        @JvmStatic private val LAYOUT_ID                   = R.layout.toggle_switch_button_view
        @JvmStatic private val LAYOUT_HEIGHT               = LinearLayout.LayoutParams.WRAP_CONTENT
        @JvmStatic private val LAYOUT_WIDTH                = LinearLayout.LayoutParams.WRAP_CONTENT

        @JvmStatic private val NUM_ENTRIES                 = 0

        @JvmStatic @ColorRes private val SEPARATOR_COLOR             = R.color.gray_very_light
        @JvmStatic private val SEPARATOR_VISIBLE           = true

        @JvmStatic private val TEXT_SIZE                   = 16f

        @JvmStatic private val TOGGLE_DISTANCE             = 0f
        @JvmStatic private val TOGGLE_ELEVATION            = 0f
        @JvmStatic private val TOGGLE_HEIGHT               = 38f
        @JvmStatic private val TOGGLE_WIDTH                = 72f

        @JvmStatic @ColorRes private val UNCHECKED_BACKGROUND_COLOR  = R.color.gray_light
        @JvmStatic @ColorRes private val UNCHECKED_BORDER_COLOR      = R.color.black
        @JvmStatic @ColorRes private val UNCHECKED_TEXT_COLOR        = R.color.gray
    }




    /*
       Instance Variables
     */


    var checkedBackgroundColor:      Int
    var checkedBorderColor:          Int
    var checkedTextColor:            Int

    var borderRadius:               Float
    var borderWidth:                Float

    var uncheckedBackgroundColor:    Int
    var uncheckedBorderColor:        Int
    var uncheckedTextColor:          Int

    var separatorColor:             Int
    var separatorVisible = SEPARATOR_VISIBLE

    var textSize:                   Float

    var toggleElevation = TOGGLE_ELEVATION
    var toggleMargin:               Float
    var toggleHeight:               Float
    var toggleWidth:                Float

    var layoutHeight = LAYOUT_HEIGHT
    var layoutWidth = LAYOUT_WIDTH

    var layoutId = LAYOUT_ID
    var numEntries = NUM_ENTRIES

    var prepareDecorator: ToggleSwitchButton.ToggleSwitchButtonDecorator = EMPTY_TOGGLE_DECORATOR
    var checkedDecorator:    ToggleSwitchButton.ViewDecorator?    = null
    var uncheckedDecorator:  ToggleSwitchButton.ViewDecorator?    = null

    var buttons = ArrayList<ToggleSwitchButton>()

    /*
       Constructors
     */

    constructor(context: Context) : super(context) {

        // Setup View
        setUpView()

        // Set default params
        checkedBackgroundColor       = ContextCompat.getColor(context, CHECKED_BACKGROUND_COLOR)
        checkedBorderColor           = ContextCompat.getColor(context, CHECKED_BORDER_COLOR)
        checkedTextColor             = ContextCompat.getColor(context, CHECKED_TEXT_COLOR)

        borderRadius                = dp2px(context, BORDER_RADIUS_DP.toFloat())
        borderWidth                 = dp2px(context, BORDER_WIDTH.toFloat())

        uncheckedBackgroundColor     = ContextCompat.getColor(context, UNCHECKED_BACKGROUND_COLOR)
        uncheckedBorderColor         = ContextCompat.getColor(context, UNCHECKED_BORDER_COLOR)
        uncheckedTextColor           = ContextCompat.getColor(context, UNCHECKED_TEXT_COLOR)

        separatorColor              = ContextCompat.getColor(context, SEPARATOR_COLOR)

        textSize                    = dp2px(context, TEXT_SIZE)

        toggleMargin                = dp2px(getContext(), TOGGLE_DISTANCE)
        toggleHeight                = dp2px(getContext(), TOGGLE_HEIGHT)
        toggleWidth                 = dp2px(getContext(), TOGGLE_WIDTH)
    }

    constructor(context: Context, attrs: AttributeSet?) : super (context, attrs) {

        if (attrs != null) {
            setUpView()

            val attributes = context.obtainStyledAttributes(attrs, R.styleable.BaseToggleSwitch, 0, 0)

            try {

                checkedBackgroundColor = attributes.getColor(
                        R.styleable.BaseToggleSwitch_checkedBackgroundColor,
                        getStyleColor(context, R.attr.colorPrimary, CHECKED_BACKGROUND_COLOR))

                checkedBorderColor = attributes.getColor(
                        R.styleable.BaseToggleSwitch_checkedBorderColor,
                        getStyleColor(context, R.attr.colorPrimary, CHECKED_BORDER_COLOR))

                checkedTextColor = attributes.getColor(
                        R.styleable.BaseToggleSwitch_checkedTextColor,
                        getStyleColor(context, android.R.attr.textColorPrimary, CHECKED_TEXT_COLOR))

                borderRadius = attributes.getDimensionPixelSize(
                        R.styleable.BaseToggleSwitch_borderRadius,
                        dp2px(context, BORDER_RADIUS_DP.toFloat()).toInt()).toFloat()

                borderWidth = attributes.getDimensionPixelSize(
                        R.styleable.BaseToggleSwitch_borderWidth,
                        dp2px(context, BORDER_WIDTH.toFloat()).toInt()).toFloat()

                isEnabled = attributes.getBoolean(
                        R.styleable.BaseToggleSwitch_android_enabled,
                        ENABLED)

                uncheckedBackgroundColor = attributes.getColor(
                        R.styleable.BaseToggleSwitch_uncheckedBackgroundColor,
                        getStyleColor(context, R.attr.colorButtonNormal, UNCHECKED_BACKGROUND_COLOR))

                uncheckedBorderColor = attributes.getColor(
                        R.styleable.BaseToggleSwitch_uncheckedBorderColor,
                        getStyleColor(context, R.attr.colorButtonNormal, UNCHECKED_BORDER_COLOR))

                uncheckedTextColor = attributes.getColor(
                        R.styleable.BaseToggleSwitch_uncheckedTextColor,
                        getStyleColor(context, android.R.attr.textColorPrimary, UNCHECKED_TEXT_COLOR))

                separatorColor = attributes.getColor(
                        R.styleable.BaseToggleSwitch_separatorColor,
                        getStyleColor(context, android.R.attr.textColorSecondary, SEPARATOR_COLOR))

                separatorVisible = attributes.getBoolean(
                        R.styleable.BaseToggleSwitch_separatorVisible,
                        SEPARATOR_VISIBLE)

                textSize = attributes.getDimensionPixelSize(
                        R.styleable.BaseToggleSwitch_android_textSize,
                        dp2px(context, TEXT_SIZE).toInt()).toFloat()

                toggleElevation = attributes.getDimensionPixelSize(
                        R.styleable.BaseToggleSwitch_elevation,
                        dp2px(context, TOGGLE_ELEVATION).toInt()).toFloat()

                toggleMargin = attributes.getDimension(
                        R.styleable.BaseToggleSwitch_toggleMargin,
                        dp2px(getContext(), TOGGLE_DISTANCE))

                toggleHeight = attributes.getDimension(
                        R.styleable.BaseToggleSwitch_toggleHeight,
                        dp2px(getContext(), TOGGLE_HEIGHT))

                toggleWidth = attributes.getDimension(
                        R.styleable.BaseToggleSwitch_toggleWidth,
                        dp2px(getContext(), TOGGLE_WIDTH))

                layoutHeight = attributes.getLayoutDimension(
                        R.styleable.BaseToggleSwitch_android_layout_height,
                        LAYOUT_HEIGHT)

                layoutWidth = attributes.getLayoutDimension(
                        R.styleable.BaseToggleSwitch_android_layout_width,
                        LAYOUT_WIDTH)

                val entries         = attributes.getTextArray(R.styleable.BaseToggleSwitch_android_entries)
                if (entries == null || entries.isEmpty()) {

                    val entriesList = ArrayList<String>()

                    val textToggleLeft  = attributes.getString(R.styleable.BaseToggleSwitch_textToggleLeft)
                    val textToggleRight = attributes.getString(R.styleable.BaseToggleSwitch_textToggleRight)

                    if (!TextUtils.isEmpty(textToggleLeft) && !TextUtils.isEmpty(textToggleRight)) {
                        entriesList.add(textToggleLeft)
                        val textToggleCenter  = attributes.getString(R.styleable.BaseToggleSwitch_textToggleCenter)
                        if (!TextUtils.isEmpty(textToggleCenter)) {
                            entriesList.add(textToggleCenter)
                        }
                        entriesList.add(textToggleRight)
                        setEntries(entriesList)
                    }
                }
                else {
                    setEntries(entries)
                }
            }
            finally {
                attributes.recycle()
            }
        }
        else {
            throw RuntimeException("AttributeSet is null!")
        }
    }


    /*
        Overrode instance methods
    */

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        for (button in buttons) {
            if (!isFullWidth()) {
                button.layoutParams.width = toggleWidth.toInt()
            }

            if (!isFullHeight()) {
                button.layoutParams.height = toggleHeight.toInt()
            }
        }
    }

    final override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else .6f
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    final override fun setElevation(elevation: Float) {
        super.setElevation(elevation)
        if (elevation > 0) {
            clipToPadding = false
            setPadding(elevation.toInt(), elevation.toInt(), elevation.toInt(), elevation.toInt())
        }
        else {
            clipToPadding = true
            setPadding(0, 0, 0, 0)
        }
        for (button in buttons) {
            ViewCompat.setElevation(button, elevation)
        }
    }


    /*
        Public instance methods
    */

    fun setEntries(stringArrayId: Int) {
        setEntries(resources.getStringArray(stringArrayId))
    }

    fun setEntries(entries: Array<String>) {
        setEntries(entries.toList())
    }

    fun setEntries(entries : Array<CharSequence>) {
        val entriesList = ArrayList<String>()
        for (entry in entries) {
            entriesList.add(entry.toString())
        }
        setEntries(entriesList)
    }

    fun setEntries(entries : List<String>) {

        val prepareDecorator = object: ToggleSwitchButton.ToggleSwitchButtonDecorator {
            override fun decorate(toggleSwitchButton: ToggleSwitchButton, view: View, position: Int) {
                val textView = view.findViewById(R.id.text_view) as TextView
                textView.text = entries[position]
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
        }

        val checkedDecorator = object: ToggleSwitchButton.ViewDecorator {
            override fun decorate(view: View, position: Int) {
                val textView = view.findViewById(R.id.text_view) as TextView
                textView.setTextColor(checkedTextColor)
            }
        }

        val uncheckedDecorator = object: ToggleSwitchButton.ViewDecorator {
            override fun decorate(view: View, position: Int) {
                val textView = view.findViewById(R.id.text_view) as TextView
                textView.setTextColor(uncheckedTextColor)
            }
        }

        setView(R.layout.toggle_switch_button_view, entries.size,
                prepareDecorator, checkedDecorator, uncheckedDecorator)
    }

    fun setView(layoutId: Int, numEntries: Int,
                prepareDecorator: ToggleSwitchButton.ToggleSwitchButtonDecorator) {

        setView(layoutId, numEntries, prepareDecorator, null, null)
    }

    fun setView(layoutId: Int, numEntries: Int,
                prepareDecorator: ToggleSwitchButton.ToggleSwitchButtonDecorator,
                checkedDecorator: ToggleSwitchButton.ViewDecorator?,
                uncheckedDecorator: ToggleSwitchButton.ViewDecorator?) {
        removeAllViews()
        buttons.clear()

        this.layoutId = layoutId
        this.numEntries = numEntries
        this.checkedDecorator = checkedDecorator
        this.uncheckedDecorator = uncheckedDecorator

        for (index in 0..numEntries - 1) {
            val positionType = getPosition(index, numEntries)
            val button = ToggleSwitchButton(context, index, positionType, this,
                    layoutId, prepareDecorator, checkedDecorator, uncheckedDecorator,
                    checkedBackgroundColor, R.color.black,
                    borderRadius, borderWidth, uncheckedBackgroundColor,
                    R.color.black, separatorColor, toggleMargin.toInt())
            buttons.add(button)
            addView(button)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = toggleElevation
        }

        manageSeparatorVisiblity()
    }

    fun reDraw() {
        setView(layoutId, numEntries, prepareDecorator, checkedDecorator, uncheckedDecorator)
        onRedrawn()
    }


    /*
       Protected instance methods
     */

    protected abstract fun onRedrawn()

    protected fun manageSeparatorVisiblity() {
        for ((index, button) in buttons.withIndex()) {
            if (separatorVisible && index < buttons.size - 1 && !hasBorder() && !areToggleSeparated()) {
                button.setSeparatorVisibility(button.isChecked == buttons[index + 1].isChecked)
            }
            else {
                button.setSeparatorVisibility(false)
            }
        }
    }

    /*
       Private instance methods
     */

    private fun areToggleSeparated() : Boolean {
        return toggleMargin > 0
    }

    private fun getPosition(index : Int, size: Int) : ToggleSwitchButton.PositionType {
        if (index == 0) {
            return ToggleSwitchButton.PositionType.LEFT
        }
        else if (index == size - 1) {
            return ToggleSwitchButton.PositionType.RIGHT
        }
        else {
            return ToggleSwitchButton.PositionType.CENTER
        }
    }

    private fun hasBorder() : Boolean {
        return borderWidth > 0f
    }

    private fun isFullHeight() : Boolean {
        return layoutHeight == LinearLayout.LayoutParams.MATCH_PARENT
    }

    private fun isFullWidth() : Boolean {
        return layoutWidth == LinearLayout.LayoutParams.MATCH_PARENT
    }

    private fun setUpView() {
        layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        orientation = HORIZONTAL
    }

    private fun getStyleColor(context: Context, r_attr_theme_color: Int, @ColorRes defaultColorResId: Int): Int {
        val typedValue = TypedValue()
        if (context == null) {
            return ContextCompat.getColor(context, defaultColorResId)
        }
        val theme = context!!.theme

        try {
            if (!theme.resolveAttribute(r_attr_theme_color, typedValue, true)) {
                return ContextCompat.getColor(context, defaultColorResId)
            }
        } catch (ignore : Exception) {
            return ContextCompat.getColor(context, defaultColorResId)
        }

        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return typedValue.data
        }

        try {
            val arr = context!!.obtainStyledAttributes(typedValue.data,
                    intArrayOf(r_attr_theme_color))
            var c = arr.getColor(0, -1)
            arr.recycle()
            if (c == -1) {
                // Sometimes TypedArray.getColor fails, but using TypedValue.resourceId
                // seems to work fine.  Could be related to Leanback?
                // TypedArray.getColor:
                // - failed | API 22 | DarkTheme  | Android TV
                // - failed | API 22 | DarkTheme  | Nexus 7
                // - Ok     | API 22 | LightTheme | Android TV
                // - Ok     | API 22 | LightTheme | Nexus 7
                // - Ok     | API 19 | LightTheme | GT 3
                // - Ok     | API 19 | DarkTheme  | GT 3
                // - Ok     | API 18 | LightTheme | Smartphone
                // - Ok     | API 18 | DarkTheme  | Smartphone
                // - Ok     | API 17 | DarkTheme  | FireTV
                c = ContextCompat.getColor(context!!, typedValue.resourceId)
                if (c >= 0) {
                    return c;
                }
                return ContextCompat.getColor(context, defaultColorResId)
            } else {
                return c
            }
        } catch (ignore: Resources.NotFoundException) {
        }

        if (typedValue.data >= 0) {
            return typedValue.data
        }
        return ContextCompat.getColor(context, defaultColorResId)
    }
}