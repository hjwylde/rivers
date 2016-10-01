package com.hjwylde.rivers.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;
import com.hjwylde.rivers.R;

public final class Sections {

    public static final Section[] MOCKED_SECTIONS = new Section[]{
            new Section(1, "Hutt gorge", "Hutt Gorge is the closest class III to the capital of NZ, Wellington. It needs a northwest fresh or sustained southerly rain to be runnable (over 1.8m on the Te Marua gauge). Above 2.7m on this gauge it is a harder run (class IV) with plenty of holes and few eddies so you need to know where the holes are going to be.\n" +
                    "\n" +
                    "At normal flows (2.0-2.5m) the Gorge is like a wilderness run even though it is close to Upper Hutt (and Wellington). After putting in on the Pakuratahi River next to the Regional Park car park (toilets) and smiling at the tourists who flock to the LOTR filming sites, there is a km of class II-III. Once you get to the last (flume) bridge it is time to make sure everryone is \"OK\" because walking out after this point is difficult. Logs are common in the Gorge and scouting of blind drops is mandatory unless you have prior recent knowledge. The logs shift in every flood so don't be tricked into thinking you know the river from six months ago.\n" +
                    "\n" +
                    "After leaving the flume bridge the river flows around to the left where the first drop occurs. In 2007, this drop has no log in it and is really just a chute. The easy but fast water and drops continue for 300m before the river goes left. This leads to the site of a recent (October 2007) pinning and overnight stay due to logs in the main flow. The offending logs have since been trimmed but have not yet moved so this rapid is a mandatory scout and/or portage depending on flows. Above 2.0m there is a line on the right or if you feel lazy you can go over the log. Below 2.0m portage is the only option. This rapid is now called Westpac in recognition of 2 recent chopper rescues.\n" +
                    "\n" +
                    "Another 400m takes you through Anne's Drop (now small at normal flows) and down to the \"Weir\". This is a generally friendly river-wide log weir which is a great play spot for everyone. A great place to let off some steam and have a go.\n" +
                    "\n" +
                    "The river then turns right down several chutes to a big pool which ends at the confluence of Putaputa Stream (enters on right) where there is a sharp left turn. The gradient steepens with a small drop then onto the rock gardens and after a right turn you enter the Chicayne Rapid. The usual line is left of the big rock in the middle. After this chute it's time to eddy out and do your best to scout the Log Drop. This is a riverwide rata log which lodged itself here about two years ago (2005?) and has created an extra degree of challenge for newbies on the Gorge. At low flow the best option is to portage on river right. At 2.0m it is not practical to portage and best to run it middle left. At high flows there is a car-eating hole on river left so you need to keep right!\n" +
                    "\n" +
                    "There is a big pool after the Log Drop then comes the Toilet Bowl - a steep tongue on river right is the easiest route or if you feel like a harder line go left.\n" +
                    "\n" +
                    "From here is roughly 2km of easy class III with some play spots which switch on and off depending on the flow. In here is a nice class III drop called Pinball? which occurs on a left hand bend and is just above a play spot we used to call Woodies Wave?\n" +
                    "\n" +
                    "Another 500m and you are at the half way point. The grade from here on is easy class III but log danger can change this of course. After Kororipo Stream (enters right) the grade drops off even more until the last half hour or so is easy class II.\n" +
                    "\n" +
                    "At flows above 2.1m the second half of the run is still good fun.\n" +
                    "\n" +
                    "Take out when you get to the car park. There is a big pool where kayakers gather before running the next (learners) section called the Twin Lakes run.\n" +
                    "\n" +
                    "The Hutt Gorge contains a variety of drops and waves. It is wisest to run it on a dropping river as the narrow gorge can fill up quickly and the grade can change appropriately. The scenery is awesome. Every time we take a new paddler down they can't believe this is all so close to Wellington. But of course it is rain dependent so be prepared to sacrifice something to get to the Gorge!", new LatLng(-41.056314, 175.193809), R.drawable.section_hutt_gorge)
    };

    private Sections() {
    }

    public static Optional<Section> find(int id) {
        for (Section section : MOCKED_SECTIONS) {
            if (section.getId() == id) {
                return Optional.of(section);
            }
        }

        return Optional.absent();
    }
}
