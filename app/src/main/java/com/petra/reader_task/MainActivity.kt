package com.petra.reader_task

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {
    private lateinit var textViewContent: TextView
    private lateinit var btnZoomIn: ImageButton
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var btnChangeColor: ImageButton
    private var currentTextSize: Float = 16f
    private val textSizeStep = 2f // Step size for increasing or decreasing text size
    private lateinit var seekBarRed: SeekBar
    private lateinit var seekBarGreen: SeekBar
    private lateinit var seekBarBlue: SeekBar
    private var redThumbPosition = 0
    private var greenThumbPosition = 0
    private var blueThumbPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewContent = findViewById(R.id.text_view_content)
        btnZoomIn = findViewById(R.id.action_zoom_in)
        btnZoomOut = findViewById(R.id.action_zoom_out)
        btnSearch = findViewById(R.id.action_search)
        btnChangeColor = findViewById(R.id.action_change_color)

        loadTextFromAssets("document.txt")

        btnZoomIn.setOnClickListener { zoomInText() }
        btnZoomOut.setOnClickListener { zoomOutText() }
        btnSearch.setOnClickListener { showSearchDialog() }
        btnChangeColor.setOnClickListener { showColorPicker() }

    }

    private fun loadTextFromAssets(fileName: String) {
        val inputStream = assets.open(fileName)
        val content = inputStream.bufferedReader().use { it.readText() }
        textViewContent.text = content
        // Enable text selection
        textViewContent.setTextIsSelectable(true)
    }

    private fun zoomInText() {
        currentTextSize += textSizeStep
        updateTextSize()
    }

    private fun zoomOutText() {
        if (currentTextSize > textSizeStep) {
            currentTextSize -= textSizeStep
            updateTextSize()
        }
    }

    private fun updateTextSize() {
        textViewContent.textSize = currentTextSize
    }

    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        val searchEditText = EditText(this)
        searchEditText.hint = "Enter word to search"
        builder.setView(searchEditText)
            .setTitle("Search Word")
            .setPositiveButton("Search") { dialog, _ ->
                val wordToSearch = searchEditText.text.toString()
                highlightWord(wordToSearch)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun highlightWord(word: String) {
        val text = textViewContent.text.toString()
        val spannableBuilder = SpannableStringBuilder(text)
        var startIndex = text.indexOf(word)
        if (startIndex == -1) {
            Toast.makeText(this, "Word not found", Toast.LENGTH_SHORT).show()
            return
        }

        while (startIndex != -1) {
            val endIndex = startIndex + word.length
            spannableBuilder.setSpan(
                ForegroundColorSpan(Color.RED),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            startIndex = text.indexOf(word, endIndex)
        }
        textViewContent.text = spannableBuilder
    }

    private fun showColorPicker() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.popup_color_picker, null)

        seekBarRed = layout.findViewById(R.id.seekBarRed)
        seekBarGreen = layout.findViewById(R.id.seekBarGreen)
        seekBarBlue = layout.findViewById(R.id.seekBarBlue)

        // Set thumb positions for each SeekBar
        seekBarRed.progress = redThumbPosition
        seekBarGreen.progress = greenThumbPosition
        seekBarBlue.progress = blueThumbPosition

        // Set up SeekBar listeners
        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar == seekBarRed || seekBar == seekBarGreen || seekBar == seekBarBlue) {
                    updateTextColor()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar == seekBarRed) {
                    redThumbPosition = seekBar.progress
                } else if (seekBar == seekBarGreen) {
                    greenThumbPosition = seekBar.progress
                } else if (seekBar == seekBarBlue) {
                    blueThumbPosition = seekBar.progress
                }
            }
        }

        seekBarRed.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarGreen.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarBlue.setOnSeekBarChangeListener(seekBarChangeListener)

        // Create and show the dialog
        val builder = AlertDialog.Builder(this)
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()
    }

    private fun updateTextColor() {
        val red = seekBarRed.progress
        val green = seekBarGreen.progress
        val blue = seekBarBlue.progress
        val color = Color.rgb(red, green, blue)
        textViewContent.setTextColor(color)
    }



}