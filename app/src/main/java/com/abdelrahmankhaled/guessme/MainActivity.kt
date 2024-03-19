package com.abdelrahmankhaled.guessme

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector
import java.util.Locale
import java.util.Random

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, ShakeDetector.ShakeListener {
    lateinit var rightWrongText:TextView
    lateinit var countText:TextView
    lateinit var soundIcon:ImageView
    lateinit var startButton:Button
    val r=Random()
    var x=0
    var wrongs=0
    var gameStarted=false
    val views= mutableListOf<TextView>()
    lateinit var tts:TextToSpeech
    var sound=true
    lateinit var pref:SharedPreferences
    //memory leak

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rightWrongText=findViewById(R.id.rightWrongText)
        countText=findViewById(R.id.countText)
        soundIcon=findViewById(R.id.soundIcon)
        startButton=findViewById(R.id.startButton)
        tts= TextToSpeech(this,this)
        pref=getPreferences(MODE_PRIVATE)
        sound=pref.getBoolean("sound",true)
        if (sound == true) {
            soundIcon.setImageResource(R.drawable.sound_on)
        } else {
            soundIcon.setImageResource(R.drawable.sound_off)
        }
        Sensey.getInstance().init(this)
        Sensey.getInstance().startShakeDetection(this)
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        Sensey.getInstance().stopShakeDetection(this)
        Sensey.getInstance().stop()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val editor=pref.edit()
        editor.putBoolean("sound",sound)
        editor.apply()
        super.onBackPressed()
    }

    fun answer(view: View) {

        if(!gameStarted){
            YoYo.with(Techniques.Shake).duration(500).repeat(3).playOn(startButton)
//            Toast.makeText(this, "please click start", Toast.LENGTH_LONG).show()
            return
        }
        val tv=view as TextView
        YoYo.with(Techniques.Tada).duration(500).repeat(3).playOn(tv)
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(rightWrongText)
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(countText)
        tv.isEnabled=false
        views.add(tv)
        val number=tv.text.toString().toInt()
        if (sound){
            tts.speak(number.toString(),TextToSpeech.QUEUE_FLUSH,null,null)
        }

        if(number==x){
            rightWrongText.setText("right")
            gameStarted=false
        }else{
            rightWrongText.setText("wrong")
            wrongs++
            countText.setText(wrongs.toString())
        }
        if (wrongs==3){
            Toast.makeText(this, "game over", Toast.LENGTH_LONG).show()
            gameStarted=false
        }
    }
    fun start(view: View) {
        wrongs=0

        for (view in views) {
            view.isEnabled=true
        }
        views.clear()
        rightWrongText.setText("")
        countText.setText("")
        gameStarted=true
        x=r.nextInt(9)+1
        Toast.makeText(this,"$x",Toast.LENGTH_LONG).show()
    }

    override fun onInit(p0: Int) {
//        tts.setLanguage(Locale("ar"))
//        tts.setPitch(0.6.toFloat())
//        tts.setSpeechRate(0.6.toFloat())
    }

    fun change(view: View) {
        changeSound(sound)
        sound = !sound
    }

    private fun changeSound(sound:Boolean) {
        if (sound == true) {
            soundIcon.setImageResource(R.drawable.sound_off)
        } else {
            soundIcon.setImageResource(R.drawable.sound_on)
        }

    }

    override fun onShakeDetected() {

    }

    override fun onShakeStopped() {
        Toast.makeText(this,"shaked mobile",Toast.LENGTH_LONG).show()
//        start(startButton)
    }
}