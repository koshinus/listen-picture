package com.listen_picture;

import de.voidplus.soundcloud.SoundCloud;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by vadim on 25.01.17.
 */
public class SoundCloudLoader
{
    private static final String CLIENT_ID     = "dzKpRvB2UoL21eGOR2zbjwpmjwskebGR";
    private static final String CLIENT_SECRET = "nGYoiQdm4c5YSH00x0irdTCwdidKX0d6";
    //private static final String name = "Rima Ahmed";

    static SoundCloud sc = new SoundCloud(CLIENT_ID, CLIENT_SECRET);

    public static void main(String[] args)
    {
        getTracks();//name);
    }

    public static ArrayList<String> getTracks()//String username)
    {
        return sc.getTracks(0, 10)
                .stream()
                .map(t -> "https://api.soundcloud.com/tracks/" +
                            t.getId() + "/stream?client_id=" + CLIENT_ID)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
