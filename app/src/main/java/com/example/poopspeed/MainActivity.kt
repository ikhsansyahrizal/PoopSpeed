package com.example.poopspeed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationUtils.loadAnimation
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.tabs.TabLayout
import android.media.MediaPlayer
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {
    private lateinit var gameScoreText: TextView
    private lateinit var timeLeftText: TextView
    private lateinit var tapMeImage: ImageView
    private lateinit var reset_button: Button
    private lateinit var play_btn: ImageButton
    private lateinit var pause_btn: ImageButton
    private var score = 0
    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 60000
    private var countDownInterval: Long = 1000
    private var timeLeft = 60
    private val TAG = MainActivity::class.java.simpleName
    private var isCancelled = false
    var mMediaPlayer: MediaPlayer? = null

    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeft")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        gameScoreText = findViewById(R.id.game_score_text)
        timeLeftText = findViewById(R.id.time_left_text)
        tapMeImage = findViewById(R.id.tap_me_image)
        reset_button = findViewById(R.id.reset_button)
        play_btn = findViewById(R.id.play_btn)
        pause_btn = findViewById(R.id.pause_btn)

        val mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.music1)

        play_btn.setOnClickListener{
                    mediaPlayer.start()
        }

        pause_btn.setOnClickListener {
                mediaPlayer.pause()

        }


        tapMeImage.setOnClickListener { v->
            val bounceAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.bounce)
            v.startAnimation(bounceAnimation)
            incrementScore()
        }

        reset_button.setOnClickListener { v->
            val bounceAnimation = loadAnimation(this, R.anim.bounce)
            v.startAnimation(bounceAnimation)


            restartGame()
        }



        Log.d(TAG, "onCreate called. Score is : $score")

        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }

    }

    private fun restoreGame(){
        val restoredScore = getString(R.string.your_score, score)
        gameScoreText.text = restoredScore
        val restoredTime = getString(R.string.time_left, timeLeft)
        timeLeftText.text = restoredTime
        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(), countDownInterval){
            override  fun onTick(millisUntilFinished: Long){
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftText.text = timeLeftString
            }

            override  fun onFinish(){
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }



    private fun incrementScore(){
        score++
        val newScore= getString(R.string.your_score, score)
        gameScoreText.text = newScore

        if (!gameStarted){
            startGame()
        }
    }

    private fun resetGame(){
        score = 0

        val initialScore = getString(R.string.your_score, score)
        gameScoreText.text = initialScore

        val initialTimeLeft = getString(R.string.time_left, 60)
        timeLeftText.text = initialTimeLeft

        countDownTimer = object  : CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                timeLeft = millisUntilFinished.toInt()/1000
                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftText.text = timeLeftString
            }

            override fun onFinish(){
                endGame()

            }
        }
        gameStarted = false
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true

    }

    private fun restartGame(){
        countDownTimer.cancel()
        resetGame()
        gameScoreText.text = getString(R.string.your_score, score)
        timeLeftText.text = getString(R.string.time_left, 60)
        Toast.makeText(this,"Game has been reset",Toast.LENGTH_LONG).show()
    }

    private fun endGame(){
        Toast.makeText(this,getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()
        resetGame()

    }


}