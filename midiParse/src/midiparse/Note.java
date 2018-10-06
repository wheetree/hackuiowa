package midiparse;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pig
 */
public class Note {
    
    public Note(long start, long duration, String name, int key, int velocity)
    {
        this.start = start;
        this.duration = duration;
        this.name = name;
        this.key = key;
        this.velocity = velocity;
    }
    
    public void setStart(long start)
    {
        this.start = start;
    }
    
    public long getStart()
    {
        return start;
    }
    
    public void setDuration(long duration)
    {
        this.duration = duration;
    }
    
    public long getDuration()
    {
        return duration;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setKey(int key)
    {
        this.key = key;
    }
    
    public int getkey()
    {
        return key;
    }
    
    public void setVelocity(int velocity)
    {
        this.velocity = velocity;
    }
    
    public int getVelocity()
    {
        return velocity;
    }
    
    int track;
    private long start;
    private long duration;
    private String name;
    private int key;
    private int velocity;
}
