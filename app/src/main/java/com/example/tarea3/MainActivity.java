package com.example.tarea3;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    //Inicializamos Atributos
    MediaPlayer mp;
    MediaRecorder mr;
    Button btReproducir, btAvanzar, btRetroceder;

    //Creamos variable para ruta del archivo
    private String outputFile = null;

    //Se usara para ver si el boton play se esta reproduciendo o en pausa
    boolean estaPlay = true;

    //ID del permiso
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Permisos para RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos botones
        btReproducir = findViewById(R.id.btReproducir);
        btAvanzar = findViewById(R.id.btAvanzar);
        btRetroceder = findViewById(R.id.btRetroceder);
        //Inicializamos el boton Reproducir con una imagen
        btReproducir.setBackgroundResource(R.drawable.play);

        //Asociamos OnClick
        btReproducir.setOnClickListener(this);
        btAvanzar.setOnClickListener(this);
        btRetroceder.setOnClickListener(this);

        //Inicializamos el MediaPlayer
        mp = new MediaPlayer();

        //Iniciamos el boton Reproducir de tal manera que no se pueda usar hasta que se cargue archivo
        btReproducir.setEnabled(false);

        //Cogemos el layout del IU para poner el boton grabar
        LinearLayout lg = findViewById(R.id.layoutGrabar);
        //Creacion del boton
        RecordButton rb = new RecordButton(this);
        //Añadir boton al contenedor
        lg.addView(rb);
        //Configurar el boton para que se muestre una imagen
        rb.setBackgroundResource(R.drawable.grabar);

        //Poner a la activity los listener del MediaPlayer
        mp.setOnCompletionListener(this);
    }

    //Si se sale de la activity
    public void onPause() {
        super.onPause();
        //Si hay contenido reproduciendo se para
        if(mp.isPlaying()){
            mp.pause();
        }
    }

    //Si esta en primer plano
    public void onResume() {
        //Si el reproductor no se ejecuta y no es nulo se reinicia la reproduccion
        if(!mp.isPlaying() && mp!=null){
            mp.start();
        }
        super.onResume();
    }

    //Si la activity se termina
    protected void onDestroy() {
        super.onDestroy();
        if (mp!= null) {
            mp.release();
            mp = null;
        }
    }

    //Metodo para iniciar grabacion
    public void startRecording(){
        //Ruta donde guardamos el archivo
        outputFile = getFilesDir().getAbsolutePath() + "Grabacion.3gp";
        Toast.makeText(this,"Grabando",Toast.LENGTH_SHORT).show();
        //Comprobamos permisos
        if (ContextCompat.checkSelfPermission(this,permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            //Inicializamos MR
            mr = new MediaRecorder();
            //Seleccionamos la fuente del audio (microfono)
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);
            //Seleccionamos el formato de salida (3GP)
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //Seleccionar el codificador
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //Buscamos la ruta para guardar la grabación
            mr.setOutputFile(outputFile);

            //Se prepara la grabacion
            try {
                mr.prepare();
            } catch (IOException e) {
                mr.reset();
                mr.release();
                mr = null;
                e.printStackTrace();
            }
            //se comienza la grabacion
            mr.start();
        }
        else{
            //Si no tenemos el permiso se realiza una peticion
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    //Metodo parar grabacion
    public void stopRecording(){

        Toast.makeText(this,"Grabación Completada",Toast.LENGTH_SHORT).show();
        enableButton(true);
        //Para la grabacion
        mr.stop();
        //resetea el MediaRecorder
        mr.reset();
        //libera el  MediaRecorder
        mr.release();
        mr = null;

        //Cogemos el archivo 3gp de la ruta y los preparamos para reproducir
        mp = new MediaPlayer();
        try {
            mp.setDataSource(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Resultado de los permisos de grabacion
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
        else{
            startRecording();
        }
    }

    //Metodo para iniciar reproduccion
    public void play(){
        mp.start();
        //Poner el onCompletion cuando termine la reproducción actual
        mp.setOnCompletionListener(this);
    }

    //Metodo para parar reproduccion
    public void pause(){
        mp.pause();
    }

    //Metodo para activar y desactivar el boton reproducir
    void enableButton(boolean btReproducir){
        this.btReproducir.setEnabled(btReproducir);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Boton Reproducir
            case R.id.btReproducir:
                //El boton reproducir tambien sera el de pausa, por lo que debemos
                // ver si esta activado o no con el boolean
                if(estaPlay){
                    //Si esta true se da la opcion de que puede reproducirse
                    play();
                    //introducimos una imagen de pause ya que se esta reproduciendo
                    btReproducir.setBackgroundResource(R.drawable.pause);
                    Toast.makeText(this,"Play",Toast.LENGTH_SHORT).show();
                    estaPlay=false;
                    break;
                }
                else{
                    //Si esta false se da la opcion de que puede pausar
                    pause();
                    //introducimos una imagen de play ya que se ha parado la reproduccion
                    btReproducir.setBackgroundResource(R.drawable.play);
                    Toast.makeText(this,"Pause",Toast.LENGTH_SHORT).show();
                    estaPlay=true;
                    break;
                }
            //boton para avanzar
            case R.id.btAvanzar:
                avanza();
                Toast.makeText(this,"Avanzar",Toast.LENGTH_SHORT).show();
                break;
            //boton para retroceder
            case R.id.btRetroceder:
                retrocede();
                Toast.makeText(this,"Retroceder",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //Metodo Avanzar 5seg
    public void avanza() {
        //5 seg son 5000 milisegundas
        mp.seekTo(mp.getCurrentPosition()+5000);
    }

    // Metodo Retroceder 5seg
    public void retrocede() {
        mp.seekTo(mp.getCurrentPosition()-5000);
    }

    //Este metodo se llama cuando termina una reproduccion
    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(this,"Completado",Toast.LENGTH_SHORT).show();
        //Activamos el boton para que pueda darse el play
        btReproducir.setBackgroundResource(R.drawable.play);
        estaPlay=true;
    }
}