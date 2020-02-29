/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fieldorg.quezbird.music;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRendererOld mRenderer; // was old
    private final MusicSetup mMusic;
    //private int i = 0;

    private int[] fileIds; // to be passed to mMusic

    public MyGLSurfaceView(Context context) {
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
        //mRenderer = new MyGLRenderer();
        mRenderer = new MyGLRendererOld();
        setRenderer(mRenderer);



        fileIds = new int[96];

        for (int i = 0 ; i < 48; i++){
            fileIds[2*i] = fileIdOf((short) i, (short) 5);      // add short
            fileIds[2*i+ 1] = fileIdOf((short) i, (short) 10);  // add long
        }
        mMusic = new MusicSetup(getContext(), fileIds);
        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    private long lastPlayed = -1;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, we are only
        // interested in events where the touch position changed.


        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mMusic.setMode((byte) (Math.round(((float)((System.nanoTime())%2000))/2000)));
                Log.v("Now Chord ",""+(Math.round(((System.nanoTime()/1000000)%2000)/2000)) );

                mMusic.setMode((byte) (0));

                mRenderer.addSquare(2*(getWidth()/2 - x)/(getHeight()),2*(getHeight()/2 - y)/(getHeight()));

                mMusic.playNoteCloseTo((short) (48*(y/getHeight())),(short) 5, getContext());


                mMusic.playChordCloseTo((byte) (48*(y/getHeight())), getContext());
                break;

            case MotionEvent.ACTION_MOVE:

                if ((System.nanoTime() - lastPlayed)/1000000 > 200){
                    mMusic.playNoteInChord((short) (48*(y/getHeight())), getContext());
                    lastPlayed = System.nanoTime();
                }


        }

        return true;
    }


    private static final String[] notes = new String[] {"c","cs","d","ds","e","f","fs","g","gs","a","as","b"};

    private int fileIdOf(short noteNumber, short duration){ // 0 to 47,   5 or 10 (tenths of a second)

        if(duration == 5){
            return this.getResources().getIdentifier( notes[(noteNumber%12)]+(2+(noteNumber - (noteNumber%12))/12)+"_05", "raw" , "com.fieldorg.quezbird.music" );

        }else{
            return this.getResources().getIdentifier( notes[(noteNumber%12)]+(2+(noteNumber - (noteNumber%12))/12)+"_1", "raw" , "com.fieldorg.quezbird.music" );
        }
    }

}
