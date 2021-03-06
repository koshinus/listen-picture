package com.listen_picture;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Track;
import gui.ListController;
import gui.MainController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.umass.lastfm.Track.search;
import static de.umass.lastfm.User.getRecentTracks;
import static de.umass.lastfm.User.getTopTracks;

/**
 * Created by vadim on 25.01.17.
 */
public class LastFMLoader
{
    private static final String LISTEN_PICTURE_KEY = "f81b6328bb1bdbbe2f9657eb3f083ef0";
    public static void main(String[] args)
    {
        Caller.getInstance().setUserAgent("listen-picture");
        //Collection<Track> tracks = getRecentTracks("sakamotocross", LISTEN_PICTURE_KEY).getPageResults();
        //for(Track track: tracks) System.out.println(track.toString());
        Collection<Track> tracks1 = Artist.getTopTracks("Death Grips", LISTEN_PICTURE_KEY);
        for(Track track: tracks1) System.out.println(track.toString());
        /*
        LastFMLoader lastFMLoader = new LastFMLoader();
        List<String> urls1 = lastFMLoader.getMusicURLs("sakamotocross");
        for(String url : urls1) System.out.println(url);
        List<String> urls2 = lastFMLoader.getMusicURLs("alwaysesoteric");
        for(String url : urls2) System.out.println(url);
        */
        //MainController controller = new MainController();
        //controller.startInitialLoading(urls2);
        //User = new User("sakamotocross", "http://www.last.fm/user/sakamotocross");
    }

    //template: http://www.last.fm/music/Name+of+artist/_/Name+of+song
    public List<String> getMusicURLs(String username)
    {
        Caller.getInstance().setUserAgent("listen-picture");
        return getRecentTracks(username, LISTEN_PICTURE_KEY).getPageResults()
                .stream()
                .map(t ->
                "http://www.last.fm/music/" +
                t.getArtist().replaceAll(" ", "+").replaceAll("'", "%27") + "/_/" +
                t.getName().replaceAll(" ", "+").replaceAll("'", "%27"))
                .collect(Collectors.toList());
    }
}
