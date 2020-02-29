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

import android.opengl.GLU;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 1.0/1.1.
 */
public class Cube {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final ShortBuffer drawListBuffer2;
    //private final ByteBuffer colorBuffer;


    private Float posX,posY;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.25f,  0.25f, 0.25f,   // top left
            -0.25f, -0.25f, 0.25f,   // bottom left
            0.25f, -0.25f, 0.25f,   // bottom right
            0.25f,  0.25f, 0.25f,     // top right
            -0.25f,  0.25f, -0.25f,
            -0.25f, -0.25f, -0.25f,
            0.25f, -0.25f, -0.25f,
            0.25f,  0.25f, -0.25f
    };

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3, 0, 1, 5, 0, 5, 4, 0, 4, 7, 0, 7, 3}; // order to draw vertices

    private final short drawOrder2[] = {6, 7, 4, 6, 4, 5, 6, 5, 1, 6, 1, 2, 6, 2, 3, 6, 3, 7};

    float color[] = { 0.0f, 0.709803922f, 0.898039216f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Cube(float x, float y) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer dlb2 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder2.length * 2);
        dlb2.order(ByteOrder.nativeOrder());
        drawListBuffer2 = dlb2.asShortBuffer();
        drawListBuffer2.put(drawOrder2);
        drawListBuffer2.position(0);


        this.posX = x;
        this.posY = y;
    }

    public Cube(){
        this(0,0);
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param gl - The OpenGL ES context in which to draw this shape.
     */
    public void draw(GL10 gl) {

        gl.glLoadIdentity();   // reset the matrix to its default state
        // When using GL_MODELVIEW, you must set the view point
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Since this shape uses vertex arrays, enable them
        gl.glTranslatef(posX, posY, 0.0f);
        gl.glRotatef(45, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(0.090f * ((int) SystemClock.uptimeMillis() % 4000L), 1.0f, 0.8f, 0.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);


        // draw the shape
        gl.glColor4f(       // set color
                color[0], color[1],
                color[2], color[3]);
        gl.glVertexPointer( // point to vertex data:
                COORDS_PER_VERTEX,
                GL10.GL_FLOAT, 0, vertexBuffer);
        //gl.glColorPointer( 3, GL_UNSIGNED_BYTE, 0, colorBuffer );
        gl.glDrawElements(  // draw shape:
                GL10.GL_TRIANGLES,
                drawOrder.length, GL10.GL_UNSIGNED_SHORT,
                drawListBuffer);

        //gl.glClear(GL10.GL_COLOR_BUFFER_BIT);


        gl.glColor4f(       // set color
                color[1], color[0],
                color[2], color[3]);
        gl.glVertexPointer( // point to vertex data:
                COORDS_PER_VERTEX,
                GL10.GL_FLOAT, 0, vertexBuffer);
        //gl.glColorPointer( 3, GL_UNSIGNED_BYTE, 0, colorBuffer );
        gl.glDrawElements(  // draw shape:
                GL10.GL_TRIANGLES,
                drawOrder.length, GL10.GL_UNSIGNED_SHORT,
                drawListBuffer2);

        //gl.glDrawElements( GL10.GL_TRIANGLES, drawOrder.length, GL_UNSIGNED_BYTE, indices );

        // Disable vertex array drawing to avoid
        // conflicts with shapes that don't use it
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}