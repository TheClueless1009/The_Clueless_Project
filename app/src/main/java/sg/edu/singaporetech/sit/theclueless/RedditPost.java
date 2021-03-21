package sg.edu.singaporetech.sit.theclueless;

import java.io.Serializable;
/**
 * This is a RedditPost Class for getting the Data in the firebase.
 * @author The Clueless
 **/
public class RedditPost implements Serializable {
    private String title;
    private String content;
    private int score;
    private int created_utc;

    /**
     * This is a default constructor.
     */

    public RedditPost(){

    }

    /**
     * A constructor to create new item.
     * @param title This is the title of a single post.
     * @param content This is the content of a single post.
     * @param score This is the number of upvotes of a single post.
     */

    public RedditPost(String title, String content, int score) {
        this.title = title;
        this.content = content;
        this.score = score;
    }

    /**
     * Get the title.
     * @return the title of the post.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the content.
     * @return the content of the post.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the score.
     * @return the number of upvotes for the post.
     */
    public int getScore() {
        return score;
    }

    /**
     * Get the time that the post was created.
     * @return the time the post was created and posted in UTC timing stored as in integer.
     */
    public int getCreated_utc() {
        return this.created_utc;
    }

}
