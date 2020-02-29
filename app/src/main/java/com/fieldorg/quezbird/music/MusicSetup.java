package com.fieldorg.quezbird.music;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.util.Log;

public class MusicSetup {

    private SoundPool soundPool;

    private int[] soundIds;

    private byte goodNotes[];
    private byte scaleNotes[];

    private byte key;
    private byte chordRoot;

    private byte mode;

    public MusicSetup(Context ctx, int[] fileIds){

        Log.v("Music Construct", ".");
        this.key = 0;
        this.chordRoot = this.key;

        this.mode = 1;

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attrs)
                .build();

        soundIds = new int[96];

        Log.v("Loading sounds", ".");

        Log.v("Package: ",  ctx.getPackageName() );

        for (int i = 0 ; i < 96; i++){
            soundIds[i] = soundPool.load(ctx, fileIds[i] , 1);
        }

        scaleNotes = new byte[] {(byte) ((key)%12),(byte) ((2+key)%12),(byte) ((4+key)%12),(byte) ((5+key)%12),(byte) ((7+key)%12),(byte) ((9+key)%12),(byte) ((11+key)%12)};

        switch(mode){

            case 0: case 1:
                goodNotes = new byte[] {(byte) ((key)%12),(byte) ((2+key)%12),(byte) ((4+key)%12),(byte) ((5+key)%12),(byte) ((7+key)%12),(byte) ((9+key)%12),(byte) ((11+key)%12)};break;
            case 2: // pent
                goodNotes = new byte[] {(byte) ((key)%12),(byte) ((2+key)%12),(byte) ((4+key)%12),(byte) ((7+key)%12),(byte) ((9+key)%12)};break;

        }
    }

    private void playNote(short noteNumber, short duration, Context ctx){

        soundPool.play(soundIds[2*noteNumber + (duration-5)/5], 1, 1, 1, 0, 1.0f);

    }

    public void playNoteCloseTo(short noteNumber, short duration, Context ctx){

        for(short i = 0; i < 6; i++){
            if(goodNote((short) (noteNumber + i))){
                noteNumber += i;
                break;
            }
            if(goodNote((short) (noteNumber - i))){
                noteNumber -= i;
                break;
            }
        }
        playNote(noteNumber, duration, ctx);
    }

    public byte getNoteCloseTo(short noteNumber){

        for(short i = 0; i < 6; i++){
            if(goodNote((short) (noteNumber + i))){
                return (byte) (noteNumber + i);
            }
            if(goodNote((short) (noteNumber - i))){
                return (byte) (noteNumber - i);
            }
        }
        return (byte) noteNumber;
    }

    public void playNoteInChord(short noteNumber, Context ctx){

        for(short i = 0; i < 6; i++){
            if(inMaj((byte) (noteNumber + i), chordRoot)){
                noteNumber += i;
                break;
            }
            if(inMaj((byte) (noteNumber - i), chordRoot)){
                noteNumber -= i;
                break;
            }
        }
        playNote(noteNumber, (short) 5, ctx);
    }


    public void playChord(byte n, byte pivot, Context ctx) { // n = root
        switch(((n-key)%12 + 12)%12){
            case 0:
                playChordNo((byte)1,pivot,ctx);break;
            case 5:
                playChordNo((byte)4, pivot, ctx);break;
            case 7:
                playChordNo((byte)5, pivot, ctx);break;

            case 2:
                playChordNo((byte)2,pivot,ctx);break;
            case 4:
                playChordNo((byte)3, pivot, ctx);break;
            case 9:
                playChordNo((byte)6, pivot, ctx);break;
        }
    }

    public void playChordNo(byte n, byte pivot, Context ctx){

        byte notesDone = 0;
        byte i = 0;

        Log.v("Playing ",""+n );

        switch (n){
            case 1:
                chordRoot = key;
                Log.v("Case ","1" );
                while(notesDone < 3){
                    if(inMaj((byte) (pivot + i), this.key )){// if in root major
                        playNote((byte) (pivot + i), (byte) 10, ctx);
                        notesDone++;
                    }else if(inMaj((byte) (pivot - i), this.key )){
                        playNote((byte) (pivot - i), (byte) 10, ctx);
                        notesDone++;
                    }
                    i++;

                } break;
            case 4:
                chordRoot = (byte) (key + 5);
                while(notesDone < 3){
                    if(inMaj((byte) (pivot + i), (byte) ((this.key +  5)%12) )){// if in 4
                        playNote((byte) (pivot + i), (byte) 10, ctx);
                        notesDone++;
                    }else if(inMaj((byte) (pivot - i), (byte) ((this.key +  5)%12) )){
                        playNote((byte) (pivot - i), (byte) 10, ctx);
                        notesDone++;
                    }
                    i++;
                } break;
            case 5:
                chordRoot = (byte) (key + 7);
                while(notesDone < 3){
                    if(inMaj((byte) (pivot + i), (byte) ((this.key +  7)%12) )){// if in 5
                        playNote((byte) (pivot + i), (byte) 10, ctx);
                        notesDone++;
                    }else if(inMaj((byte) (pivot - i), (byte) ((this.key +  7)%12))){
                        playNote((byte) (pivot - i), (byte) 10, ctx);
                        notesDone++;
                    }
                    i++;
                } break;


            // Minor

            case 3:
                chordRoot = (byte) (key + 4);
                Log.v("Case ","1" );
                while(notesDone < 3){
                    if(inMin((byte) (pivot + i), (byte) ((this.key +  4)%12) )){// if in root major
                        playNote((byte) (pivot + i), (byte) 10, ctx);
                        notesDone++;
                    }else if(inMin((byte) (pivot - i), (byte) ((this.key +  4)%12) )){
                        playNote((byte) (pivot - i), (byte) 10, ctx);
                        notesDone++;
                    }
                    i++;

                } break;
            case 2:
                chordRoot = (byte) (key + 2);
                while(notesDone < 3){
                    if(inMin((byte) (pivot + i), (byte) ((this.key +  2)%12) )){// if in 4
                        playNote((byte) (pivot + i), (byte) 10, ctx);
                        notesDone++;
                    }else if(inMin((byte) (pivot - i), (byte) ((this.key +  2)%12) )){
                        playNote((byte) (pivot - i), (byte) 10, ctx);
                        notesDone++;
                    }
                    i++;
                } break;
            case 6:
                chordRoot = (byte) (key + 9);
                while(notesDone < 3){
                    if(inMin((byte) (pivot + i), (byte) ((this.key +  9)%12) )){// if in 5
                        playNote((byte) (pivot + i), (byte) 10, ctx);
                        notesDone++;
                    }else if(inMin((byte) (pivot - i), (byte) ((this.key +  9)%12))){
                        playNote((byte) (pivot - i), (byte) 10, ctx);
                        notesDone++;
                    }
                    i++;
                } break;
        }
    }

    public void playChordCloseTo(byte note, Context ctx){ // give just a note

        note = getNoteCloseTo(note);

        switch (mode){
            case 0:
                if(inMaj(note, key)){
                    playChord(key, note, ctx);
                }else if(inMaj(note, chordRoot)){
                    playChord(chordRoot, note, ctx);
                }else if(inMaj(note, (byte) (key + 5))){
                    playChord((byte) (key + 5), note, ctx);
                }else if(inMaj(note, (byte) (key + 7))){
                    playChord((byte) (key + 7), note, ctx);
                }break;
            case 1:
                if(inMin(note, (byte) (key + 2))){
                    playChord((byte) (key + 2), note, ctx);
                }else if(inMin(note, chordRoot)){
                    playChord(chordRoot, note, ctx);
                }else if(inMin(note, (byte) (key + 4))){
                    playChord((byte) (key + 4), note, ctx);
                }else if(inMin(note, (byte) (key + 9))){
                    playChord((byte) (key + 9), note, ctx);
                }break;
        }


    }

    private boolean goodNote(short n){
        if(n > 47 || n < 0){
            return false;
        }
        for(byte i : goodNotes){
            if((n - i)%12 == 0){
                return true;
            }
        }

        return false;
    }

    private boolean inMaj(byte n,byte root){

        if(n > 47 || n < 0){ // out of range
            return false;
        }

        if((n - root)%12 == 0){ // root
            return true;
        }
        if((n - root - 7)%12 == 0){ // fifth
            return true;
        }
        if((n - root - 4)%12 == 0){ // maj third
            return true;
        }

        return false;
    }

    private boolean inMin(byte n,byte root){

        if(n > 47 || n < 0){ // out of range
            return false;
        }

        if((n - root)%12 == 0){ // root
            return true;
        }
        if((n - root - 7)%12 == 0){ // fifth
            return true;
        }
        if((n - root - 3)%12 == 0){ // maj third
            return true;
        }

        return false;
    }

    public void setMode(byte m){
        mode = m;
    }


}
