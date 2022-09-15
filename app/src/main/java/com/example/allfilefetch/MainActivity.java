package com.example.allfilefetch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements A {

    String[] permission={Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private ArrayList<File> mp3_list;
    private File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission,1);
        }
        else
        {
            display_All_Mp3();
        }

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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1)
        {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Grand", Toast.LENGTH_SHORT).show();


                display_All_Mp3();
            }
            else
            {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
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
        adapter = new MyAdapter(this, mp3_list,this);
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
        }
        catch (Exception e){
            Log.i("raf", "findPDF: "+e.getMessage());
        }
        return arrayList;

    }

    @Override
    public void click(TextView textName, View view) {

        showPopMenu(textName, view);

    }

    private void showPopMenu(TextView textView, View v) {
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

                                        String s=textView.getText().toString();
                                        File file = new File(path+"/"+s);
                                        file.delete();
                                        if(file.exists()){
                                            try {
                                                file.getCanonicalFile().delete();
                                            }
                                            catch (IOException e) {}
                                            if(file.exists()){
                                                getApplicationContext().deleteFile(file.getName());

                                            }
                                        }

                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create();
                    dialog.show();
                }
                else if (id==R.id.view)
                {
                    Uri urii=Uri.parse(String.valueOf(path));
                    Intent intent=new Intent(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(urii,"*/*");
                    startActivity(intent);

                }
                return false;
            }
        });
        popupMenu.show();
    }
}