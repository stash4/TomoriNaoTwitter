package TomoriNaoTwitter;

import twitter4j.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TomoriUserStream extends UserStreamAdapter {
    private Twitter twitter;
    private String[] tomoriPictureURL;
    private Random random;

    public TomoriUserStream(){
        this.twitter = new TwitterFactory().getInstance();

        String filename = "pictureURL.txt";
        ArrayList<String> tmpArray = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String tmp;
            while ((tmp = br.readLine()) != null){
                tmpArray.add(tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tomoriPictureURL = tmpArray.toArray(new String[0]);
        this.random = new Random();

    }

    //ツイート取得時
    @Override
    public void onStatus(Status status) {
        String tweet = status.getText();
        //RTを除外
        if(!tweet.matches(".*RT @.*")) {
            System.out.println(status.getUser().getName() + " : " + tweet);
            if (tweet.matches(".*にゃーん.*")) {
                try {
                    //画像をランダムに投げる
                    this.twitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName()
                            + " " + tomoriPictureURL[random.nextInt(tomoriPictureURL.length)]).inReplyToStatusId(status.getId()));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            } else if (tweet.matches(".*鯖プロ.*")) {
                try {
                    this.twitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " 進捗どうですか？").inReplyToStatusId(status.getId()));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //フォローされた時
    @Override
    public void onFollow(User source, User followedUser) {
        //フォローされた人
        System.out.println("source: " + source.getName());
        //フォローした人
        System.out.println("followedUser: " + followedUser.getName());

        if(!source.getScreenName().contains("TomoriNaoServer")) {
            try {
                this.twitter.createFriendship(source.getId());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public String[] getTomoriPictureURL() {
        return tomoriPictureURL;
    }

    public static void main(String[] args) {
        TomoriUserStream tus = new TomoriUserStream();
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tus);
        twitterStream.user();
    }
}
