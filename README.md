# midiMusic
Take any MIDI music track and play along with a specific channel from the track using an MIDI controller.  Notes to play and notes you play will be displayed on screen, and when it's all finished you'll receive a score for how well you play.

Entry for HackUIOWA 2018.  See submission here: https://hackuiowa.hackerearth.com/sprints/hackuiowa-1/dashboard/450868b/submission/

# Compiling
```
cd hackuiowa/midiMusic
mvn compile
```

# Running
`mvn exec:java -Dexec.mainClass="hackuiowa.app.MainApp"`
