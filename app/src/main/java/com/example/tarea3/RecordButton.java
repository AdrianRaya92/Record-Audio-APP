package com.example.tarea3;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class RecordButton extends androidx.appcompat.widget.AppCompatButton {

    //Atributo
    boolean estaGrabando = true; //Para saber si se puede grabar
    MainActivity activity;

    public RecordButton(@NonNull Context context) {
        super(context);
        activity= (MainActivity) context;
        setOnClickListener(clicker);
        //introducimos imagen para el boton para comenzar a grabar
        setBackgroundResource(R.drawable.grabar);
    }

    //Creamos un listener del evento de click que inicia y para la grabacion
    OnClickListener clicker = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(estaGrabando){
                //introducimos imagen para parar de grabar
                setBackgroundResource(R.drawable.stopgrabar);
                estaGrabando=false;
                //Comineza a grabar
                //Metodo para grabar
                activity.startRecording();
            }
            else{
                //introducimos imagen para grabar
                setBackgroundResource(R.drawable.grabar);
                estaGrabando=true;
                //Para de grabar
                //Metodo para parar
                activity.stopRecording();
            }
        }
    };
}
