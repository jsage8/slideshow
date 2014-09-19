slideshow
=========

Media and Development Lab, Hamilton College - Java Swing desktop slideshow application for psychology experiments

This project was design as experimental stimuli for a psychology lab.
The finished program was used to generate data that was later
published.

Sage, K. (2014). What pace is best? Assessing adults’ learning from 
slideshows and video. Journal of Educational Multimedia and Hypermedia, 
23(1), 91-108.

A series of magic tricks were broken down into frames. The program
displays these frames at various paces as a slideshow. The pace can 
be set by the experimenter. The pace options include:

Set Pace - slides advance automatically every x number of milliseconds.

Set Pace Free Pause - slides advance automatically every x number of 
milliseconds, but the slideshow can be paused by clicking the mouse.

Set Pace Subgoal Pause - slides advance automatically up to 
pre-determined break points where they automatically pause. Users
must click to continue and the slides advance automatically again.

Set Pace Timed Pause - slides advance automatically and pause
automatically ever x number of slides. Users must click to
continue and the slides advance automatically again.

Yoked Pace - slides advance automatically at the same pace as
a previous user. The time per slide and the trick order is
extracted from a previous datafile. Note that this was used
in a previous version of the study and was not necessary for
this study, as a result this mode was not fully implemented in
this version of the program, but the mode was left in the menu
as a stub for future use if necessary.

Self Pace - slides advance only when the user clicks the mouse.

Note that between tricks in all of the studies the slides
will not advance without hitting enter. This was to allow the
experimenter to give participants further instructions before
they begin the next set of slides.

The program was written in Java using the Swing package. The
most challenging portion of this project was nimbly loading
the images as they are needed. There are 737 images at a total
of 32.4 mb of data. Loading them all in advance was not possible
on the system being used. Instead they are loaded just in advance
of when they are needed and the viewed images are cleared from 
memory to make room for new ones.