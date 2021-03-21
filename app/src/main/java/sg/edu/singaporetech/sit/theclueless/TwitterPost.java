package sg.edu.singaporetech.sit.theclueless;

import java.io.Serializable;
/**
 * This is a TwitterPost Class used to get data from Firestore
 * @author The Clueless
 */
public class TwitterPost implements Serializable {
    private String tweet;
    private int likes;
    private String created_date;

    /**
     * This is a default constructor
     */
    public TwitterPost() {

    }

    /**
     * A constructor to create a new item
     * @param tweet The tweet of a single post
     * @param likes The amount of likes for the single post
     */
    public TwitterPost(String tweet, int likes) {
        this.tweet = tweet;
        this.likes = likes;
    }

    /**
     * Get the tweet post
     * @return the tweet in String
     */
    public String getTweet() {
        return tweet;
    }

    /**
     * Get the total number of likes for the tweet post
     * @return the total number of likes in integer
     */
    public float getLikes() { return likes; }

    /**
     * Get the date and time the tweet was created
     * @return the date and time in String
     */
    public String getCreated_date() {
        return created_date;
    }

}