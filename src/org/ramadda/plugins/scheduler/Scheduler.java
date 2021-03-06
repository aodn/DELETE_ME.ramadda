/*
* Copyright 2008-2012 Jeff McWhirter/ramadda.org Don Murray/CU-CIRES
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this 
* software and associated documentation files (the "Software"), to deal in the Software 
* without restriction, including without limitation the rights to use, copy, modify, 
* merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
* permit persons to whom the Software is furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all copies 
* or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
* PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
* OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
* DEALINGS IN THE SOFTWARE.
*/

package org.ramadda.plugins.scheduler;


import org.ramadda.repository.*;
import org.ramadda.repository.auth.*;
import org.ramadda.repository.harvester.*;
import org.ramadda.repository.search.*;
import org.ramadda.repository.type.*;

import org.ramadda.util.HtmlUtils;
import org.ramadda.util.HtmlUtils;

import org.w3c.dom.*;

import ucar.unidata.util.IOUtil;


import ucar.unidata.util.Misc;
import ucar.unidata.util.StringUtil;
import ucar.unidata.xml.XmlUtil;


import java.io.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.List;
import java.util.regex.*;


/**
 * Provides a top-level API
 *
 */
public class Scheduler extends RepositoryManager implements RequestHandler {
    public static final String ARG_WEEKS = "weeks";
    public static final String ARG_NUMPLAYERS = "numplayers";
    public static final String ARG_PLAYERSPERGAME = "playerspergame";
    public static final String ARG_NAMES = "names";
    public static final String ARG_SEED = "seed";
    public static final String ARG_MAXWITHOUT = "maxwithout";
    public static final String ARG_ASCSV = "ascsv";

    /**
     *     ctor
     *    
     *     @param repository the repository
     *     @param node xml from api.xml
     *     @param props propertiesn
     *    
     *     @throws Exception on badness
     */
    public Scheduler(Repository repository) throws Exception {
        super(repository);
    }




    /**
     * handle the request
     *
     * @param request request
     *
     * @return result
     *
     * @throws Exception on badness
     */
    public Result processScheduleRequest(Request request) throws Exception {
        boolean asCsv = request.get(ARG_ASCSV,false);
        StringBuffer sb = new StringBuffer();
        String       base   = getRepository().getUrlBase();
        if(!request.defined(ARG_SEED)) {
            sb.append(msgHeader("Scheduler"));
            sb.append(HtmlUtils.form(base+"/scheduler/schedule"));
            sb.append(HtmlUtils.formTable());
            sb.append(HtmlUtils.formEntry(msgLabel("# Weeks"),HtmlUtils.input(ARG_WEEKS,request.getString(ARG_WEEKS,"12"), HtmlUtils.SIZE_6)));
            sb.append(HtmlUtils.formEntry(msgLabel("# Players"),HtmlUtils.input(ARG_NUMPLAYERS,request.getString(ARG_NUMPLAYERS, "8"), HtmlUtils.SIZE_6)));
            sb.append(HtmlUtils.formEntry(msgLabel("Or enter names"),HtmlUtils.textArea(ARG_NAMES,request.getString(ARG_NAMES, ""), 8,40)));
            sb.append(HtmlUtils.formEntry(msgLabel("Players per week"),HtmlUtils.input(ARG_PLAYERSPERGAME,request.getString(ARG_PLAYERSPERGAME, "4"), HtmlUtils.SIZE_6)));

            sb.append(HtmlUtils.formEntry("",HtmlUtils.submit("Generate Schedule")));
            sb.append(HtmlUtils.formTableClose());
            sb.append(HtmlUtils.formClose());
        }


        if(request.defined(ARG_WEEKS)) {
            List<Player> players = new ArrayList<Player>();
            if(request.defined(ARG_NAMES)) {
                for(String line: StringUtil.split(request.getString(ARG_NAMES,""),"\n", true,true)) {
                    players.add(new Player(line));
                }
            } else {
                int numPlayers = request.get(ARG_NUMPLAYERS,8);
                for(int i=0;i<numPlayers;i++) {
                    players.add(new Player("Player " + (i+1)));
                }
            }

            Random random;
            long seed;
            String url;
            if(request.defined(ARG_SEED)) {
                random = new Random(seed = (long) request.get(ARG_SEED,0));
            } else {
                seed  = (long)(Math.random()*100000);
                request.put(ARG_SEED,""+seed);
                random = new Random(seed);
                sb.append(HtmlUtils.href(request.getUrl(),"Link to these results"));
            }
            schedule(sb, players, request.get(ARG_WEEKS,0), request.get(ARG_PLAYERSPERGAME,4), random, asCsv);
        }
        return new Result("Scheduler", sb);
    }

    public void schedule(StringBuffer sb, List<Player> players, int weeks, int playersPerGame, Random random, boolean asCsv) {
        if(!asCsv) {
            sb.append("<pre>");
        }
        for(int week=0;week<weeks;week++) {
            if(!asCsv) {
                sb.append("Week " + (week+1));
                sb.append("\n");
            }
            schedule(week, players, playersPerGame, random);
            for(Player player: players) {
                if(player.playing) {
                    for(Player otherPlayer: players) {
                        if(!otherPlayer.equals(player)) {
                            otherPlayer.yourPlayingWith(player);
                            player.yourPlayingWith(otherPlayer);
                        }
                    }
                    if(!asCsv) {
                        sb.append("\t"+ player);
                        sb.append("\n");
                    } else {
                    }
                }
            }
        }

        if(!asCsv) {
            for(Player player: players) {
                if(player.playedWith.size()!= players.size()-1) {
                    sb.append ("Player:" + player +" not played with all players:" + player.playedWith);
                    sb.append("\n");
                }
            }
            sb.append("</pre>");
            sb.append("\nSummary\n");
            sb.append("<table cellpadding=4 cellspacing=0 border=1><tr><td>&nbsp;</td><td align=center colspan=" + weeks+"><b>Weeks</b></td></tr>");
            sb.append("<tr><td>&nbsp;</td>");
            for (int i=0;i<weeks;i++) {
                sb.append("<td>");
                if(i<10)
                    sb.append("&nbsp;");
                sb.append(""+(i+1));
                sb.append("</td>");
            }

            for(Player player: players) {
                sb.append("<tr><td>");
                sb.append(player.toString());
                sb.append("</td>");
                for (int i=0;i<weeks;i++) {
                    if(player.isPlaying(i)) {
                        sb.append("<td bgcolor=#888>&nbsp;</td>");
                    } else {
                        sb.append("<td>&nbsp;</td>");
                    }
                }
                //                sb.append(StringUtil.join(" ", player.games));
                //                sb.append("</td></tr>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }

    }




    public  void schedule(int week, List<Player> players, int playersPerGame, Random random) {
        List<Player> myPlayers = new ArrayList<Player>(players);
        int maxNumberOfGamesPlayed = 0;
        for(Player player: myPlayers) { 
            player.playing = false;
            maxNumberOfGamesPlayed = Math.max(player.numGamesPlayed, maxNumberOfGamesPlayed);
        }
        int selectedCnt = 0;
        //        System.err.println("week:" + week);
        for(Player player: players) { 
            int weeksSincePlayed = week-player.lastPlayed;
            if(weeksSincePlayed>2) {
                //                System.err.println("  adding:" + player +" " + player.lastPlayed);
                player.setPlaying(week, true);
                myPlayers.remove(player);
                if(selectedCnt++>=playersPerGame) break;
            }
        }
        if(selectedCnt>=playersPerGame) return;

        List<Player> playersToPickFrom = new ArrayList<Player>();
        for(Player player: myPlayers) { 
            int weeksSincePlayed = week-player.lastPlayed;
            playersToPickFrom.add(player);
            if(weeksSincePlayed==1) continue;
            playersToPickFrom.add(player);
            if(weeksSincePlayed==2) continue;
            playersToPickFrom.add(player);
        }

        for(Player player: myPlayers) { 
            int diff = maxNumberOfGamesPlayed - player.numGamesPlayed;
            while(diff>0) {
                //System.err.println("weighting " + player + " max=" +maxNumberOfGamesPlayed +"   played:" +player.numGamesPlayed);
                playersToPickFrom.add(player);
                playersToPickFrom.add(player);
                diff--;
            }
        }


        //        System.err.println("pick:" + playersToPickFrom);
        while(selectedCnt<playersPerGame) {
            int item = random.nextInt(playersToPickFrom.size());
            Player player = playersToPickFrom.get(item);
            while(playersToPickFrom.remove(player)) {
            }
            player.setPlaying(week, true);
            selectedCnt++;
        }
    }



    public static class Player {
        
        String name;
        boolean playing = false;
        int lastPlayed=-1;
        HashSet<Player> playedWith = new HashSet<Player>();
        int numGamesPlayed = 0;
        List<Integer> games = new ArrayList<Integer>();
        HashSet<Integer> gamesPlayed = new HashSet<Integer>();

        public Player(String name) {
            this.name = name;
        }


        public void setPlaying(int week, boolean playing) {
            lastPlayed = week;
            this.playing  = playing;
            numGamesPlayed ++;
            games.add(week+1);
            gamesPlayed.add(week+1);
        }

        public boolean isPlaying(int week) {
            return gamesPlayed.contains(week);
        }

        public void yourPlayingWith(Player player) {
            playedWith.add(player);
        }

        public int hashCode() {
            return name.hashCode();
        }


        public boolean equals(Object o) {
            if(!(o instanceof Player)) return false;
            Player that = (Player) o;
            return that.name.equals(this.name);
        }

        public String toString() {
            return name;
        }

    }



}
