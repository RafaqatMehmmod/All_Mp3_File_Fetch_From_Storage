package com.example.allfilefetch;

import static com.example.allfilefetch.MainActivity.handler;
import static com.example.allfilefetch.MainActivity.mp;
import static com.example.allfilefetch.MainActivity.runnable;
import static com.example.allfilefetch.MainActivity.seekBar;

/**
 * Created by Rafaqat Mehmood
 * Whatsapp No:+923101025532
 * 16/09/2022
 */
public class UpdateSeekBar implements Runnable{

    @Override
    public void run() {
        seekBar.setProgress(mp.getCurrentPosition());
        handler.postDelayed(this,1000);
        handler.removeCallbacks(runnable);

    }
}
