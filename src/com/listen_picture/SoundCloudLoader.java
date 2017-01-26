package com.listen_picture;

import de.voidplus.soundcloud.SoundCloud;
import de.voidplus.soundcloud.User;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * Created by vadim on 25.01.17.
 */
public class SoundCloudLoader
{
    private static final String CLIENT_ID     = "dzKpRvB2UoL21eGOR2zbjwpmjwskebGR";
    private static final String CLIENT_SECRET = "nGYoiQdm4c5YSH00x0irdTCwdidKX0d6";
    private static final String name = "Rima Ahmed";

    SoundCloud sc = new SoundCloud(CLIENT_ID, CLIENT_SECRET);

    public static void main(String[] args)
    {
        SoundCloudLoader loader = new SoundCloudLoader();
        loader.getTracks(name);
    }

    public void getTracks(String username)
    {
        ArrayList<User> users = sc.findUser(username);
        System.out.println(users.get(0).getDescription());
        //ArrayList<String> urls1 = sc.getUser(sc.findUser(username).get(0).getId());
        List<String> urls = sc.getTracks(0, 10)
                .stream()
                .map(t -> "https://api.soundcloud.com/tracks/" +
                            t.getId() + "/stream?client_id=" + CLIENT_ID)
                .collect(toList());
        for (String url: urls) System.out.println(url);
        //for(Track track: tracks) System.out.println(track.toString());
        //ArrayList<Group> groups = sc.findGroup()
        //for(User user: users) System.out.println(user.toString());
    }
}
