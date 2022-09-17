package com.example.allfilefetch;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements A {

    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private ArrayList<File> mp3_list;
    private File path;
    public static MediaPlayer mp;
    public static SeekBar seekBar;
    public static Runnable runnable;
    public static Handler handler;
    private ImageView pause,cancel,play;
    private boolean check=false;
    TextView title,startingPoint,endingPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        seekBar=findViewById(R.id.seekbar);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        runTimePermission();
        mp=new MediaPlayer();
        handler=new Handler();
//        String path= Environment.getExternalStorageState();
//        String path2= Environment.getExternalStorageDirectory().getPath();
//        String path3= Environment.getExternalStorageDirectory().getAbsolutePath();
//        String path4= Environment.getExternalStorageDirectory().getName();
//        String path5= Environment.getExternalStorageDirectory().getParent();
//        Log.i("rafaqat", "path: "+path);
//        Log.i("rafaqat", "path2: "+path2);
//        Log.i("rafaqat", "path3: "+path3);
//        Log.i("rafaqat", "path4: "+path4);
//        Log.i("rafaqat", "path5: "+path5);

//        File file=Environment.getExternalStorageDirectory();
//        File file2=Environment.getExternalStoragePublicDirectory(Environment.getExternalStorageState());
//        File file3=Environment.getDataDirectory().getAbsoluteFile();
//        File file4=Environment.getDataDirectory().getParentFile();
//        File file5=Environment.getDownloadCacheDirectory();
//        File file7=Environment.getRootDirectory();
//        Log.i("rafaqat", "file: "+file);
//        Log.i("rafaqat", "file2: "+file2);
//        Log.i("rafaqat", "file3: "+file3);
//        Log.i("rafaqat", "file4: "+file4);
//        Log.i("rafaqat", "file5: "+file5);
//        Log.i("rafaqat", "file7: "+file7);

//        ArrayList<File> files=new ArrayList<>();



    }


    private void runTimePermission() {
        if (!PermissionUtil.isPermissionGranted(this)) {
            new AlertDialog.Builder(this).setTitle("All File Permission")
                    .setMessage("Due to Android 11 & 12 restriction, this app required all files permission ")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            takePermission();
                        }
                    }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setIcon(R.drawable.ic_launcher_foreground).show();
        } else {
            Toast.makeText(this, "Permission already Granted", Toast.LENGTH_SHORT).show();
            display_All_Mp3();
        }
    }

    private void takePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(MainActivity.this, permission, 2296);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2296:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                        display_All_Mp3();
                        Toast.makeText(this, "OnRequest Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                        takePermission();
                    }
                }
                break;
        }
    }

    public void display_All_Mp3() {
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mp3_list = new ArrayList<>();
        mp3_list.addAll(findMp3(path));
        adapter = new MyAdapter(this, mp3_list, this);
        recyclerView.setAdapter(adapter);


    }

    public ArrayList<File> findMp3(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        try {
            //Check pdf is r not
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findMp3(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3")) {
                        arrayList.add(singleFile);
                    }

                }
            }
        } catch (Exception e) {
            Log.i("raf", "findPDF: " + e.getMessage());
        }
        return arrayList;

    }

    @Override
    public void click(TextView textName, View view, int pos) {

        showPopMenu(textName, view, pos);

    }

    private void showPopMenu(TextView textView, View v, int pos) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.inflate(R.menu.classes_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.delete) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Delete")
                            .setCancelable(false)
                            .setMessage("Do you really want to delete this class?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String s = textView.getText().toString();
                                    File file = new File(path + "/" + s);
                                    file.delete();
                                    if (file.exists()) {
                                        try {
                                            file.getCanonicalFile().delete();
                                            adapter.notifyItemRemoved(pos);
                                            adapter.notifyItemRangeChanged(pos, mp3_list.size());
                                        } catch (IOException e) {
                                        }
                                        if (file.exists()) {
                                            getApplicationContext().deleteFile(file.getName());

                                        }
                                    }
                                    adapter.notifyItemRemoved(pos);
                                    adapter.notifyItemRangeChanged(pos, mp3_list.size());

                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create();
                    dialog.show();
                } else if (id == R.id.view) {
                    Uri urii = Uri.parse(String.valueOf(path));
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(urii, "*/*");
                    startActivity(intent);

                } else if (id == R.id.share) {
                    String s = textView.getText().toString();
                    String ss = path + "/" + s;
                    Log.i("RAFAQAT", "onMenuItemClick: " + ss);

                    Uri uri = Uri.parse(ss);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio/*");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share Sound File"));

                } else if (id == R.id.play) {
                    String s = textView.getText().toString();
                    String ss = path + "/" + s;
//                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse(ss), "audio/*");
//                    MainActivity.this.startActivity(intent);



                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    View view = inflater.inflate(R.layout.music_player_dialog, null);


                    seekBar=view.findViewById(R.id.seekbar);
                    pause=view.findViewById(R.id.pause);
                    cancel=view.findViewById(R.id.cancel);
                    play=view.findViewById(R.id.play);
                    title=view.findViewById(R.id.title);
                    startingPoint=view.findViewById(R.id.startingPoint);
                    endingPoint=view.findViewById(R.id.endingPoint);
                    alert.setView(view);
                    AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(false);

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar s, int i, boolean b) {

                            if (b)
                            {
                                mp.seekTo(i);
                                seekBar.setProgress(i);
                            }

                            startingPoint.setText(convertFormat(mp.getCurrentPosition()));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    title.setText(s);

                    pause.setOnClickListener(view1 -> {

                            mp.pause();
                            pause.setVisibility(View.INVISIBLE);
                            play.setVisibility(View.VISIBLE);
                            handler.removeCallbacks(runnable);



                    });
                    play.setOnClickListener(view1 -> {

                        mp.start();
                        pause.setVisibility(View.VISIBLE);
                        play.setVisibility(View.INVISIBLE);



                    });

                    cancel.setOnClickListener(view1 -> {
                        mp.stop();
                        alertDialog.dismiss();
                        mp.reset();


                    });

                    playAudio(ss);
                    alertDialog.show();
                }

                return false;
            }
        });
        popupMenu.show();
    }


    private void playAudio(String ss)
    {
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Uri uri=Uri.parse(ss);
        try {
            mp.setDataSource(MainActivity.this,uri);
            mp.prepare();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mp.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    String  sDuration=convertFormat(mediaPlayer.getDuration());
                    endingPoint.setText(sDuration);
                    UpdateSeekBar updateSeekBar=new UpdateSeekBar();
                    handler.post(updateSeekBar);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String convertFormat(int duration)
    {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration),
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}